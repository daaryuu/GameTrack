# Padrões de Codificação – GameTrack

| | |
|---|---|
| **Versão** | 1.0 |
| **Aplica-se a** | Todo código Java do projeto (`src/` e `test/`), pela equipe inteira |
| **Base** | Convenções da linguagem Java, princípios SOLID e livro *Código Limpo* (Clean Code) |

## 1. Objetivo

Estas regras tornam o código do GameTrack fácil de ler, de testar e de alterar por qualquer integrante. Elas valem durante a programação e são conferidas na revisão de cada pull request (seção 5). São 10 regras, entre elas as duas obrigatórias: **padrão de notação** (C1) e **comentários e documentação** (C2).

## 2. Resumo das regras

| Código | Regra | Origem |
|---|---|---|
| C1 | Padrão de notação de nomes | Convenção Java (obrigatória) |
| C2 | Comentários e documentação | Javadoc e Clean Code (obrigatória) |
| C3 | Nomes que revelam a intenção | Clean Code |
| C4 | Métodos pequenos, com uma só tarefa | Clean Code e SRP |
| C5 | Sem números e textos mágicos | Clean Code |
| C6 | Cada classe tem uma responsabilidade e fica na sua camada | SOLID (S) |
| C7 | Depender de interfaces, não de implementações | SOLID (D) e (I) |
| C8 | Erros tratados com exceções | Clean Code |
| C9 | Consultas SQL sempre parametrizadas | Segurança (RNF) |
| C10 | Sem duplicação e sem código morto | Clean Code (DRY) |

## 3. Regras

### C1 – Padrão de notação de nomes (obrigatória)

| Elemento | Notação | Exemplo |
|---|---|---|
| Pacote | minúsculas, sem acentos nem `_` | `back.services` |
| Classe, interface, enum, record | `PascalCase`, substantivo | `ItemBiblioteca`, `JogoService` |
| Método | `camelCase`, começa com verbo | `cadastrarJogo`, `buscarPorId` |
| Variável e atributo | `camelCase`, substantivo | `tempoJogadoMin`, `jogoId` |
| Constante (`static final`) e valor de enum | `MAIÚSCULAS_COM_SUBLINHADO` | `TAMANHO_MINIMO_SENHA`, `SENDO_JOGADO` |
| Interface de comportamento | nome do papel, sem prefixo `I` | `JogoRepository` |

Idioma: os nomes do **domínio** ficam em português, como nos diagramas e no dicionário de dados (`Jogo`, `Categoria`, `tempoJogadoMin`). Os sufixos técnicos ficam em inglês (`Service`, `Controller`, `Repository`, `View`). Os nomes de classes seguem o diagrama de classes e o de pacotes, para o código e a documentação se corresponderem.

Os nomes não levam acentos, `ç` nem abreviações que só o autor entende.

**Como verificar:** algum nome foge da tabela acima? (deve ser não)

### C2 – Comentários e documentação (obrigatória)

1. Toda **classe** e todo **método público** tem comentário Javadoc (`/** ... */`) com uma frase do que faz e, quando houver, `@param`, `@return` e `@throws`.
2. O comentário explica o **porquê** e as regras de negócio. O código bem nomeado já diz o **quê**, então não se comenta o óbvio.
3. A regra de negócio comentada cita o requisito: `@see RF006`.
4. Não se mantém código comentado. Para recuperar uma versão antiga, usa-se o histórico do Git.
5. Comentários de linha (`//`) só para decisões não óbvias e sempre acima da linha comentada.

```java
/**
 * Exclui um jogo do catálogo.
 * Um jogo que está na biblioteca de algum usuário não pode ser excluído,
 * pois os itens perderiam a referência ao jogo (RF006).
 *
 * @param id identificador do jogo
 * @throws JogoEmUsoException se algum item da biblioteca usa o jogo
 */
public void excluir(int id) { ... }
```

Comentário a evitar:

```java
// soma 1 ao contador
contador++;
```

**Como verificar:** toda classe e todo método público têm Javadoc, e não existe bloco de código comentado? (deve ser sim)

### C3 – Nomes que revelam a intenção

- O nome diz o que o elemento é ou faz, sem precisar de comentário: `listarItensDoUsuario`, e não `listar2`.
- Sem nomes de uma letra, exceto contadores de laço curto (`i`).
- Booleanos e métodos que respondem sim ou não começam com `e`, `tem` ou `existe`: `existeItemDoJogo`.
- Um conceito tem um nome só, o mesmo do glossário do documento de requisitos (por exemplo, «item», e não «registro»).

**Como verificar:** dá para entender o método lendo só o nome e os parâmetros? (deve ser sim)

### C4 – Métodos pequenos, com uma só tarefa

- Um método tem no máximo **20 linhas** e faz **uma** coisa.
- No máximo **3 parâmetros**. Acima disso, agrupe os dados em um objeto (`ItemDTO`).
- Evite aninhar mais de **2 níveis** de `if` ou `for`. Prefira retornar cedo.
- Se o método precisa de um comentário para separar «partes», cada parte vira um método.

```java
// Evitar: valida, busca e grava no mesmo método
// Preferir:
public void cadastrar(int usuarioId, ItemDTO dto) {
    validarDados(dto);
    Jogo jogo = jogoRepository.buscarPorId(dto.jogoId());
    itemRepository.inserir(montarItem(usuarioId, jogo, dto));
}
```

**Como verificar:** algum método passa de 20 linhas ou de 3 parâmetros? (deve ser não)

### C5 – Sem números e textos mágicos

- Valores de regra de negócio vão para constantes nomeadas ou enums: `TAMANHO_MINIMO_SENHA = 8` (RNF005), `Status.BACKLOG`.
- As mensagens exibidas ao usuário ficam em um único lugar (classe `Mensagens`), com o texto igual ao dos requisitos (por exemplo, «Jogo cadastrado com sucesso!»).
- Plataforma e status são enums, nunca `String` solta.

**Como verificar:** algum literal numérico (exceto 0 e 1) ou mensagem aparece direto no meio da lógica? (deve ser não)

### C6 – Cada classe tem uma responsabilidade e fica na sua camada (SOLID: S)

Cada classe tem **um motivo para mudar**. Cada pacote tem um papel, conforme o diagrama de pacotes:

| Pacote | Responsabilidade | Não deve |
|---|---|---|
| `view` | Mostrar telas e receber ações do usuário | Conter regra de negócio ou acessar o banco |
| `cliente` | Enviar requisições ao servidor | Conter regra de negócio |
| `controllers` | Receber a requisição e chamar o service | Conter regra de negócio ou SQL |
| `services` | Regras de negócio e validações | Montar SQL ou desenhar telas |
| `repositories` | Acesso ao banco de dados | Conter regra de negócio |
| `model` | Entidades do domínio | Depender de qualquer outra camada |

A regra de dependência é a do diagrama de pacotes: `view` → `cliente` → `routes` → `controllers` → `services` → `repositories` → `model`. Nenhuma camada chama uma camada anterior, e nenhuma pula a seguinte.

**Como verificar:** algum `import` vai contra a direção das dependências do diagrama de pacotes? (deve ser não)

### C7 – Depender de interfaces, não de implementações (SOLID: D e I)

- O service recebe o repository pelo **construtor**, declarado como **interface** (`JogoRepository`), e não como a classe concreta que usa JDBC (`JogoRepositoryJdbc`).
- Interfaces são pequenas e específicas. Uma interface com métodos que a classe não precisa deve ser dividida.
- Uma subclasse substitui a classe-mãe sem quebrar o código que a usa: `Administrador` pode ser usado onde se espera um `Usuario` (princípio de Liskov).

Isso permite testar o service com um repository falso, sem banco de dados.

```java
public class JogoService {
    private final JogoRepository repository;

    public JogoService(JogoRepository repository) {
        this.repository = repository;
    }
}
```

**Como verificar:** algum service cria o repository com `new` ou declara o atributo com a classe concreta? (deve ser não)

### C8 – Erros tratados com exceções

- Regras violadas lançam exceções com nome que diz o problema: `JogoEmUsoException`, `DadosInvalidosException`.
- Métodos não retornam `null` nem códigos como `-1` para sinalizar erro. Quando pode não haver resultado, retornam `Optional`.
- O `catch` vazio é proibido. A exceção é tratada, relançada ou registrada.
- A mensagem para o usuário vem da exceção e é a mesma dos requisitos (por exemplo, «Jogo em uso em bibliotecas e não pode ser excluído!»).
- Recursos (conexões, `PreparedStatement`) são abertos com `try-with-resources`.

**Como verificar:** existe `catch` vazio, `return null` de método de busca ou `return -1`? (deve ser não)

### C9 – Consultas SQL sempre parametrizadas

- Todo SQL usa `PreparedStatement` com `?`. É proibido montar SQL concatenando texto vindo do usuário.
- O SQL fica só em `repositories`.
- A senha é gravada somente como hash bcrypt (RNF006) e nunca aparece em log, em mensagem de erro ou em commit.

```java
// Proibido
"SELECT * FROM Jogo WHERE titulo = '" + titulo + "'"
// Correto
"SELECT * FROM Jogo WHERE titulo = ?"
```

**Como verificar:** existe SQL montado com `+` ou `String.format`? (deve ser não)

### C10 – Sem duplicação e sem código morto

- Um trecho que aparece em dois lugares vira um método ou uma classe (princípio DRY).
- Importações, variáveis, métodos e classes sem uso são removidos.
- Quando uma mudança precisa ser feita em vários lugares ao mesmo tempo, é sinal de duplicação.

**Como verificar:** existe trecho repetido, `import` sem uso ou método que ninguém chama? (deve ser não)

## 4. Formatação

Complementam a C1:

- Indentação com **4 espaços**, sem tabulação.
- Linhas com no máximo **100 caracteres**.
- Chaves `{ }` sempre presentes, mesmo em `if` de uma linha, com a abertura na mesma linha da instrução.
- Um comando por linha e uma classe pública por arquivo, com o nome do arquivo igual ao da classe.
- O formatador do VSCode (`Format Document`) é executado antes de cada commit, para o estilo ser o mesmo em todas as máquinas.

## 5. Como as regras são aplicadas

1. **Durante a programação:** o autor confere as regras antes de abrir o pull request.
2. **Na revisão:** o revisor (outro integrante, regra P4 dos Padrões de Uso do Git) percorre a lista abaixo. Qualquer resposta diferente da esperada bloqueia o merge, e o autor corrige.
3. **Nos testes:** cada requisito do backlog tem tarefas de teste unitário e de validação. As regras C7 e C8 existem para tornar esses testes possíveis.

### Lista de verificação do pull request

| Código | Pergunta | Esperado |
|---|---|---|
| C1 | Algum nome foge da notação? | Não |
| C2 | Toda classe e todo método público têm Javadoc, e não há código comentado? | Sim |
| C3 | O nome de cada elemento revela a intenção? | Sim |
| C4 | Algum método passa de 20 linhas ou de 3 parâmetros? | Não |
| C5 | Há número ou mensagem solta na lógica? | Não |
| C6 | Algum `import` vai contra o diagrama de pacotes? | Não |
| C7 | Algum service depende de uma classe concreta de repository? | Não |
| C8 | Há `catch` vazio ou `return null` para erro? | Não |
| C9 | Há SQL concatenado? | Não |
| C10 | Há duplicação ou código sem uso? | Não |