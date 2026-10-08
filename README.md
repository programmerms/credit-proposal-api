# API de Propostas de Crédito

Projeto **didático** (bootcamp DIO/Itaú) que demonstra os padrões de projeto **Facade**, **Strategy** e **Singleton** em uma arquitetura **hexagonal**.

Um gerente informa o CPF ou CNPJ do cliente, um produto de crédito, um valor e um prazo. A API escolhe o catálogo da pessoa (PF ou PJ), aplica a taxa mensal configurada, calcula as parcelas (Tabela Price) e gera a **proposta**. Depois, o cliente pode **contratar** a proposta informando seus dados cadastrais.

> As taxas dos catálogos (`0.05`, `0.025`, `0.04` e `0.035`) são **didáticas** e não representam taxas reais de mercado.

## Fluxo em duas fases

```
Fase 1 — Geração                          Fase 2 — Contratação
POST /api/propostas                       POST /api/contratacoes
  documento + produto + valor + prazo       numeroProposta + cliente + endereço + contato
        │                                           │
        ▼                                           ▼
  ProposalService (FACADE)                  ContractService (FACADE)
   ├─ DocumentValidator (CPF/CNPJ)           ├─ busca a proposta (o documento vem dela)
   ├─ StrategySelector ──► Pf/PjRateStrategy ├─ CustomerStrategySelector ──► Natural/LegalPersonCustomerStrategy
   │        (STRATEGY: taxa por tipo)        │        (STRATEGY: cliente por tipo)
   ├─ ProductCatalogRegistry (SINGLETON)     ├─ grava a Contract
   └─ calcula parcelas e grava a Proposal    └─ Proposal: GERADA ──► CONTRATADA
        status = GERADA
```

## Padrões de projeto aplicados

| Padrão | Fase 1 – Geração da proposta | Fase 2 – Contratação |
| --- | --- | --- |
| **Facade** | `ProposalService`: valida o documento, identifica PF/PJ, resolve a taxa, calcula parcelas e persiste, escondendo essa orquestração do controller | `ContractService`: busca a proposta, usa o documento dela, monta o cliente, grava a contratação e muda o status, tudo em uma transação |
| **Strategy** | `PfRateStrategy` / `PjRateStrategy`, escolhidas por `StrategySelector`: cada tipo de pessoa usa seu próprio catálogo e taxa | `NaturalPersonCustomerStrategy` / `LegalPersonCustomerStrategy`, escolhidas por `CustomerStrategySelector`: cada tipo exige e monta os dados de cliente corretos (PF: nome, nascimento, sexo, estado civil; PJ: razão social) |
| **Singleton** | `ProductCatalogRegistry` (Singleton GoF explícito): carrega os catálogos PF e PJ uma única vez e os expõe como somente leitura | Não é consultado na contratação: a proposta já guarda o *snapshot* da taxa aplicada |

Mais detalhes e as decisões de projeto estão em [`docs/contratacao-design.md`](docs/contratacao-design.md).

## Tecnologias e arquitetura

- Java 17 e Spring Boot (Spring MVC, Bean Validation, Spring Data JPA)
- Maven, H2, JUnit 5, Mockito, AssertJ e Instancio
- Springdoc OpenAPI (Swagger UI)

Estrutura hexagonal (`com.dio.desafio`):

- `adapter/web`: controllers, DTOs (contrato JSON em português), mapper web e `GlobalExceptionHandler`.
- `adapter/persistence`: entidades JPA, repositórios, mappers e adapters das portas de saída.
- `application/port/in` e `application/port/out`: casos de uso e portas de saída (persistência e catálogo).
- `application/service` e `application/strategy`: Facades e Strategies.
- `domain`: `Proposal`, `Contract`, `Customer` (PF/PJ), cálculo de parcelas e validação de documentos.
- `config`: beans e catálogo de produtos.

## Pré-requisitos e comandos

Instale um JDK 17. Os comandos usam o Maven Wrapper:

```bash
./mvnw test
./mvnw spring-boot:run
```

O primeiro executa os testes; o segundo sobe a API em `http://localhost:8080`.

Para desenvolvimento local com o console do H2 (`/h2-console`), ative o profile `dev`; sem ele o console fica desabilitado:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

A documentação interativa (Swagger UI) fica em `http://localhost:8080/swagger-ui.html` e o documento OpenAPI em `/v3/api-docs`.

Para empacotar e executar o JAR:

```bash
./mvnw package
java -jar target/desafio-0.0.1-SNAPSHOT.jar
```

Se o Maven Wrapper não estiver disponível, use um Maven instalado (`mvn test`, `mvn spring-boot:run`, `mvn package`).

## Catálogos de produtos e taxas

Produtos são dados de configuração, não enums Java:

- PF: `src/main/resources/catalogs/pf.json`
- PJ: `src/main/resources/catalogs/pj.json`

O CPF seleciona apenas o catálogo PF e o CNPJ apenas o catálogo PJ. O campo `produto` deve conter o `code` textual estável do produto, não a descrição. O produto é resolvido pela combinação tipo de pessoa + código, então um mesmo código poderia existir nos dois catálogos com taxas independentes. Um produto que existe só no catálogo do outro tipo de pessoa (ex.: `CREDITO_PESSOAL` para um CNPJ) é rejeitado como incompatível. Cada produto tem `monthlyRate` numérico estritamente maior que zero.

| Pessoa | Código do produto | Descrição | Taxa mensal |
| --- | --- | --- | ---: |
| PF | `CREDITO_PESSOAL` | Crédito Pessoal | `0.05` |
| PF | `CREDITO_CONSIGNADO` | Crédito Consignado | `0.025` |
| PJ | `CREDITO_VERDE` | Crédito Verde / Sustentável | `0.04` |
| PJ | `CAPITAL_GIRO` | Capital de Giro | `0.035` |

A proposta guarda a taxa aplicada no momento da geração; alterações posteriores no catálogo não afetam propostas existentes. O H2 é um banco em memória: os dados são recriados quando a aplicação para.

## Fase 1: gerar uma proposta

`POST /api/propostas` com os campos:

- `documento`: CPF (PF) ou CNPJ (PJ), com ou sem formatação;
- `produto`: código do produto no catálogo correspondente;
- `valor`: valor em reais, maior que zero e no máximo `1000000000.00`;
- `prazoMeses`: prazo em meses, entre 1 e 600.

Exemplos (documentos de teste válidos):

```bash
# PF — CREDITO_PESSOAL (0.05 ao mês)
curl -i -X POST http://localhost:8080/api/propostas -H 'Content-Type: application/json' \
  -d '{"documento":"529.982.247-25","produto":"CREDITO_PESSOAL","valor":2500.00,"prazoMeses":12}'

# PF — CREDITO_CONSIGNADO (0.025 ao mês)
curl -i -X POST http://localhost:8080/api/propostas -H 'Content-Type: application/json' \
  -d '{"documento":"529.982.247-25","produto":"CREDITO_CONSIGNADO","valor":2500.00,"prazoMeses":12}'

# PJ — CREDITO_VERDE (0.04 ao mês)
curl -i -X POST http://localhost:8080/api/propostas -H 'Content-Type: application/json' \
  -d '{"documento":"11.222.333/0001-81","produto":"CREDITO_VERDE","valor":25000.00,"prazoMeses":24}'

# PJ — CAPITAL_GIRO (0.035 ao mês)
curl -i -X POST http://localhost:8080/api/propostas -H 'Content-Type: application/json' \
  -d '{"documento":"11.222.333/0001-81","produto":"CAPITAL_GIRO","valor":25000.00,"prazoMeses":24}'
```

Resposta de sucesso (`201 Created`):

```json
{
  "numero": "b6e9e387-f5c1-4d4e-9a52-dfe9d2977c89",
  "documento": "52998224725",
  "descricao": "Crédito Pessoal",
  "valor": 2500.00,
  "prazoMeses": 12,
  "taxaMensal": 0.05,
  "status": "GERADA",
  "parcelas": [
    {"numero": 1, "vencimento": "2026-11-02", "amortizacao": 156.38, "juros": 125.00, "valor": 281.38, "saldoDevedor": 2343.62},
    {"numero": 2, "vencimento": "2026-12-02", "amortizacao": 164.20, "juros": 117.18, "valor": 281.38, "saldoDevedor": 2179.42},
    "... mais 10 parcelas ..."
  ]
}
```

`numero` é o **número da proposta**, usado na contratação. As `parcelas` seguem a Tabela Price (parcelas fixas): `valor = amortizacao + juros`, `saldoDevedor` é o principal restante e a última parcela absorve o arredondamento, zerando o saldo.

Consulta: `GET /api/propostas/{numero}` devolve o mesmo corpo (`200`).

## Fase 2: contratar a proposta

`POST /api/contratacoes` recebe o número da proposta e os dados do cliente. **O documento não é informado**: ele já foi enviado na geração e é sempre o da proposta, o que impede cadastrar na contratação um CPF/CNPJ diferente do da proposta. O tipo de pessoa (PF/PJ) também vem da proposta e define quais campos de `cliente` são exigidos.

**PF** (CPF): `cliente` exige `nome`, `dataNascimento`, `sexo` e `estadoCivil`.

```bash
curl -i -X POST http://localhost:8080/api/contratacoes -H 'Content-Type: application/json' -d '{
  "numeroProposta": "b6e9e387-f5c1-4d4e-9a52-dfe9d2977c89",
  "cliente": {"nome": "Maria Silva", "dataNascimento": "1990-05-20", "sexo": "FEMININO", "estadoCivil": "SOLTEIRO"},
  "endereco": {"tipo": "RESIDENCIAL", "logradouro": "Rua A", "numero": "10", "cep": "01001000", "cidade": "São Paulo", "estado": "SP"},
  "contato": {"email": "maria@email.com", "telefones": [{"tipo": "CELULAR", "ddd": "11", "numero": "999990000"}]}
}'
```

**PJ** (CNPJ): `cliente` exige `razaoSocial` (`nomeFantasia` é opcional):

```json
"cliente": {"razaoSocial": "Empresa Ltda", "nomeFantasia": "Empresa"}
```

Valores aceitos: `sexo` (`MASCULINO`, `FEMININO`, `NAO_INFORMADO`), `estadoCivil` (`SOLTEIRO`, `CASADO`, `DIVORCIADO`, `VIUVO`), `endereco.tipo` (`RESIDENCIAL`, `COMERCIAL`) e `telefones[].tipo` (`RESIDENCIAL`, `COMERCIAL`, `CELULAR`).

Resposta (`201 Created`):

```json
{
  "numero": "0c1f7c1e-3b59-4f0b-9e43-6d0a7c3b2f11",
  "numeroProposta": "b6e9e387-f5c1-4d4e-9a52-dfe9d2977c89",
  "documento": "52998224725",
  "tipoPessoa": "PF",
  "cliente": {"nome": "Maria Silva", "dataNascimento": "1990-05-20", "sexo": "FEMININO", "estadoCivil": "SOLTEIRO"},
  "endereco": {"tipo": "RESIDENCIAL", "logradouro": "Rua A", "numero": "10", "cep": "01001000", "cidade": "São Paulo", "estado": "SP"},
  "contato": {"email": "maria@email.com", "telefones": [{"tipo": "CELULAR", "ddd": "11", "numero": "999990000"}]},
  "contratadaEm": "2026-10-07T17:00:00Z"
}
```

Após a contratação, a proposta passa para o status `CONTRATADA`. Consulta: `GET /api/contratacoes/{numero}`.

## Erros centralizados

Todos os erros usam a mesma estrutura em português, tratada por um único `@RestControllerAdvice`: `dataHora`, `status`, `mensagem` e, em validações, `errosCampos` (lista de `campo` e `motivo`, com todos os campos inválidos de uma vez).

| Situação | HTTP | `mensagem` |
| --- | --- | --- |
| Campo inválido ou ausente (inclui CPF/CNPJ inválido) | 400 | Falha na validação da requisição |
| Corpo da requisição malformado | 400 | Corpo da requisição malformado ou ilegível |
| Produto inexistente nos dois catálogos | 404 | Produto não encontrado |
| Proposta ou contratação inexistente | 404 | Proposta não encontrada / Contratação não encontrada |
| Rota inexistente | 404 | Recurso não encontrado |
| Proposta já contratada | 409 | A proposta já foi contratada |
| Produto existe só para o outro tipo de pessoa | 422 | Produto não disponível para o tipo de pessoa PF/PJ |
| Catálogo do tipo de pessoa ausente ou inválido | 503 | Catálogo de produtos PF/PJ indisponível ou inválido |
| Erro inesperado | 500 | Não foi possível processar a requisição |

O tipo de erro é identificado pelo `status` HTTP e pela `mensagem`.

Exemplo (`400 Bad Request`):

```json
{
  "dataHora": "2026-10-07T17:00:00Z",
  "status": 400,
  "mensagem": "Falha na validação da requisição",
  "errosCampos": [{"campo": "documento", "motivo": "documento deve ser um CPF ou CNPJ válido"}]
}
```

Um problema no catálogo PF afeta apenas requisições com CPF; um problema no catálogo PJ afeta apenas requisições com CNPJ. O outro catálogo continua atendendo normalmente.
