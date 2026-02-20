package com.example.demo.asesorias.service;

import com.example.demo.asesorias.dto.CreateAsesoriaDto;
import com.example.demo.asesorias.dto.AsesoriaResponseDto;
import com.example.demo.asesorias.dto.AsesoriaMyRequestsResponseDto;
import com.example.demo.asesorias.dto.UpdateAsesoriaDto;
import com.example.demo.asesorias.dto.UpdateAsesoriaStatusDto;
import com.example.demo.asesorias.dto.CancelAsesoriaDto;
import com.example.demo.asesorias.entity.Asesoria;
import com.example.demo.asesorias.entity.AsesoriaStatus;
import com.example.demo.asesorias.mapper.AsesoriaMapper;
import com.example.demo.asesorias.repository.AsesoriaRepository;
import com.example.demo.roles.entity.RoleName;
import com.example.demo.security.SecurityUtils;
import com.example.demo.shared.exception.BadRequestException;
import com.example.demo.shared.exception.ForbiddenException;
import com.example.demo.shared.exception.ResourceNotFoundException;
import com.example.demo.users.entity.User;
import com.example.demo.users.repository.UserRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AsesoriaServiceImpl implements AsesoriaService {
    private final AsesoriaRepository asesoriaRepository;
    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public AsesoriaServiceImpl(AsesoriaRepository asesoriaRepository, 
                             UserRepository userRepository,
                             SecurityUtils securityUtils) {
        this.asesoriaRepository = asesoriaRepository;
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    @Override
    public List<AsesoriaResponseDto> listAll() {
        // ADMIN ve todo
        if (securityUtils.isAdmin()) {
            return asesoriaRepository.findAll().stream().map(AsesoriaMapper::toResponse).toList();
        }
        
        Long currentUserId = securityUtils.getCurrentUserId();
        
        // PROGRAMADOR ve asesorías donde él es programador
        if (securityUtils.isProgrammer()) {
            return asesoriaRepository.findByProgrammerId(currentUserId)
                .stream().map(AsesoriaMapper::toResponse).toList();
        }
        
        // USER ve asesorías donde él es cliente
        return asesoriaRepository.findByClientId(currentUserId)
            .stream().map(AsesoriaMapper::toResponse).toList();
    }

    @Override
    public AsesoriaMyRequestsResponseDto getMyRequests() {
        Long currentUserId = securityUtils.getCurrentUserId();
        
        // Obtener asesorías donde soy programador (received)
        List<AsesoriaResponseDto> received = asesoriaRepository.findByProgrammerId(currentUserId)
            .stream().map(AsesoriaMapper::toResponse).toList();
        
        // Obtener asesorías donde soy cliente (sent)
        List<AsesoriaResponseDto> sent = asesoriaRepository.findByClientId(currentUserId)
            .stream().map(AsesoriaMapper::toResponse).toList();
        
        return new AsesoriaMyRequestsResponseDto(sent, received);
    }

    @Override
    public List<AsesoriaResponseDto> listByProgrammer(Long programmerId) {
        // SOLO ADMIN puede acceder a este método
        if (!securityUtils.isAdmin()) {
            throw new ForbiddenException("Solo administradores pueden filtrar asesorías por programador");
        }
        
        return asesoriaRepository.findByProgrammerId(programmerId)
            .stream().map(AsesoriaMapper::toResponse).toList();
    }

    @Override
    public List<AsesoriaResponseDto> listByClient(Long clientId) {
        // SOLO ADMIN puede acceder a este método
        if (!securityUtils.isAdmin()) {
            throw new ForbiddenException("Solo administradores pueden filtrar asesorías por cliente");
        }
        
        return asesoriaRepository.findByClientId(clientId)
            .stream().map(AsesoriaMapper::toResponse).toList();
    }

    @Override
    public AsesoriaResponseDto getById(Long id) {
        Asesoria asesoria = asesoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asesoría no encontrada"));
        
        // Validar que el usuario actual es ADMIN o está involucrado
        assertAdminOrInvolved(asesoria);
        
        return AsesoriaMapper.toResponse(asesoria);
    }

    @Override
    @Transactional
    public AsesoriaResponseDto create(CreateAsesoriaDto request) {
        // El usuario actual siempre es el cliente
        User currentUser = securityUtils.getCurrentUser();
        
        // Validar y obtener el programador
        User programmer = userRepository.findById(request.programmerId())
            .orElseThrow(() -> new ResourceNotFoundException("Programador no encontrado"));
        
        // Validar que el programador tenga rol PROGRAMADOR
        if (!programmer.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleName.PROGRAMADOR)) {
            throw new BadRequestException("El usuario especificado no tiene el rol PROGRAMADOR");
        }
        
        // *** VALIDACIÓN CRÍTICA: Prohibir auto-asesoría (client == programmer) ***
        if (currentUser.getId().equals(programmer.getId())) {
            throw new BadRequestException("No puedes crear una asesoría donde el cliente y el programador son la misma persona");
        }
        
        // Crear la asesoría
        Asesoria asesoria = new Asesoria();
        asesoria.setStartAt(request.startAt());
        asesoria.setDurationMinutes(request.durationMinutes());
        asesoria.setModality(request.modality());
        asesoria.setTopic(request.topic());
        asesoria.setNotes(request.notes());
        asesoria.setClient(currentUser);
        asesoria.setProgrammer(programmer);
        asesoria.setStatus(AsesoriaStatus.PENDING);
        
        return AsesoriaMapper.toResponse(asesoriaRepository.save(asesoria));
    }

    @Override
    @Transactional
    public AsesoriaResponseDto update(Long id, UpdateAsesoriaDto request) {
        Asesoria asesoria = asesoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asesoría no encontrada"));
        
        Long currentUserId = securityUtils.getCurrentUserId();
        
        // ADMIN siempre puede actualizar
        if (!securityUtils.isAdmin()) {
            // USER (cliente) solo si es el cliente
            if (!asesoria.getClient().getId().equals(currentUserId)) {
                throw new ForbiddenException("Solo el cliente puede actualizar una asesoría");
            }
            
            // USER solo puede actualizar si status == PENDING
            if (asesoria.getStatus() != AsesoriaStatus.PENDING) {
                throw new BadRequestException(
                    "El cliente solo puede actualizar asesorías en estado PENDIENTE. Estado actual: " + asesoria.getStatus()
                );
            }
        }
        
        // Actualizar solo los campos permitidos
        if (request.startAt() != null) {
            asesoria.setStartAt(request.startAt());
        }
        if (request.durationMinutes() != null) {
            asesoria.setDurationMinutes(request.durationMinutes());
        }
        if (request.modality() != null) {
            asesoria.setModality(request.modality());
        }
        if (request.topic() != null) {
            asesoria.setTopic(request.topic());
        }
        if (request.notes() != null) {
            asesoria.setNotes(request.notes());
        }
        
        return AsesoriaMapper.toResponse(asesoriaRepository.save(asesoria));
    }

    @Override
    @Transactional
    public AsesoriaResponseDto updateStatus(Long id, UpdateAsesoriaStatusDto request) {
        Asesoria asesoria = asesoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asesoría no encontrada"));
        
        Long currentUserId = securityUtils.getCurrentUserId();
        AsesoriaStatus currentStatus = asesoria.getStatus();
        AsesoriaStatus newStatus = request.status();
        
        // Validar ownership: verificar que el usuario tiene permiso para cambiar el estado de esta asesoría
        // ADMIN siempre puede
        if (!securityUtils.isAdmin()) {
            // PROGRAMADOR solo si es el programador asignado
            if (securityUtils.isProgrammer()) {
                if (!asesoria.getProgrammer().getId().equals(currentUserId)) {
                    throw new BadRequestException("No tienes permiso para cambiar el estado de esta asesoría. Solo el programador asignado puede hacerlo.");
                }
            }
            // USER (cliente) solo si es el cliente
            else if (securityUtils.isUser()) {
                if (!asesoria.getClient().getId().equals(currentUserId)) {
                    throw new BadRequestException("No tienes permiso para cambiar el estado de esta asesoría. Solo el cliente propietario puede hacerlo.");
                }
            }
        }
        
        // Validar la transición de estado
        validateStateTransition(currentStatus, newStatus);
        
        // Actualizar el estado y setear responseMsg si corresponde
        AsesoriaMapper.updateStatus(request, asesoria);
        
        // Mantener el seteo de responseMsg solo para PROGRAMADOR o ADMIN
        if ((securityUtils.isProgrammer() || securityUtils.isAdmin()) && request.responseMsg() != null) {
            asesoria.setResponseMsg(request.responseMsg());
        }
        
        return AsesoriaMapper.toResponse(asesoriaRepository.save(asesoria));
    }

    /**
     * Valida si la transición de estado es permitida según las reglas de negocio.
     * Reglas:
     * - USER (cliente) puede cancelar SOLO si status=PENDING
     * - PROGRAMADOR puede APPROVE o REJECT SOLO si status=PENDING
     * - ADMIN puede cualquier transición
     * 
     * @param currentStatus estado actual de la asesoría
     * @param newStatus estado objetivo
     * @throws BadRequestException si la transición no es permitida
     */
    private void validateStateTransition(AsesoriaStatus currentStatus, AsesoriaStatus newStatus) {
        // ADMIN puede cualquier transición
        if (securityUtils.isAdmin()) {
            return;
        }

        // PROGRAMADOR
        if (securityUtils.isProgrammer()) {
            if (newStatus == AsesoriaStatus.APPROVED || newStatus == AsesoriaStatus.REJECTED) {
                if (currentStatus != AsesoriaStatus.PENDING) {
                    throw new BadRequestException(
                        "PROGRAMADOR solo puede APROBAR o RECHAZAR si el estado actual es PENDING. Estado actual: " + currentStatus
                    );
                }
            } else {
                throw new BadRequestException(
                    "PROGRAMADOR solo puede cambiar el estado a APPROVED o REJECTED. Se intentó cambiar a: " + newStatus
                );
            }
        }

        // USER (cliente)
        else if (securityUtils.isUser()) {
            if (newStatus == AsesoriaStatus.CANCELLED) {
                if (currentStatus != AsesoriaStatus.PENDING) {
                    throw new BadRequestException(
                        "USER (cliente) solo puede cancelar si el estado actual es PENDING. Estado actual: " + currentStatus
                    );
                }
            } else {
                throw new BadRequestException(
                    "USER (cliente) solo puede cancelar una asesoría (cambiar a CANCELLED). Se intentó cambiar a: " + newStatus
                );
            }
        }

        // Otros roles (si los hay)
        else {
            throw new BadRequestException("No tienes permisos para cambiar el estado de una asesoría");
        }
    }

    @Override
    @Transactional
    public AsesoriaResponseDto cancel(Long id, CancelAsesoriaDto request) {
        Asesoria asesoria = asesoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asesoría no encontrada"));
        
        Long currentUserId = securityUtils.getCurrentUserId();
        
        // Validar que sea el cliente (owner)
        if (!asesoria.getClient().getId().equals(currentUserId)) {
            throw new ForbiddenException("Solo el cliente puede cancelar una asesoría");
        }
        
        // Validar que el status sea PENDING
        if (asesoria.getStatus() != AsesoriaStatus.PENDING) {
            throw new BadRequestException(
                "Solo puedes cancelar asesorías en estado PENDING. Estado actual: " + asesoria.getStatus()
            );
        }
        
        // Cambiar a CANCELLED
        asesoria.setStatus(AsesoriaStatus.CANCELLED);
        
        // Guardar responseMsg si está presente
        if (request.responseMsg() != null) {
            asesoria.setResponseMsg(request.responseMsg());
        }
        
        return AsesoriaMapper.toResponse(asesoriaRepository.save(asesoria));
    }

    @Transactional
    public AsesoriaResponseDto respondAsProgrammer(Long id, UpdateAsesoriaStatusDto request) {
        Asesoria asesoria = asesoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asesoría no encontrada"));
        
        Long currentUserId = securityUtils.getCurrentUserId();
        
        // Verificar que sea el programador asignado
        if (!asesoria.getProgrammer().getId().equals(currentUserId)) {
            throw new ForbiddenException("Solo el programador asignado puede responder esta asesoría");
        }
        
        // Verificar que el estado sea PENDING
        if (asesoria.getStatus() != AsesoriaStatus.PENDING) {
            throw new BadRequestException(
                "Solo puedes responder asesorías en estado PENDIENTE. Estado actual: " + asesoria.getStatus()
            );
        }
        
        // Validar que intente cambiar a APPROVED o REJECTED
        AsesoriaStatus newStatus = request.status();
        if (newStatus != AsesoriaStatus.APPROVED && newStatus != AsesoriaStatus.REJECTED) {
            throw new BadRequestException(
                "El programador solo puede responder con APROBADA o RECHAZADA. Se intentó: " + newStatus
            );
        }
        
        // Aplicar la actualización
        AsesoriaMapper.updateStatus(request, asesoria);
        return AsesoriaMapper.toResponse(asesoriaRepository.save(asesoria));
    }

    /**
     * Verifica que el usuario actual es ADMIN o está involucrado en la asesoría
     * (como programador o cliente).
     * 
     * @param asesoria la asesoría a validar
     * @throws ForbiddenException si el usuario no tiene acceso
     */
    private void assertAdminOrInvolved(Asesoria asesoria) {
        // ADMIN siempre tiene acceso
        if (securityUtils.isAdmin()) {
            return;
        }
        
        Long currentUserId = securityUtils.getCurrentUserId();
        
        // Verificar que sea programador o cliente
        boolean isProgrammer = asesoria.getProgrammer().getId().equals(currentUserId);
        boolean isClient = asesoria.getClient().getId().equals(currentUserId);
        
        if (!isProgrammer && !isClient) {
            throw new ForbiddenException("No tienes permisos para acceder a esta asesoría. Solo ADMIN, el programador asignado o el cliente pueden acceder.");
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Asesoria asesoria = asesoriaRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Asesoría no encontrada"));
        
        Long currentUserId = securityUtils.getCurrentUserId();
        
        // ADMIN siempre puede eliminar
        if (securityUtils.isAdmin()) {
            asesoriaRepository.deleteById(id);
            return;
        }
        
        // USER (cliente) solo si es cliente y status == PENDING
        if (securityUtils.isUser()) {
            if (!asesoria.getClient().getId().equals(currentUserId)) {
                throw new ForbiddenException("Solo puedes eliminar tus propias asesorías como cliente");
            }
            if (asesoria.getStatus() != AsesoriaStatus.PENDING) {
                throw new BadRequestException(
                    "El cliente solo puede eliminar asesorías en estado PENDING. Estado actual: " + asesoria.getStatus()
                );
            }
            asesoriaRepository.deleteById(id);
            return;
        }
        
        // PROGRAMADOR solo si es programador (sin restricción de estado)
        if (securityUtils.isProgrammer()) {
            if (!asesoria.getProgrammer().getId().equals(currentUserId)) {
                throw new ForbiddenException("Solo puedes eliminar asesorías donde eres el programador");
            }
            asesoriaRepository.deleteById(id);
            return;
        }
        
        throw new ForbiddenException("No tienes permiso para eliminar esta asesoría");
    }
}
