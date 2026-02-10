# 🔍 RESPUESTAS DETALLADAS A TUS 4 PREGUNTAS

---

## ❓ Pregunta 1: ¿Por qué busca un 'static resource'?

### Explicación Técnica

```
REQUEST: GET /api/profile/me
         Header: Authorization: Bearer {token}
              ↓
         Spring DispatcherServlet
              ↓
    ┌─────────────────────────────────┐
    │  SecurityFilterChain             │
    ├─────────────────────────────────┤
    │  1. CORS Filter        │  ✓ OK   │
    │  2. JWT Filter         │  ✗ FALLA│ ← Token inválido/no llega
    │  3. Auth Check         │  ✗ SKIP │
    └─────────────────────────────────┘
            ↓ (RECHAZADO)
    Spring no puede servir controller
            ↓
    Intenta buscar archivo estático:
    src/main/resources/static/api/profile/me  (NO EXISTE)
            ↓
    Retorna: 500 "No static resource" con 404 HttpMessageNotReadableException
```

### Causa Raíz: JWT Filter Falla

En `JwtAuthenticationFilter.java`:

```java
if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
    String token = jwtUtil.extractTokenFromBearerString(authorizationHeader);
    
    // AQUÍ SUCEDE EL PROBLEMA:
    if (token != null && jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)) {
        // ✓ Si llega aquí, autentica correctamente
        // ✗ Si no llega, continúa SIN autenticación
    }
}

// Sin autenticación → @PreAuthorize("isAuthenticated()") rechaza 403
```

### Cómo Evitarlo

**Spring Security debe verificar el token ANTES de pasar al controller:**

```
Orden de ejecución (CORRECTO):
1. Llega request con Authorization header
2. JwtAuthenticationFilter lo intercepta
3. Extrae, valida y configura autenticación
4. Controller recibe SecurityContext poblado
5. @PreAuthorize verifica isAuthenticated() → ✓ PASS
6. Servicio se ejecuta
7. Retorna 200 con datos
```

Si el filtro falla:
```
1. Llega request
2. JwtAuthenticationFilter – EXCEPCIÓN (silenciada por catch)
3. SecurityContext está VACÍO
4. Controller nunca se ejecuta
5. Spring busca archivo estático
6. No encuentra nada → 500
```

### Validación

Verifica que en el JwtAuthenticationFilter el filtro esté correctamente registrado:

**SecurityConfig.java:**
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authz -> authz
            // ... rutas públicas ...
            .anyRequest().authenticated()  // ← TODOS requieren auth
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // ↑ El filtro DEBE estar registrado aquí
    
    return http.build();
}
```

**Si el filtro no está registrado → el token NUNCA se procesa.**

---

## ❓ Pregunta 2: ¿El problema está en SecurityFilterChain?

### Análisis del Código Actual

**Tu SecurityConfig.java tiene:**

```java
.authorizeHttpRequests(authz -> authz
    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/").permitAll()
    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
    .requestMatchers("/actuator/health").permitAll()
    .requestMatchers("/actuator/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/status/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/portfolios/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/projects/public").permitAll()
    
    .anyRequest().authenticated()  // ← AQUÍ: Todas las demás rutas requieren auth
)
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
```

### Los Problemas:

#### 1. ⚠️ `/api/profile/**` NO está en la lista permitida

```
GET /api/profile/me
    ├─ NO coincide con:
    │  ├─ /swagger-ui/**
    │  ├─ /v3/api-docs/**
    │  ├─ /api/status/**
    │  ├─ /api/portfolios/**
    │  └─ /api/projects/public
    │
    └─ CAE EN: .anyRequest().authenticated()
       ↓
       Requiere JWT válido
```

**Esto es CORRECTO**, porque necesitas autenticación.

#### 2. ✓ El filtro JWT ESTÁ registrado

```java
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
```

**Esto es CORRECTO**, el filtro se ejecuta antes de todo.

#### 3. ✗ PERO: El problema está en la IMPLEMENTACIÓN del filtro, no en la configuración

Si el filtro falla internamente:
- No configura autenticación en SecurityContext
- La request llega sin autenticación
- `@PreAuthorize("isAuthenticated()")` la rechaza (403)
- Spring intenta servir como recurso estático (500)

### Solución para SecurityConfig

Si quieres permitir `/api/profile/**` de manera explícita:

```java
.authorizeHttpRequests(authz -> authz
    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/").permitAll()
    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/v3/api-docs.yaml").permitAll()
    .requestMatchers("/actuator/health").permitAll()
    .requestMatchers("/actuator/**").hasRole("ADMIN")
    .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
    .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/status/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/portfolios/**").permitAll()
    .requestMatchers(HttpMethod.GET, "/api/projects/public").permitAll()
    
    // ✓ OPCIONAL: Permitir /api/profile/me para todos, pero el controlador verifica
    // .requestMatchers(HttpMethod.GET, "/api/profile/me").authenticated()
    
    .anyRequest().authenticated()
)
.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
.csrf(csrf -> csrf.disable())
.cors(Customizer.withDefaults());
```

---

## ❓ Pregunta 3: ¿Error 500 por mala gestión de Principal/Authentication?

### Análisis del ProfileController

```java
@GetMapping("/me")
@PreAuthorize("isAuthenticated()")
public ResponseEntity<ProfileResponseDto> getMyProfile() {
    return ResponseEntity.ok(profileService.getMyProfile());
}
```

**Verifica qué es lo que invoca:**

```java
// ProfileService.getMyProfile()
@Override
public ProfileResponseDto getMyProfile() {
    User currentUser = securityUtils.getCurrentUser();  // ← AQUÍ OCURREN ERRORES
    Long currentUserId = currentUser.getId();
    
    // ...
    
    return ProfileMapper.toResponse(profile);
}
```

### Problemas Posibles

#### 1. ⚠️ SecurityUtils.getCurrentUser() falla

```java
// SecurityUtils.java (component)
public User getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
    if (auth == null || !auth.isAuthenticated()) {
        return null;  // ← PROBLEMA: Retorna null
    }
    
    // El principal podría ser:
    // - String (username)
    // - UserDetails
    // - null
    
    // Si el JwtAuthenticationFilter hace:
    // new UsernamePasswordAuthenticationToken(userDetails, null, authorities)
    // entonces getPrincipal() retorna UserDetails
    
    // PERO si hace:
    // new UsernamePasswordAuthenticationToken(username, null, authorities)
    // entonces getPrincipal() retorna String
    
    // El código no sabe cuál es
}
```

#### 2. ⚠️ JwtAuthenticationFilter configura principal incoherente

**Tu código actual:**
```java
UserDetails userDetails = userDetailsService.loadUserByUsername(username);

UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(
                userDetails,           // ← Principal es UserDetails
                null,
                userDetails.getAuthorities());

SecurityContextHolder.getContext().setAuthentication(authenticationToken);
```

**Código mejorado (en JwtAuthenticationFilter_MEJORADO.java):**
```java
List<GrantedAuthority> authorities = buildAuthoritiesFromToken(rolesFromToken);

UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(
                username,      // ← Principal es String (simple y confiable)
                null,
                authorities);

SecurityContextHolder.getContext().setAuthentication(authenticationToken);
```

### Por Qué Importa

```java
// En ProfileService:
User currentUser = securityUtils.getCurrentUser();

// SecurityUtils intenta:
public User getCurrentUser() {
    String username = (String) auth.getPrincipal();  // ✓ FUNCIONA si es String
    // PERO si es UserDetails:
    // UserDetails ud = (UserDetails) auth.getPrincipal();
    // String username = ud.getUsername();
    
    return userRepository.findByUsername(username)
            .orElseThrow(() -> new Exception("User not found"));  // ← 500 aquí
}
```

### Solución: Revisar SecurityUtils

```java
// SecurityUtils.java - MEJORADO
@Component
public class SecurityUtils {
    
    @Autowired
    private UserRepository userRepository;
    
    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResourceNotFoundException("No authenticated user");
        }
        
        Object principal = auth.getPrincipal();
        String username = null;
        
        // Manejar ambos casos
        if (principal instanceof String) {
            username = (String) principal;
        } else if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            throw new ResourceNotFoundException("Invalid principal type: " + principal.getClass());
        }
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
    
    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }
}
```

---

## ❓ Pregunta 4: ¿El AuthInterceptor en Angular adjunta el token?

### Cómo Debería Verse

**auth.interceptor.ts (Correcto):**

```typescript
import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  
  constructor(private authService: AuthService) {}
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // 1. Obtener el token del localStorage
    const token = this.authService.getToken();
    
    console.log('🔐 AuthInterceptor - Token:', token ? 'Present' : 'Missing');
    
    // 2. Si hay token, clonear request y agregar header
    if (token) {
      const clonedRequest = req.clone({
        setHeaders: {
          'Authorization': `Bearer ${token}`  // ← FORMATO CORRECTO
        }
      });
      
      console.log('✓ Header Authorization agregado:', clonedRequest.headers.get('Authorization'));
      return next.handle(clonedRequest);
    }
    
    // 3. Si no hay token, pasar request sin modificar
    console.log('ℹ️ Sin token, enviando request sin Authorization');
    return next.handle(req);
  }
}
```

### Validación en el Navegador

1. **Abre DevTools** (F12)
2. **Ve a Network tab**
3. **Haz login:**
   ```
   POST /api/auth/login → respuesta con token
   ```
4. **Verifica que el token se guardó:**
   - Storage → localStorage
   - Busca clave "token" o "jwt"
   - Debe tener un valor largo con "eyJ..."

5. **Haz request a /api/profile/me:**
   - GET /api/profile/me
   - Headers → Authorization
   - Debe decir: `Bearer eyJ...` (NO solo "eyJ...")

6. **Si NO está el header:**
   - El interceptor NO está registrado
   - O `authService.getToken()` retorna null
   - O el interceptor NO clona la request correctamente

### Problemas Comunes en Angular

#### 1. Interceptor no registrado en HttpClientModule

```typescript
// app.module.ts
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';

@NgModule({
  imports: [
    HttpClientModule,
    // ... otros imports
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true }
    // ↑ SIN ESTO, el interceptor NO se ejecuta
  ]
})
export class AppModule {}
```

#### 2. También podría ser en app.config.ts (Angular 14+)

```typescript
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([authInterceptor])  // O usar functional interceptors
    ),
    // ... otros providers
  ],
};
```

#### 3. El token NO se está guardando correctamente

```typescript
// auth.service.ts CORRECTO
export class AuthService {
  
  constructor(private http: HttpClient) {}
  
  login(email: string, password: string): Observable<any> {
    return this.http.post('/api/auth/login', { email, password })
      .pipe(
        tap(response => {
          if (response && response.token) {
            localStorage.setItem('token', response.token);  // ← GUARDAR
            console.log('✓ Token guardado:', response.token?.substring(0, 20) + '...');
          }
        })
      );
  }
  
  getToken(): string | null {
    return localStorage.getItem('token');
  }
}
```

#### 4. El interceptor NO clona correctamente

```typescript
// ✗ INCORRECTO
intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = this.authService.getToken();
    req.headers = req.headers.set('Authorization', `Bearer ${token}`);  // ← MUTA request
    return next.handle(req);
}

// ✓ CORRECTO
intercept(req: HttpRequest<any>, next: HttpHandler) {
    const token = this.authService.getToken();
    const clonedRequest = req.clone({
        setHeaders: {
            'Authorization': `Bearer ${token}`  // ← CLONA
        }
    });
    return next.handle(clonedRequest);  // ← USA CLON
}
```

### Cambios de Angular 17+

Si usas **Angular 17+** con standalone components:

```typescript
// functional-auth.interceptor.ts (Nueva forma)
import { inject } from '@angular/core';
import { HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from '../auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();
  
  console.log('🔐 Auth Interceptor - Token:', token ? 'Present' : 'Missing');
  
  if (token) {
    const AuthReq = req.clone({
      setHeaders: {
        'Authorization': `Bearer ${token}`
      }
    });
    return next(AuthReq);
  }
  
  return next(req);
};

// app.config.ts
import { provideHttpClient, withInterceptors } from '@angular/common/http';

export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(
      withInterceptors([authInterceptor])  // ← REGISTRAR AQUÍ
    ),
  ],
};
```

---

## 🔍 CHECKLIST FINAL

| Área | Check | Estado |
|------|-------|--------|
| **Backend Security** | removeToken() se ejecuta | ❓ Ver logs |
| | JWT Filter extrae token | ❓ Ver logs |
| | Roles están con "ROLE_" prefix | ❓ Validar |
| | SecurityContext tiene autenticación | ❓ Ver logs |
| | ProfileController recibe @PreAuthorize OK | ❓ Ver logs |
| **Frontend Angular** | Token se guarda en localStorage | ❓ DevTools → Storage |
| | AuthInterceptor está registrado | ❓ DevTools → Network → Headers |
| | Header Authorization está presente | ❓ DevTools → Network → Header Authorization |
| | Token tiene formato "Bearer ..." | ❓ DevTools → Network → Authorization: header |
| | getToken() NO retorna null | ❓ Console logs |

---

## 📞 Diagrama de Debugging

```
¿Problema en 403/500?
│
├─→ BACKEND (Spring Boot)
│   │
│   ├─→ ¿El token llega al servidor?
│   │   └─ Ver logs del JwtAuthenticationFilter
│   │      - "Authorization Header:"
│   │      - "Token extraído:"
│   │
│   ├─→ ¿El token es válido?
│   │   └─ Ver logs:
│   │      - "Token válido:"
│   │      - "Token expirado:"
│   │
│   ├─→ ¿Se carga el usuario?
│   │   └─ Ver logs:
│   │      - "Username del token:"
│   │      - "UserDetails cargado:"
│   │
│   └─→ ¿Se tiene seguridad contexto?
│       └─ Ver logs:
│          - "Autenticación JWT establecida para:"
│
└─→ FRONTEND (Angular)
    │
    ├─→ ¿Se genera el token?
    │   └─ Ver en DevTools → Storage → localStorage
    │      Busca clave "token" con valor "eyJ..."
    │
    ├─→ ¿Se envía el token?
    │   └─ Ver DevTools → Network
    │      GET /api/profile/me → Headers
    │      Authorization: Bearer eyJ...
    │
    └─→ ¿El interceptor está activo?
        └─ En console.log de authInterceptor.ts
           "🔐 Auth Interceptor - Token: Present"
```

**Si falta algo, ahí está el problema.**
