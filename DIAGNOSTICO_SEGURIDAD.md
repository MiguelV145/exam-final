# 🔐 DIAGNÓSTICO COMPLETO - Error 403/500 en GET /api/profile/me

**Fecha:** 10 de Febrero de 2026  
**Problema:** 403 Forbidden → 500 Error "No static resource api/profile/me"  
**Endpoint:** `GET /api/profile/me`  

---

## 📋 Análisis del Problema

### ¿Por qué Spring Boot busca un "static resource"?

El error **"No static resource api/profile/me"** ocurre cuando:

1. **Spring Security rechaza la solicitud (403)** antes de que llegue al controlador
2. **Spring intenta servir la ruta como recurso estático** (fallback)
3. **Cuando no encuentra el archivo, retorna 500**

```
Solicitud HTTP
      ↓
┌─────────────────────────────────────────┐
│  Spring Security Filter Chain           │
│  ├─ CORS (OPTIONS OK)                   │
│  ├─ JWT Authentication Filter           │ ← ¿TOKEN LLEGANDO?
│  └─ Authorization Check (@PreAuthorize) │ ← ¿ROLES OK?
└────────────────────┬────────────────────┘
                     │
            ✓ OK → Controller
            ✗ REJECT → Static Resource Handler (ERROR 500)
```

---

## 🔍 Problemas Identificados en el Código

### 1. ⚠️ CRÍTICO: Problema en SecurityConfig - Rutas Sin /api/profile

**Archivo:** `SecurityConfig.java` línea 48-51

```java
.requestMatchers(HttpMethod.GET, "/api/status/**").permitAll()
.requestMatchers(HttpMethod.GET, "/api/portfolios/**").permitAll()  
.requestMatchers(HttpMethod.GET, "/api/projects/public").permitAll()

.anyRequest().authenticated()  // ← CUALQUIER OTRA RUTA REQUIERE AUTENTICACIÓN
```

**Problema:**
- `/api/profile/**` NO está en la lista de rutas públicas
- Pero tampoco está explícitamente permitido para usuarios autenticados
- Depende 100% del JWT token siendo validado correctamente

**Checklist:**
- ✅ El endpoint `/api/profile/me` existe en `ProfileController.java`
- ❓ ¿Llega el JWT token al filtro?
- ❓ ¿Se extrae correctamente el username del token?
- ❓ ¿Se cargan los roles correctamente?

---

### 2. ⚠️ CRÍTICO: Posible Problema en JwtAuthenticationFilter

**Archivo:** `JwtAuthenticationFilter.java` línea 38-60

```java
@Override
protected void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {
    try {
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = jwtUtil.extractTokenFromBearerString(authorizationHeader);
            
            if (token != null && jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());  // ← AQUÍ SE CARGAN LOS ROLES
                    
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        }
    } catch (Exception e) {
        logger.error("No se pudo establecer la autenticación de usuario", e);  // ← SILENCIA ERRORES
    }
    
    filterChain.doFilter(request, response);
}
```

**Problemas Identificados:**

1. **El catch silencia excepciones** - Sin stacktrace, no sabemos qué falla
2. **UserDetailsService.loadUserByUsername()** - ¿Retorna los roles correctamente?
3. **userDetails.getAuthorities()** - ¿Están en formato "ROLE_xxx"?

---

### 3. ⚠️ PROBABLE: UserDetailsServiceImpl No Retorna Roles

**Necesitamos verificar:** `UserDetailsServiceImpl.java`

```java
// ¿Está implementando correctamente getAuthorities()?
// ¿Los roles vienen con "ROLE_" prefix?
// ¿El usuario del token existe en la BD?
```

---

### 4. ⚠️ PROBABLE: Token No Contiene Roles

**Archivo:** En el login, se generan los roles:

```java
// Obtener roles
Set<String> roles = user.getRoles().stream()
    .map(role -> role.getName().name())  // ← Retorna "ADMIN", "PROGRAMADOR", "USER"
    .collect(Collectors.toSet());

// Generar JWT
String token = jwtUtil.generateToken(user.getId(), user.getUsername(), roles);
```

**En JwtUtil.java:**
```java
.claim("roles", roles)  // ← Se guarda en el token
```

**Pero en JwtAuthenticationFilter:**
```java
UserDetails userDetails = userDetailsService.loadUserByUsername(username);
// ¿Carga los roles de la BD o solo del nombre de usuario?
// ¿Si el usuario fue eliminado de la BD, falla?
```

---

## ✅ SOLUCIONES RECOMENDADAS

### Solución 1: Mejorar el JwtAuthenticationFilter (Logging)

```java
@Override
protected void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {
    try {
        String authorizationHeader = request.getHeader("Authorization");
        logger.info("Authorization Header: {}", authorizationHeader);
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = jwtUtil.extractTokenFromBearerString(authorizationHeader);
            logger.info("Token extraído: {}", token != null ? "✓ Sí" : "✗ No");
            
            if (token != null && jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                logger.info("Username del token: {}", username);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    logger.info("UserDetails cargado. Authorities: {}", userDetails.getAuthorities());
                    
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities());
                    
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("✓ Autenticación establecida para: {}", username);
                }
            } else {
                logger.warn("✗ Token inválido o expirado");
            }
        } else {
            logger.warn("✗ Header Authorization no encontrado o sin 'Bearer '");
        }
    } catch (Exception e) {
        logger.error("✗ Error en JwtAuthenticationFilter", e);  // ← AHORA VEMOS EL STACKTRACE
    }
    
    filterChain.doFilter(request, response);
}
```

---

### Solución 2: Usar Roles desde el JWT Token

Si deseas no depender de cargar UserDetails desde BD:

```java
// JwtAuthenticationFilter.java mejorado
@Override
protected void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {
    try {
        String authorizationHeader = request.getHeader("Authorization");
        
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = jwtUtil.extractTokenFromBearerString(authorizationHeader);
            
            if (token != null && jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
                String username = jwtUtil.getUsernameFromToken(token);
                Set<String> rolesFromToken = jwtUtil.getRolesFromToken(token);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Criar authorities desde el token en lugar de cargar de BD
                    List<GrantedAuthority> authorities = rolesFromToken.stream()
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .collect(Collectors.toList());
                    
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    username,     // Principal
                                    null,         // Credentials
                                    authorities); // Authorities del token
                    
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    logger.info("✓ Autenticación JWT establecida para: {} con roles: {}", username, rolesFromToken);
                }
            }
        }
    } catch (Exception e) {
        logger.error("✗ Error en JwtAuthenticationFilter", e);
    }
    
    filterChain.doFilter(request, response);
}
```

**Necesitarías agregar imports:**
```java
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import java.util.List;
import java.util.stream.Collectors;
```

---

### Solución 3: Verificar UserDetailsServiceImpl

**Crea este archivo para validar:**

```java
// UserDetailsServiceImpl.java debe implementar correctamente
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
        
        // ✓ IMPORTANTE: Asegurar que los roles tienen el prefijo "ROLE_"
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toList());
        
        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)  // ← CON ROLES
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}
```

---

### Solución 4: Agregar logging detallado en ProfileController

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        logger.info("=== GET /api/profile/me ===");
        logger.info("Authenticated: {}", auth != null && auth.isAuthenticated());
        logger.info("Principal: {}", auth != null ? auth.getPrincipal() : "null");
        logger.info("Authorities: {}", auth != null ? auth.getAuthorities() : "null");
        
        return ResponseEntity.ok(profileService.getMyProfile());
    }
    
    // ... resto del controlador
}
```

---

## 📝 Frontend (Angular) - Validación del AuthInterceptor

Asume que tu interceptor está correctamente configured:

```typescript
// auth.interceptor.ts (Angular)
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthService) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = this.authService.getToken();  // Obtener del localStorage
    
    logger.info("Token enviado: ", token);  // ← VERIFICAR QUE EXISTE
    
    if (token) {
      const clonedRequest = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`  // ← FORMATO CORRECTO
        }
      });
      return next.handle(clonedRequest);
    }
    return next.handle(req);
  }
}
```

---

## 🧪 PRUEBAS PARA VALIDAR

### Test 1: Verificar que el login retorna token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "username": "john_doe",
  "email": "user@example.com",
  "roles": ["USER", "PROGRAMADOR"]  // ← DEBE TENER ROLES
}
```

### Test 2: Usar token en endpoint protegido

```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:8080/api/profile/me \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Test 3: Verificar logs en consola

Deberías ver:
```
INFO: Authorization Header: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
INFO: Token extraído: ✓ Sí
INFO: Username del token: john_doe
INFO: UserDetails cargado. Authorities: [ROLE_USER, ROLE_PROGRAMADOR]
INFO: ✓ Autenticación establecida para: john_doe
INFO: === GET /api/profile/me ===
INFO: Authenticated: true
INFO: Principal: john_doe
INFO: Authorities: [ROLE_USER, ROLE_PROGRAMADOR]
```

---

## 🔧 Orden de Implementación

1. **Primero:** Implementar Solución 1 (Mejorar logging)
2. **Ejecutar y revisar:** Los logs en consola
3. **Si falla en cargar UserDetails:** Implementar Solución 2 (Usar roles del JWT)
4. **Verificar UserDetailsServiceImpl:** Asegura que retorna roles con "ROLE_" prefix (Solución 3)
5. **Testear desde cero:** Login → GET /api/profile/me
6. **Si aún falla:** Revisar Angular AuthInterceptor (Solución 4)

---

## 📊 Diagrama de Flujo Correcto

```
┌─────────────────────────────────────────────────────┐
│  1. Angular POST /api/auth/login                    │
│     ↓ {email, password}                             │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  2. AuthServiceImpl.login()                          │
│     ├─ Buscar user por email                        │
│     ├─ Validar password                             │
│     ├─ Obtener roles de user.getRoles()             │
│     └─ Generar JWT con roles como claim             │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  3. LoginResponseDto retorna token + roles          │
│     ↓ {token, userId, username, roles}             │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  4. Angular guarda token en localStorage            │
│     localStorage.setItem('token', response.token)   │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  5. Angular GET /api/profile/me                     │
│     ├─ AuthInterceptor intercepta                   │
│     ├─ Adjunta: Authorization: Bearer {token}       │
│     └─ Envía request                                │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  6. Spring Security Chain                           │
│     ├─ CORS Filter: ✓ OK                            │
│     ├─ JwtAuthenticationFilter:                     │
│     │  ├─ Lee header Authorization: Bearer ...      │
│     │  ├─ Extrae token                              │
│     │  ├─ Valida signature                          │
│     │  ├─ Verifica no expirado                      │
│     │  ├─ Extrae username                           │
│     │  ├─ Carga UserDetails (con roles)             │
│     │  └─ SetAuthentication en SecurityContext      │
│     └─ AuthorizationFilter: @PreAuthorize check     │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  7. ProfileController.getMyProfile()                │
│     ├─ Usa SecurityUtils.getCurrentUserId()         │
│     ├─ Llama profileService.getMyProfile()          │
│     └─ Retorna ProfileResponseDto                   │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│  8. Angular recibe perfil                           │
│     ✓ 200 OK con datos del perfil                   │
└─────────────────────────────────────────────────────┘
```

---

## ⚡ RESUMEN EJECUTIVO

| Problema | Causa Probable | Solución |
|----------|---|---|
| **403 Forbidden** | Token no llega correcto | Verificar AuthInterceptor en Angular |
| **500 "No static resource"** | Autenticación falla, Spring busca archivo estático | Implementar Solución 1 (logging) |
| **Roles no se cargan** | UserDetailsServiceImpl no retorna authorities | Implementar Solución 3 |
| **Token válido pero falla** | Mismatch "ROLE_" prefix | Asegurar roles con "ROLE_" en JwtAuthenticationFilter |

**Próximo paso:** Implementa la Solución 1 (mejorado JwtAuthenticationFilter) e inspecciona los logs.
