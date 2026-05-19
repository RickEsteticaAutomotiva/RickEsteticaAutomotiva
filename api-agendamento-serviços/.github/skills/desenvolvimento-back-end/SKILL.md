# 🚀 SKILL - Desenvolvimento Back-End Especialista
## Spring Boot 3.5.7 + Java 21 + Clean Architecture + TDD Obrigatório

> **Agente Especialista em Backend Development**  
> Versão 2.0 | Maio 2026 | Rick Estética Automotiva  
> Foco: **Simplicidade. Qualidade. Performance. Segurança (OWASP)**

---

## 📋 Índice Rápido

1. **[🏗️ Arquitetura](#-arquitetura-clean-architecture)**
2. **[📐 Padrões de Código](#-padrões-de-código-prontos-para-usar)**
3. **[🔴🟢 TDD Obrigatório](#-tdd-obrigatório-red-green-refactor)**
4. **[⚡ Performance & OWASP](#-performance--owasp-security)**
5. **[🛠️ Checklist de Feature](#-checklist-de-feature)**
6. **[🐛 Troubleshooting](#-troubleshooting-rápido)**

---

## 🏗️ Arquitetura: Clean Architecture

### 📁 Estrutura de Pacotes

```
com.automotiva.estetica.rick/
├── application/       ← 🎯 Presentation Layer
│   ├── controller/    ← @RestController, @PostMapping, @GetMapping...
│   ├── dto/           ← Data Transfer Objects (request/response)
│   ├── service/       ← Orquestração de casos de uso
│   ├── mapper/        ← DTO ↔ Entity (MapStruct)
│   └── security/      ← JWT, Authorization, Roles
│
├── domain/            ← 📦 Business Logic Layer (regras do negócio)
│   ├── entity/        ← @Entity, @Table, @Column
│   ├── gateway/       ← Interfaces abstratas (abstração de infraestrutura)
│   ├── usecase/       ← Casos de uso (ex: CriarAgendamentoUseCase)
│   ├── enums/         ← Enumerações de domínio
│   └── exception/     ← Exceções customizadas do negócio
│
└── infrastructure/    ← 🔌 Infrastructure Layer (detalhes técnicos)
    ├── repository/    ← @Repository extends JpaRepository
    ├── gateway/       ← Implementações de gateways
    ├── messaging/     ← RabbitMQ, Publishers, MessageQueues
    ├── config/        ← @Configuration, Beans, Security
    ├── handler/       ← Exception handlers, Converters
    ├── email/         ← Serviço de email
    └── entity/        ← @Entity para infraestrutura
```

### 🎯 Regras de Arquitetura

| Camada | Pode acessar | NÃO pode acessar | Responsabilidade |
|--------|-------------|-----------------|------------------|
| **Controller** | DTO, Service | Entity direto | Entrada HTTP |
| **Service** | Entity, Gateway, UseCase | Infrastructure direta | Orquestração |
| **UseCase** | Entity, Gateway | Controller, Service | Lógica de negócio |
| **Repository** | Entity, Nada mais | - | Acesso a dados |
| **Gateway** | Entity | - | Contrato técnico |

**Regra de Ouro:** `Domain` nunca depende de `Application` ou `Infrastructure`

---

## 📐 Padrões de Código Prontos para Usar

### ✨ Padrão 1: CRUD REST Endpoint (Entity + DTO + Service + Controller + Testes)

**Passo 1: Domain Layer - Entidade e Gateway**

```java
// 📦 domain/entity/Veiculo.java
@Entity
@Table(name = "veiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Veiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String placa;
    
    @Column(nullable = false, length = 50)
    private String marca;
    
    @Column(nullable = false, length = 50)
    private String modelo;
    
    @Column(nullable = false)
    private Integer ano;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime criadoEm;
    
    @UpdateTimestamp
    private LocalDateTime atualizadoEm;
}

// 📦 domain/gateway/VeiculoGateway.java (interface)
public interface VeiculoGateway {
    Veiculo salvar(Veiculo veiculo);
    Optional<Veiculo> buscarPorId(Long id);
    void deletar(Long id);
    Page<Veiculo> listarPaginado(Pageable pageable);
}
```

**Passo 2: Infrastructure Layer - Repository & Gateway Implementation**

```java
// 🔌 infrastructure/repository/VeiculoRepository.java
@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {
    Optional<Veiculo> findByPlaca(String placa);
    Page<Veiculo> findByPessoaId(Long pessoaId, Pageable pageable);
}

// 🔌 infrastructure/gateway/VeiculoGatewayImpl.java
@Component
@RequiredArgsConstructor
public class VeiculoGatewayImpl implements VeiculoGateway {
    private final VeiculoRepository repository;
    
    @Override
    @Transactional
    public Veiculo salvar(Veiculo veiculo) {
        return repository.save(veiculo);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Veiculo> buscarPorId(Long id) {
        return repository.findById(id);
    }
    
    @Override
    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Veiculo> listarPaginado(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
```

**Passo 3: Application Layer - DTO & Mapper**

```java
// 🎯 application/dto/VeiculoRequest.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeiculoRequest {
    @NotBlank(message = "Placa é obrigatória")
    private String placa;
    
    @NotBlank(message = "Marca é obrigatória")
    private String marca;
    
    @NotBlank(message = "Modelo é obrigatório")
    private String modelo;
    
    @NotNull(message = "Ano é obrigatório")
    @Min(1990)
    private Integer ano;
    
    @NotNull(message = "ID da pessoa é obrigatório")
    private Long pessoaId;
}

// 🎯 application/dto/VeiculoResponse.java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VeiculoResponse {
    private Long id;
    private String placa;
    private String marca;
    private String modelo;
    private Integer ano;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}

// 🎯 application/mapper/VeiculoMapper.java
@Mapper(componentModel = "spring")
public interface VeiculoMapper {
    VeiculoResponse toResponse(Veiculo veiculo);
    Veiculo toEntity(VeiculoRequest request);
}
```

**Passo 4: Application Layer - Service**

```java
// 🎯 application/service/VeiculoService.java
@Service
@RequiredArgsConstructor
@Slf4j
public class VeiculoService {
    private final VeiculoGateway gateway;
    private final VeiculoMapper mapper;
    private final PessoaGateway pessoaGateway;
    
    @Transactional
    public VeiculoResponse criar(VeiculoRequest request) {
        // ✅ Verificar se pessoa existe
        Pessoa pessoa = pessoaGateway.buscarPorId(request.getPessoaId())
            .orElseThrow(() -> new PessoaNaoEncontradaException("Pessoa não encontrada"));
        
        // ✅ Mapear DTO → Entity
        Veiculo veiculo = mapper.toEntity(request);
        veiculo.setPessoa(pessoa);
        
        // ✅ Salvar
        Veiculo salvo = gateway.salvar(veiculo);
        
        log.info("Veículo criado: {}", salvo.getId());
        return mapper.toResponse(salvo);
    }
    
    @Transactional(readOnly = true)
    public VeiculoResponse buscarPorId(Long id) {
        return gateway.buscarPorId(id)
            .map(mapper::toResponse)
            .orElseThrow(() -> new VeiculoNaoEncontradoException("Veículo não encontrado"));
    }
    
    @Transactional(readOnly = true)
    public Page<VeiculoResponse> listarPaginado(Pageable pageable) {
        return gateway.listarPaginado(pageable)
            .map(mapper::toResponse);
    }
    
    @Transactional
    public void deletar(Long id) {
        buscarPorId(id); // ✅ Validar existência
        gateway.deletar(id);
        log.info("Veículo deletado: {}", id);
    }
}
```

**Passo 5: Application Layer - Controller**

```java
// 🎯 application/controller/VeiculoController.java
@RestController
@RequestMapping("/api/veiculos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Veículos", description = "Gerenciamento de veículos")
public class VeiculoController {
    private final VeiculoService service;
    
    @PostMapping
    @Operation(summary = "Criar novo veículo")
    public ResponseEntity<VeiculoResponse> criar(@RequestBody @Valid VeiculoRequest request) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(service.criar(request));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar veículo por ID")
    public ResponseEntity<VeiculoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
    
    @GetMapping
    @Operation(summary = "Listar veículos com paginação")
    public ResponseEntity<Page<VeiculoResponse>> listar(
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(service.listarPaginado(pageable));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar veículo")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 🔴🟢 TDD Obrigatório: Red-Green-Refactor

### 📝 Padrão de Teste Unitário (Sem BD)

```java
// 📋 test/java/.../VeiculoServiceTest.java
@ExtendWith(MockitoExtension.class)
@DisplayName("VeiculoService - Testes Unitários")
class VeiculoServiceTest {
    
    @Mock
    private VeiculoGateway gateway;
    
    @Mock
    private VeiculoMapper mapper;
    
    @Mock
    private PessoaGateway pessoaGateway;
    
    @InjectMocks
    private VeiculoService service;
    
    private Pessoa pessoaMock;
    private Veiculo veiculoMock;
    private VeiculoRequest requestMock;
    private VeiculoResponse responseMock;
    
    @BeforeEach
    void setup() {
        pessoaMock = Pessoa.builder().id(1L).nome("João").build();
        veiculoMock = Veiculo.builder()
            .id(1L)
            .placa("ABC1234")
            .marca("Toyota")
            .modelo("Corolla")
            .ano(2023)
            .pessoa(pessoaMock)
            .build();
        
        requestMock = VeiculoRequest.builder()
            .placa("ABC1234")
            .marca("Toyota")
            .modelo("Corolla")
            .ano(2023)
            .pessoaId(1L)
            .build();
        
        responseMock = VeiculoResponse.builder()
            .id(1L)
            .placa("ABC1234")
            .marca("Toyota")
            .modelo("Corolla")
            .ano(2023)
            .build();
    }
    
    // 🔴 RED: Teste falha inicialmente
    // 🟢 GREEN: Implementação mínima faz passar
    // 🔵 REFACTOR: Melhorar código (sem quebrar teste)
    
    @Test
    @DisplayName("Deve criar veículo com sucesso")
    void deveCriarVeiculoComSucesso() {
        // ARRANGE
        when(pessoaGateway.buscarPorId(1L)).thenReturn(Optional.of(pessoaMock));
        when(mapper.toEntity(requestMock)).thenReturn(veiculoMock);
        when(gateway.salvar(veiculoMock)).thenReturn(veiculoMock);
        when(mapper.toResponse(veiculoMock)).thenReturn(responseMock);
        
        // ACT
        VeiculoResponse resultado = service.criar(requestMock);
        
        // ASSERT
        assertThat(resultado)
            .isNotNull()
            .extracting("id", "placa", "marca")
            .containsExactly(1L, "ABC1234", "Toyota");
        
        verify(gateway).salvar(any(Veiculo.class));
        verify(pessoaGateway).buscarPorId(1L);
    }
    
    @Test
    @DisplayName("Deve lançar exceção quando pessoa não existe")
    void deveLancarExcecaoQuandoPessoaNaoExiste() {
        // ARRANGE
        when(pessoaGateway.buscarPorId(999L))
            .thenReturn(Optional.empty());
        
        // ACT & ASSERT
        assertThatThrownBy(() -> service.criar(requestMock))
            .isInstanceOf(PessoaNaoEncontradaException.class)
            .hasMessage("Pessoa não encontrada");
        
        verify(gateway, never()).salvar(any());
    }
    
    @Test
    @DisplayName("Deve buscar veículo by ID com sucesso")
    void deveBuscarVeiculoPorIdComSucesso() {
        // ARRANGE
        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(veiculoMock));
        when(mapper.toResponse(veiculoMock)).thenReturn(responseMock);
        
        // ACT
        VeiculoResponse resultado = service.buscarPorId(1L);
        
        // ASSERT
        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
    }
    
    @Test
    @DisplayName("Deve deletar veículo com sucesso")
    void deveDeletarVeiculoComSucesso() {
        // ARRANGE
        when(gateway.buscarPorId(1L)).thenReturn(Optional.of(veiculoMock));
        
        // ACT
        service.deletar(1L);
        
        // ASSERT
        verify(gateway).deletar(1L);
    }
}
```

### 📝 Padrão de Teste de Integração (Com BD Real - H2)

```java
// 📋 test/java/.../VeiculoControllerIT.java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration-test")
@DisplayName("VeiculoController - Testes de Integração")
class VeiculoControllerIT {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private VeiculoRepository veiculoRepository;
    
    @Autowired
    private PessoaRepository pessoaRepository;
    
    private Pessoa pessoaSalva;
    
    @BeforeEach
    void setup() {
        veiculoRepository.deleteAll();
        pessoaRepository.deleteAll();
        
        pessoaSalva = pessoaRepository.save(
            Pessoa.builder()
                .nome("João Silva")
                .email("joao@test.com")
                .build()
        );
    }
    
    @AfterEach
    void cleanup() {
        veiculoRepository.deleteAll();
        pessoaRepository.deleteAll();
    }
    
    @Test
    @DisplayName("POST /api/veiculos - Deve criar veículo")
    void devePostVeiculoComSucesso() {
        // ARRANGE
        VeiculoRequest request = VeiculoRequest.builder()
            .placa("ABC1234")
            .marca("Toyota")
            .modelo("Corolla")
            .ano(2023)
            .pessoaId(pessoaSalva.getId())
            .build();
        
        // ACT
        ResponseEntity<VeiculoResponse> response = restTemplate.postForEntity(
            "/api/veiculos",
            request,
            VeiculoResponse.class
        );
        
        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody())
            .isNotNull()
            .extracting("placa", "marca")
            .containsExactly("ABC1234", "Toyota");
        
        assertThat(veiculoRepository.findAll()).hasSize(1);
    }
    
    @Test
    @DisplayName("GET /api/veiculos/{id} - Deve buscar veículo")
    void deveGetVeiculoPorIdComSucesso() {
        // ARRANGE
        Veiculo veiculoSalvo = veiculoRepository.save(
            Veiculo.builder()
                .placa("ABC1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2023)
                .pessoa(pessoaSalva)
                .build()
        );
        
        // ACT
        ResponseEntity<VeiculoResponse> response = restTemplate.getForEntity(
            "/api/veiculos/" + veiculoSalvo.getId(),
            VeiculoResponse.class
        );
        
        // ASSERT
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody())
            .isNotNull()
            .extracting("id")
            .isEqualTo(veiculoSalvo.getId());
    }
    
    @Test
    @DisplayName("DELETE /api/veiculos/{id} - Deve deletar veículo")
    void deveDeletarVeiculoComSucesso() {
        // ARRANGE
        Veiculo veiculoSalvo = veiculoRepository.save(
            Veiculo.builder()
                .placa("ABC1234")
                .marca("Toyota")
                .modelo("Corolla")
                .ano(2023)
                .pessoa(pessoaSalva)
                .build()
        );
        
        // ACT
        restTemplate.delete("/api/veiculos/" + veiculoSalvo.getId());
        
        // ASSERT
        assertThat(veiculoRepository.findAll()).isEmpty();
    }
}
```

### ✅ Checklist TDD Obrigatório

- [ ] **RED:** Teste falha (implementação não existe)
- [ ] **GREEN:** Implementação mínima faz teste passar
- [ ] **REFACTOR:** Melhorar código sem quebrar testes
- [ ] **Coverage:** Mínimo 75% via JaCoCo
- [ ] **Testes Unitários:** 1 teste por método crítico
- [ ] **Testes de Integração:** Fluxo completo (API → DB)
- [ ] **Mocks:** Gateway/Repository mockados em unit tests
- [ ] **Dados:** @BeforeEach popular com dados realistas

---

## ⚡ Performance & OWASP Security

### 🚀 Performance - 5 Rules Essenciais

#### 1. **Use `readOnly = true` em Queries**
```java
@Transactional(readOnly = true)  // ✅ Otimiza flush (não salva desnecessariamente)
public VeiculoResponse buscar(Long id) {
    return gateway.buscarPorId(id).map(mapper::toResponse)
        .orElseThrow(...);
}
```

#### 2. **Evite N+1 Query Problem**
```java
// ❌ RUIM: SELECT pessoa, depois SELECT veículo para cada pessoa
List<Pessoa> pessoas = pessoaRepository.findAll();
pessoas.forEach(p -> System.out.println(p.getVeiculos()));

// ✅ BOM: JOIN em uma query
@Query("SELECT p FROM Pessoa p JOIN FETCH p.veiculos")
List<Pessoa> findAllWithVeiculos();
```

#### 3. **Use Pagination para Largas Listas**
```java
// ❌ RUIM: Retorna tudo (10k registros = 10MB RAM)
List<Veiculo> todos = repository.findAll();

// ✅ BOM: Retorna apenas 20 registros
Page<Veiculo> pagina = repository.findAll(PageRequest.of(0, 20));
```

#### 4. **Cache para Dados Imutáveis**
```java
@Cacheable("categorias")  // ✅ Armazena em memória por 10 min
public List<CategoriaResponse> listarCategorias() {
    return repository.findAll().stream()
        .map(mapper::toResponse)
        .toList();
}
```

#### 5. **Use DTOs (não retorne entidades completas)**
```java
// ❌ RUIM: Serializa relacionamentos desnecessários
@RestController
public void pessoaController {
    return pessoaRepository.findAll();  // Inclui veiculos, enderecos...
}

// ✅ BOM: Retorna apenas campos necessários
public Page<PessoaResponse> listar(Pageable pageable) {
    return gatewya.listar(pageable).map(mapper::toResponse);
}
```

### 🔒 OWASP Security - 10 Implementações Essenciais

#### 1. **Input Validation (OWASP A7)**
```java
public VeiculoRequest {
    @NotBlank(message = "Placa obrigatória")
    @Size(min = 7, max = 8)
    @Pattern(regexp = "[A-Z]{3}\\d{4}", message = "Formato inválido")
    private String placa;
}
```

#### 2. **SQL Injection Prevention (OWASP A1)**
```java
// ❌ RUIM: SQL concatenation
String sql = "SELECT * FROM veiculo WHERE placa = '" + placa + "'";

// ✅ BOM: Parameterized queries (JPA automático)
repository.findByPlaca(placa);  // Usa prepared statement
```

#### 3. **Authentication & Authorization (OWASP A1)**
```java
@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
public ResponseEntity<List<VeiculoResponse>> listarTodos() {
    return ResponseEntity.ok(service.listarTodos());
}

// ✅ Use JWT + Spring Security
```

#### 4. **Sensitive Data Exposure (OWASP A2)**
```java
// ❌ RUIM: Retorna senha no DTO
public PessoaResponse {
    private String senha;  // NÃO!
}

// ✅ BOM: Nunca retorne senha
public PessoaResponse {
    private String id;
    private String nome;
    private String email;
    // senha nunca aqui
}
```

#### 5. **Broken Access Control (OWASP A1)**
```java
@PostMapping("/{id}/deletar")
@PreAuthorize("@authService.isOwnerOrAdmin(#id)")  // ✅ Valida permissão
public ResponseEntity<Void> deletar(@PathVariable Long id) {
    service.deletar(id);
    return ResponseEntity.noContent().build();
}
```

#### 6. **CORS Configuration (OWASP A1)**
```java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins("https://app.rickestetca.com.br")  // ✅ Específico
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
```

#### 7. **Rate Limiting (OWASP A4)**
```java
// Adicionar ao pom.xml
<dependency>
    <groupId>io.github.bucket4j</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>7.6.0</version>
</dependency>

@Component
public class RateLimitFilter implements Filter {
    private final Bucket bucket = Bucket4j.builder()
        .addLimit(Limit.of(100, Bandwidth.ofHour(1)))  // 100 req/hour
        .build();
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, 
            FilterChain chain) throws IOException, ServletException {
        if (bucket.tryConsume(1)) {
            chain.doFilter(req, res);
        } else {
            ((HttpServletResponse) res).sendError(429, "Too Many Requests");
        }
    }
}
```

#### 8. **Error Handling (OWASP A6)**
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(VeiculoNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleVeiculoNotFound(
            VeiculoNaoEncontradoException ex) {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse.builder()
                .message("Recurso não encontrado")
                .status(404)
                .build());  // ✅ Nunca exponha stack trace
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.of("Erro interno do servidor"));  // ✅ Genérico
    }
}
```

#### 9. **Logging Seguro (OWASP A10)**
```java
@Aspect
@Component
public class SecurityAuditAspect {
    private static final Logger AUDIT = LoggerFactory.getLogger("AUDIT");
    
    @After("@annotation(com.automotiva.estetica.rick.infrastructure.security.Audit)")
    public void auditLog(JoinPoint jp) {
        String user = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        String method = jp.getSignature().getName();
        AUDIT.info("AÇÃO: user={}, method={}, timestamp={}", 
            user, method, LocalDateTime.now());
    }
}
```

#### 10. **Database Encryption (OWASP A2)**
```yaml
# application-prod.properties
spring.datasource.url=jdbc:mysql://localhost:3306/rick?useSSL=true&requireSSL=true&serverTimezone=UTC
spring.datasource.username=${DB_USER}  # ✅ Via env var, não hardcoded
spring.datasource.password=${DB_PASSWORD}  # ✅ Via env var
```

---

## 🛠️ Checklist de Feature

Sempre seguir essa sequência para cada nova feature:

```
┌──────────────────────────────────────────────────────────────────┐
│ 📋 CHECKLIST - Nova Feature Back-End                             │
└──────────────────────────────────────────────────────────────────┘

FASE 1: ANÁLISE (15 min)
  ☐ Ler requisito completo
  ☐ Identificar entidade/agregador (Domain)
  ☐ Mapear campos obrigatórios
  ☐ Validações de negócio
  ☐ Relacionamentos com outras entidades
  ☐ Performance considerations

FASE 2: TDD RED (30 min)
  ☐ Criar teste unitário falha (ServiceTest)
  ☐ Criar teste integração falha (ControllerIT)
  ☐ mvn test (DEVE FALHAR)
  
FASE 3: IMPLEMENTAÇÃO GREEN (1h)
  ☐ Domain: Entity + Gateway (interface)
  ☐ Infrastructure: Repository + GatewayImpl
  ☐ Application: DTO + Mapper + Service + Controller
  ☐ Exception customizada
  ☐ mvn test (DEVE PASSAR)
  
FASE 4: REFACTOR & QUALIDADE (30 min)
  ☐ Remover duplicação
  ☐ Melhorar nomes
  ☐ Adicionar Javadoc em métodos públicos
  ☐ mvn clean verify (100% build sucesso)
  ☐ Validar coverage ≥ 75% (JaCoCo)
  
FASE 5: SEGURANÇA (20 min)
  ☐ Input Validation (@Valid em DTOs)
  ☐ Authorization (@PreAuthorize)
  ☐ Dados sensíveis protegidos (DTO)
  ☐ SQL Injection prevenido (JPA + Query objects)
  ☐ Logs conformes (sem senha/token)
  
FASE 6: PERFORMANCE (15 min)
  ☐ @Transactional(readOnly=true) em queries
  ☐ Evitou N+1 (JOIN FETCH se necessário)
  ☐ Paginação em listas grandes
  ☐ Cache implementado (se faz sentido)
  ☐ DTOs retornados (não Entity)
  
FASE 7: PR REVIEW (10 min)
  ☐ Coverage ≥ 75%
  ☐ mvn clean verify = SUCCESS
  ☐ Sem duplicação (PMD clean)
  ☐ Sem violação Spotless (format correto)
  ☐ ArchUnit tests validando camadas
  ☐ Mensagens de commit descritivas
  ☐ PR description com contexto
  
TOTAL: ~3h por feature completa ✅
```

---

## 🐛 Troubleshooting Rápido

### ❌ "NullPointerException em Service"
```java
// Causa: @Autowired, mas Bean não injetado
// Solução:
@Service
@RequiredArgsConstructor  // ✅ Use Lombok para injeção construtor
public class VeiculoService {
    private final VeiculoGateway gateway;  // ✅ Final (immutable)
}
```

### ❌ "LazyInitializationException ao acessar relacionamento"
```java
// Causa: LAZY = não carrega related entities por padrão
@ManyToOne(fetch = FetchType.LAZY)  // ❌ Padrão (lazy)
private Pessoa pessoa;

// Solução 1: Usar @Query com JOIN FETCH
@Query("SELECT v FROM Veiculo v JOIN FETCH v.pessoa WHERE v.id = :id")
Optional<Veiculo> findByIdComPessoa(Long id);

// Solução 2: Abrir transação
@Transactional(readOnly = true)
public Veiculo buscar(Long id) {
    return repository.findById(id).orElseThrow();
}
```

### ❌ "No entity manager with synchronization enabled"
```java
// Causa: Fora de transação
// Solução:
@Transactional  // ✅ Abre transação
public void atualizar(Veiculo veiculo) {
    repository.save(veiculo);
}
```

### ❌ "Failed to instantiate class ... no default constructor"
```java
// Causa: Classe sem @NoArgsConstructor
@Entity
@NoArgsConstructor  // ✅ Obrigatório para JPA
@AllArgsConstructor
public class Veiculo { }
```

### ❌ "Test coverage < 75% (Build falha)"
```
Solução:
1. Identificar classes com coverage baixo:
   ./mvnw jacoco:report
   # Abra: target/site/jacoco/index.html

2. Adicionar testes para:
   - Métodos críticos (buscar, salvar, deletar)
   - Validações
   - Exceções
   
3. Verificar novamente:
   ./mvnw clean verify
```

### ❌ "Spotless BUILD FAILURE"
```bash
# Causa: Formatação de código incorreta
# Solução:
./mvnw spotless:apply  # ✅ Auto-corrige

# Depois:
./mvnw clean verify
```

### ❌ "Port 8080 already in use"
```powershell
# Windows:
Get-Process -Id (Get-NetTCPConnection -LocalPort 8080).OwningProcess
Stop-Process -Id <PID> -Force

# Ou simplesmente mude a porta:
./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### ❌ "MySQL connection refused"
```bash
# Verificar se MySQL está rodando:
docker ps | grep mysql

# Se não estiver:
docker compose -f docker/docker-compose.yml up -d mysql
```

---

## 📚 Comandos Maven Essenciais

```bash
# Compilar
./mvnw compile

# Testes unitários + verificação de qualidade
./mvnw clean test

# Testes de integração
./mvnw clean verify

# Verificação completa (test + integration + pmd + spotless + coverage)
./mvnw clean verify

# Rodar aplicação
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev,swagger"

# Verificar Coverage (JaCoCo)
./mvnw jacoco:report
# Abra: target/site/jacoco/index.html

# Auto-formatar código
./mvnw spotless:apply

# Pular testes (apenas para build, não recomendado)
./mvnw clean install -DskipTests

# Profile prod
./mvnw clean verify -P prod

# Profile teste integração
./mvnw verify -P integration-test
```

---

## 🎯 Resumo Rápido: Do ZERO ao Coding

### 1️⃣ Setup (Primeira Vez)
```bash
git clone <repo>
cd api-agendamento-servicos
docker compose -f docker/docker-compose.yml up -d
./setup-dev-secrets.ps1  # Windows
./mvnw clean verify
```

### 2️⃣ Comece uma Feature
1. Crie teste que FALHA (TDD Red)
2. Implemente código mínimo (TDD Green)
3. Refatore se necessário
4. `mvn clean verify` = OK = PRonto para PR

### 3️⃣ Estruture Assim
```
Entity (domain) → Gateway (domain interface)
    ↓
Repository (infra interface) → GatewayImpl (infra impl)
    ↓
DTO (application) → Mapper (application)
    ↓
Service (application) → Controller (application)
```

### 4️⃣ Sempre
- ✅ TDD Obrigatório (Red → Green → Refactor)
- ✅ Testes Unitários (75%+ coverage)
- ✅ Testes Integração (fluxo API → DB)
- ✅ Segurança (OWASP)
- ✅ Performance (readOnly, pagination, cache)
- ✅ Clean Architecture (domain nunca depende de application/infra)

---

## 📖 Links de Referência

- **Maven:** https://maven.apache.org/
- **Spring Boot:** https://spring.io/projects/spring-boot
- **JPA/Hibernate:** https://hibernate.org/orm/
- **JUnit 5:** https://junit.org/junit5/
- **Mockito:** https://site.mockito.org/
- **OWASP Top 10:** https://owasp.org/www-project-top-ten/
- **Clean Architecture:** https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html

---

## ✨ Notas Finais

**Este é um agente VIVO.** Atualize conforme:
- ✅ Novos padrões descobertos
- ✅ Problemas resolvidos
- ✅ Mudanças na stack

**Evolução do Agente:**
- v1.0: Guia inicial
- v2.0: **VOCÊ ESTÁ AQUI** - TDD + Performance + OWASP
- v3.0: (Future) RabbitMQ patterns, async events

---

**🚀 Agora comece!**

```
1. Escolha sua feature
2. Crie teste que FALHA
3. Implemente código mínimo
4. mvn clean verify
5. Submit PR
```

**Tempo estimado por feature:** ~3 horas (análise + código + testes + qualidade)

---

**Versão:** 2.0.0  
**Última atualização:** Maio 2026  
**Mantido por:** Rick Estética Automotiva - Backend Team  
**Status:** ✅ Pronto para Produção

