package com.example.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filtro JWT mejorado con mejor manejo de errores y logging.
 * 
 * Cambios principales:
 * 1. Logging detallado en TODOS los pasos
 * 2. Dos estrategias: Cargar UserDetails O construir authorities del token
 * 3. Mejor manejo de excepciones con stacktrace
 * 4. Validación del token más robusta
 */
@Component
public class JwtAuthenticationFilter_MEJORADO extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter_MEJORADO.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter_MEJORADO(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Extraer header Authorization
            String authorizationHeader = request.getHeader("Authorization");
            String requestPath = request.getRequestURI();
            String method = request.getMethod();
            
            logger.debug("┌─ JwtAuthenticationFilter");
            logger.debug("│  Método: {} {}", method, requestPath);
            logger.debug("│  Authorization Header: {}", 
                authorizationHeader != null ? "✓ Presente" : "✗ Ausente");
            
            // 2. Si no hay header, continuar sin autenticación (permitir públicos)
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                logger.debug("│  → Sin token Bearer, permitiendo acceso público");
                logger.debug("└─");
                filterChain.doFilter(request, response);
                return;
            }
            
            // 3. Extraer token del Bearer string
            String token = jwtUtil.extractTokenFromBearerString(authorizationHeader);
            logger.debug("│  Token extraído: {}", token != null ? "✓ Sí" : "✗ No");
            
            if (token == null) {
                logger.warn("│  ✗ Fallo extrayendo token de: {}", authorizationHeader);
                logger.debug("└─");
                filterChain.doFilter(request, response);
                return;
            }
            
            // 4. Validar firma y expiración del token
            boolean isValid = jwtUtil.validateToken(token);
            boolean isExpired = jwtUtil.isTokenExpired(token);
            
            logger.debug("│  Token válido: {}", isValid ? "✓ Sí" : "✗ No");
            logger.debug("│  Token expirado: {}", isExpired ? "✓ Sí (expirado)" : "✗ No");
            
            if (!isValid || isExpired) {
                logger.warn("│  ✗ Token inválido o expirado. Válido: {}, Expirado: {}", isValid, isExpired);
                logger.debug("└─");
                filterChain.doFilter(request, response);
                return;
            }
            
            // 5. Extraer datos del token
            String username = jwtUtil.getUsernameFromToken(token);
            Set<String> rolesFromToken = jwtUtil.getRolesFromToken(token);
            
            logger.debug("│  Username: {}", username);
            logger.debug("│  Roles en token: {}", rolesFromToken);
            
            if (username == null) {
                logger.warn("│  ✗ No se pudo extraer username del token");
                logger.debug("└─");
                filterChain.doFilter(request, response);
                return;
            }
            
            // 6. Evitar sobrescribir autenticación existente
            if (SecurityContextHolder.getContext().getAuthentication() != null) {
                logger.debug("│  ⓘ Authentication ya existe en contexto, omitiendo");
                logger.debug("└─");
                filterChain.doFilter(request, response);
                return;
            }
            
            // 7. ESTRATEGIA A: Construir authorities desde el token
            // (Recomendado: No depende de que el usuario siga en BD)
            List<GrantedAuthority> authorities = buildAuthoritiesFromToken(rolesFromToken);
            logger.debug("│  Authorities construidas: {}", authorities);
            
            // 7b. Alternativa ESTRATEGIA B: Cargar UserDetails de BD
            // (Descomenta para usar)
            // List<GrantedAuthority> authorities = loadAuthoritiesFromDatabase(username);
            
            // 8. Crear y configurar token de autenticación
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            username,            // Principal
                            null,                // Credentials (null para JWT)
                            authorities);        // Authorities/Roles
            
            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
            
            // 9. Establecer autenticación en SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            logger.info("│  ✓ Autenticación JWT establecida para: {} con roles: {}", 
                username, authorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.joining(", ")));
            
            logger.debug("└─");
            
        } catch (Exception e) {
            logger.error("✗ Error CRÍTICO en JwtAuthenticationFilter: {}", e.getMessage(), e);
            // NO lanzar excepción, permitir que continúe sin autenticación
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * Estrategia A: Construir authorities directamente del JWT token.
     * Ventaja: No depende de que el usuario siga existiendo en la BD.
     * Desventaja: Los roles no se actualizan hasta que el token expire.
     */
    private List<GrantedAuthority> buildAuthoritiesFromToken(Set<String> rolesFromToken) {
        return rolesFromToken.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toList());
    }
    
    /**
     * Estrategia B: Cargar authorities desde la BD usando UserDetailsService.
     * Ventaja: Los roles siempre están actualizados.
     * Desventaja: Si el usuario es eliminado, falla.
     * 
     * Descomenta en doFilterInternal() para usar.
     */
    @SuppressWarnings("unused")
    private List<GrantedAuthority> loadAuthoritiesFromDatabase(String username) throws Exception {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            logger.debug("│  UserDetails cargado de BD. Authorities: {}", 
                userDetails.getAuthorities());
            return userDetails.getAuthorities().stream()
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("│  ✗ Error cargando UserDetails para {}: {}", username, e.getMessage());
            throw e;
        }
    }
}
