# 🚀 GUÍA DE IMPLEMENTACIÓN - Solucionar Error 403/500 en /api/profile/me

**Dificultad:** ⭐⭐⭐ Media  
**Tiempo estimado:** 15-30 minutos  
**Archivos a modificar:** 1 principal + 1 opcional  

---

## 📋 PASO 1: Entender el Problema (5 minutos)

### Síntomas
- ✓ Login funciona: `POST /api/auth/login` → retorna token
- ✗ Perfil falla: `GET /api/profile/me` → 403 Forbidden → 500 Error
- Error dice: "No static resource api/profile/me"

### Causa Raíz
El `JwtAuthenticationFilter` **no está extrayendo correctamente** el token o **no está configurando los roles** en el SecurityContext. Por eso, cuando Angular envía el token, Spring Security lo rechaza (403) y el DispatcherServlet intenta buscar un archivo estático (500).

---

## 📋 PASO 2: Reemplazar JwtAuthenticationFilter (10 minutos)

### Opción A: Usar el archivo mejorado (RECOMENDADO)

1. **Abre el archivo existente:**
   ```
   src/main/java/com/example/demo/security/JwtAuthenticationFilter.java
   ```

2. **Copia TODO el contenido del archivo mejorado:**
   ```
   src/main/java/com/example/demo/security/JwtAuthenticationFilter_MEJORADO.java
   ```

3. **Pégalo en el archivo original** (reemplaza TODO el contenido anterior)

4. **Elimina el archivo mejorado** (ya no lo necesitas):
   ```bash
   rm src/main/java/com/example/demo/security/JwtAuthenticationFilter_MEJORADO.java
   ```

### Opción B: Edición manual (si prefieres cambios mínimos)

**Archivo:** `JwtAuthenticationFilter.java`

Reemplaza solo el método `doFilterInternal()` con:

```java
@Override
protected void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {
    try {
        String authorizationHeader = request.getHeader("Authorization");
        logger.info("📌 JWT Filter - Authorization Header: {}", 
            authorizationHeader != null ? "✓ Present" : "✗ Missing");
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = jwtUtil.extractTokenFromBearerString(authorizationHeader);
            logger.info("📌 JWT Filter - Token extracted: {}", token != null ? "✓ Yes" : "✗ No");
            
            if (token != null && jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                Set<String> rolesFromToken = jwtUtil.getRolesFromToken(token);
                
                logger.info("📌 JWT Filter - Username: {}, Roles: {}", username, rolesFromToken);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // ✓ OPCIÓN 1: Usar roles del token (recomendado)
                    List<GrantedAuthority> authorities = rolesFromToken.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                    
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null,
                                    authorities);
                    
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("✓ JWT Authentication set for: {} with authorities: {}", 
                        username, authorities);
                }
            } else {
                logger.warn("✗ Token invalid or expired");
            }
        } else {
            logger.warn("✗ No Bearer token found");
        }
    } catch (Exception e) {
        logger.error("✗ Error in JwtAuthenticationFilter", e);
    }
    
    filterChain.doFilter(request, response);
}
```

**Imports necesarios a agregar:**
```java
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import java.util.List;
import java.util.stream.Collectors;
```

---

## 📋 PASO 3: Verificar UserDetailsServiceImpl (5 minutos)

**Archivo:** `src/main/java/com/example/demo/security/UserDetailsServiceImpl.java`

Asegúrate que implementa `getAuthorities()` correctamente:

```java
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        // ✓ IMPORTANTE: Los roles DEBEN tener el prefijo "ROLE_"
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toList());
        
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
```

---

## 📋 PASO 4: Mejorar ProfileController (Opcional pero recomendado)

**Archivo:** `src/main/java/com/example/demo/profiles/controller/ProfileController.java`

Agrega logging para validar que llega autenticado:

```java
@RestController
@RequestMapping({"/api/profile", "/api/profiles"})
public class ProfileController {
    
    private final ProfileService profileService;
    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponseDto> getMyProfile() {
        // ✓ Validar que la autenticación llegó correctamente
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        logger.info("═══════════════════════════════════════════");
        logger.info("GET /api/profile/me");
        logger.info("Authenticated: {}", auth != null && auth.isAuthenticated());
        if (auth != null) {
            logger.info("Principal: {}", auth.getPrincipal());
            logger.info("Authorities: {}", auth.getAuthorities());
        }
        logger.info("═══════════════════════════════════════════");
        
        return ResponseEntity.ok(profileService.getMyProfile());
    }

    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponseDto> updateMyProfile(@Valid @RequestBody UpdateProfileDto request) {
        return ResponseEntity.ok(profileService.updateMyProfile(request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponseDto> getProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(profileService.getProfileByUserId(userId));
    }
}
```

**Imports necesarios:**
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
```

---

## 📋 PASO 5: Rebuild y Test (5 minutos)

### 1. Compilar el proyecto

```bash
# Opción A: Con Gradle
./gradlew clean build

# Opción B: Con Maven
mvn clean install

# Opción C: En IDE (click derecho → Rebuild Project)
```

### 2. Ejecutar la aplicación

```bash
# Opción A: Docker Compose
docker-compose up --build

# Opción B: Local
./gradlew bootRun --args='--spring.profiles.active=dev'

# Opción C: JAR pre-compilado
java -jar build/libs/app.jar --spring.profiles.active=dev
```

### 3. Testear con cURL

```bash
# ===== PASO 1: LOGIN =====
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }' | jq .

# Espera respuesta:
# {
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "userId": 1,
#   "username": "john_doe",
#   "roles": ["USER"]
# }

# ===== PASO 2: GUARDA EL TOKEN =====
export TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# ===== PASO 3: ACCEDE A /api/profile/me =====
curl -X GET http://localhost:8080/api/profile/me \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" | jq .

# Espera código 200 con datos del perfil, NO 403 o 500
```

### 4. Ver los logs

```bash
# En Docker
docker-compose logs -f app | grep -E "JWT Filter|Authentication|GET /api/profile"

# En consola local
# Deberías ver algo como:
# 📌 JWT Filter - Authorization Header: ✓ Present
# 📌 JWT Filter - Token extracted: ✓ Yes
# 📌 JWT Filter - Username: john_doe, Roles: [USER]
# ✓ JWT Authentication set for: john_doe
# ═══════════════════════════════════════════
# GET /api/profile/me
# Authenticated: true
# Principal: john_doe
# Authorities: [ROLE_USER]
```

---

## 🧪 CHECKLIST DE VALIDACIÓN

Después de implementar, verifica esto:

- [ ] Proyecto compila sin errores
- [ ] Servidor inicia correctamente
- [ ] Login genera JWT token con roles
- [ ] Token tiene formato "Bearer {jwt}"
- [ ] JwtAuthenticationFilter extrae token correctamente (ver logs)
- [ ] Roles están como "ROLE_USER", "ROLE_PROGRAMADOR", etc
- [ ] GET /api/profile/me retorna 200 (NO 403 o 500)
- [ ] ProfileController recibe Authentication con roles
- [ ] SecurityContext está poblado en el controlador

---

## 🔧 TROUBLESHOOTING

### Problema 1: Aún veo 403 Forbidden

**Posible causa:** Token no llega al servidor

**Solución:**
```bash
# En cURL, verifica que estés usando el token correcto
echo $TOKEN  # ¿Tiene contenido?

# En Angular, revisa AuthInterceptor:
console.log("Token enviado:", token);  // ¿Tiene valor?

# Verifica header Authorization:
# Network tab → Headers → Authorization: Bearer ...
```

### Problema 2: 500 "No static resource"

**Posible causa:** Exception en JwtAuthenticationFilter no está siendo logeada

**Solución:**
```bash
# Asegúrate de que los logs estén en NIVEL DEBUG
# En application.yml:
logging:
  level:
    com.example.demo.security: DEBUG
    com.example.demo.profiles: DEBUG
```

### Problema 3: "User not found"

**Posible causa:** El usuario fue eliminado entre login y la request

**Solución:** Usar Estrategia A (roles del token) que NO depende de cargar el usuario de BD.

```java
// Ya está implementada en JwtAuthenticationFilter_MEJORADO.java
List<GrantedAuthority> authorities = buildAuthoritiesFromToken(rolesFromToken);
```

### Problema 4: Roles no aparecen en @PreAuthorize

**Posible causa:** Roles no tienen prefijo "ROLE_"

**Solución:** Asegurate que en JwtAuthenticationFilter hagas:
```java
new SimpleGrantedAuthority("ROLE_" + role)  // ← "ROLE_" obligatorio
```

---

## 📊 COMPARACIÓN ANTES vs DESPUÉS

### ANTES (Código original)

```java
// JwtAuthenticationFilter.java - Original
if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);  // ← Risk aquí
    
    UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(
                    userDetails,          // ← Usa UserDetails (puede ser null)
                    null,
                    userDetails.getAuthorities());  // ← Espera que getAuthorities() funcione
    
    // ...
}
```

**Problemas:**
- ❌ No hay logging
- ❌ Silencia excepciones
- ❌ Depende de UserDetailsService
- ❌ Si usuario es eliminado, falla

### DESPUÉS (Código mejorado)

```java
// JwtAuthenticationFilter.java - Mejorado
List<GrantedAuthority> authorities = rolesFromToken.stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
        .collect(Collectors.toList());

UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(
                username,        // ← String simple (no objet)
                null,
                authorities);    // ← Authorities construidas del token

logger.info("✓ JWT Authentication set for: {} with authorities: {}", username, authorities);
```

**Ventajas:**
- ✅ Logging detallado
- ✅ No depende de BD
- ✅ Manejo de excepciones mejor
- ✅ Más rápido
- ✅ Más seguro

---

## 🎯 SIGUIENTES PASOS

Una vez que `/api/profile/me` funcione:

1. **Testea otros endpoints protegidos:**
   ```bash
   GET /api/asesorias/my
   GET /api/availability/my
   POST /api/projects
   ```

2. **Configura tokens con mayor expiración si es necesario:**
   Edit `application.yml`:
   ```yaml
   jwt:
     secret: ${JWT_SECRET:dev-secret-key}
     expiration: 3600000  # 1 hora
   ```

3. **Implementa refresh tokens** (opcional):
   - Endpoint POST /api/auth/refresh
   - Retorna nuevo token cuando está por expirar

4. **Agrega rate limiting** (para producción):
   - Limita intentos de login
   - Protege contra fuerza bruta

---

## 📞 DIAGRAMA DE AYUDA RÁPIDA

```
¿Ves 403?
│
├─→ ¿Está el token en el header Authorization?
│   ├─ NO → Revisar Angular AuthInterceptor
│   └─ SÍ → Continúa
│
├─→ ¿El token es válido (sin caracteres basura)?
│   ├─ NO → Copiar token correctamente del login
│   └─ SÍ → Continúa
│
├─→ ¿Ves logs "JWT Filter"?
│   ├─ NO → El filtro no se ejecuta (añade logging)
│   └─ SÍ → Continúa
│
├─→ ¿Dice "✓ JWT Authentication set"?
│   ├─ NO → El token está invalido o expirado
│   └─ SÍ → Se debería autenticar
│
└─→ Si llegaste acá → Revisar @PreAuthorize en controller

¿Ves 500?
│
├─→ Revisa logs para ver la excepción completa
├─→ Usa Solución 1: Mejorar JwtAuthenticationFilter
└─→ Ejecuta de nuevo y lee los logs
```

---

Este es el plan completo. ¿Por dónde quieres empezar?
