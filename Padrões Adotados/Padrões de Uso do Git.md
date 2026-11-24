# Padrões de Uso do Git – GameTrack

| | |
|---|---|
| **Versão** | 1.0 |
| **Aplica-se a** | Todo o repositório `GameTrack`, por todos os integrantes da equipe |

## 1. Objetivo

Estas regras servem para que o histórico do repositório seja legível, para que ninguém sobrescreva o trabalho do outro e para que cada mudança possa ser ligada a um requisito. Elas cobrem cinco assuntos: branches, mensagens de commit, pull requests, organização de pastas e o arquivo `.gitignore`.

## 2. Branches

### 2.1 Regras

| Código | Regra |
|---|---|
| B1 | A branch `main` guarda sempre a versão estável. Ninguém faz commit direto nela: toda mudança entra por pull request. |
| B2 | Todo trabalho acontece em uma branch própria, criada a partir da `main` atualizada. |
| B3 | Uma branch trata de **um assunto** (um requisito, uma correção, um documento). Branch com vários assuntos misturados deve ser dividida. |
| B4 | A branch é apagada depois do merge. |
| B5 | A equipe é pequena, por isso não usamos branch `develop`. A `main` e as branches de trabalho bastam. |

### 2.2 Nome da branch

Formato: `tipo/identificador-descricao-curta`, em minúsculas, sem acentos e com hífens no lugar de espaços. O `tipo` é o mesmo da mensagem de commit (seção 3.2). Quando houver requisito, o identificador é o código dele.

| Exemplo | Quando usar |
|---|---|
| `feat/rf003-cadastrar-jogo` | Implementação de um requisito funcional |
| `feat/rnf005-tamanho-senha` | Implementação de um requisito não funcional |
| `fix/rf008-filtro-por-status` | Correção de erro encontrado em teste |
| `docs/diagramas-de-sequencia` | Documentação |
| `test/rf007-teste-unitario` | Testes unitários ou de validação |
| `chore/gitignore` | Configuração do projeto |

### 2.3 Ciclo de trabalho

```bash
git switch main
git pull                                 # atualiza a main
git switch -c feat/rf003-cadastrar-jogo  # cria a branch de trabalho
# ... trabalha e faz commits pequenos ...
git push -u origin feat/rf003-cadastrar-jogo
# abre o pull request no GitHub (seção 4)
```

Se a `main` mudou enquanto a branch estava aberta, traga as mudanças antes do pull request: `git merge main` dentro da branch de trabalho, resolvendo os conflitos localmente.

## 3. Mensagens de commit

O padrão é baseado em [padroes-de-commits](https://github.com/iuricode/padroes-de-commits), que segue o Conventional Commits, com a adaptação do **escopo** para o código do requisito.

### 3.1 Formato

```
:emoji: tipo(escopo): Descrição curta

Corpo opcional: o que mudou e por quê.

Rodapé opcional: Closes #12
```

| Parte | Regra |
|---|---|
| `:emoji:` | Código do emoji do tipo (tabela abaixo). |
| `tipo` | Um dos tipos da tabela abaixo. |
| `(escopo)` | Opcional. Código do requisito (`RF003`, `RNF005`) ou área (`readme`, `docs`). Liga o commit à rastreabilidade. |
| Descrição | Até **50 caracteres**, começando com letra maiúscula, sem ponto final, dizendo **o que** mudou (por exemplo, «Cadastro de jogo no catálogo»). |
| Corpo | Separado da primeira linha por uma linha em branco, com linhas de até 72 caracteres. Explica o motivo e os impactos. |
| Rodapé | Referência à issue do kanban: `Closes #n` (fecha a issue ao entrar na `main`) ou `Refs #n` (só cita). |

### 3.2 Tipos

| Tipo | Emoji | Use para |
|---|---|---|
| `feat` | ✨ `:sparkles:` | Nova funcionalidade (um requisito, ou parte dele) |
| `fix` | 🐛 `:bug:` | Correção de erro |
| `docs` | 📚 `:books:` | Documentação (README, requisitos, diagramas, regras) |
| `test` | ✅ `:white_check_mark:` | Criação ou alteração de testes |
| `refactor` | ♻️ `:recycle:` | Reorganização do código sem mudar o comportamento |
| `style` | 👌 `:ok_hand:` | Formatação, sem mudar o código |
| `perf` | ⚡ `:zap:` | Melhoria de desempenho |
| `build` | 📦 `:package:` | Dependências e arquivos de build |
| `chore` | 🔧 `:wrench:` | Configurações do projeto (por exemplo, `.gitignore`) |
| `remove` | 🗑️ `:wastebasket:` | Exclusão de arquivos ou funcionalidades |
| `init` | 🎉 `:tada:` | Commit inicial do repositório |

### 3.3 Exemplos

```
:tada: init: Commit inicial
:books: docs(readme): Seção de organização do projeto
:books: docs: Diagramas de sequência dos RF003 a RF010
:sparkles: feat(RF003): Cadastro de jogo no catálogo
:sparkles: feat(RF007): Cadastro de item na biblioteca
:bug: fix(RF008): Filtro por status na biblioteca
:white_check_mark: test(RF005): Teste unitário da alteração de jogo
:recycle: refactor(RF004): Busca por filtro no repositório
:wrench: chore: Regras do gitignore
```

Exemplo com corpo e rodapé:

```
:bug: fix(RF006): Bloqueio de exclusão de jogo em uso

A exclusão passava mesmo quando algum usuário tinha o jogo na
biblioteca. O serviço agora consulta os itens antes de excluir.

Closes #34
```

### 3.4 O que evitar

- Mensagens vagas, como «ajustes», «arrumando coisas» ou «teste».
- Commits que misturam assuntos (por exemplo, uma funcionalidade e a correção de um documento).
- Commits gigantes. Prefira vários commits pequenos, cada um com um motivo.
- Versionar arquivos gerados, senhas ou configurações da máquina (veja a seção 6).

## 4. Pull requests

| Código | Regra |
|---|---|
| P1 | Todo merge na `main` passa por pull request, aberto a partir da branch de trabalho. |
| P2 | O título do pull request segue o padrão da mensagem de commit (seção 3.1). |
| P3 | A descrição diz **o que** mudou, **por quê** e **como testar**, e cita a issue com `Closes #n`. |
| P4 | Outro integrante revisa e aprova antes do merge. Ninguém aprova o próprio pull request. |
| P5 | O merge usa a opção «Create a merge commit», que preserva os commits da branch no histórico. |
| P6 | Depois do merge, a branch de trabalho é apagada. |

Modelo de descrição:

```markdown
## O que mudou
Cadastro de jogo no catálogo (RF003).

## Por quê
Atende ao RF003: o administrador precisa cadastrar jogos no catálogo.

## Como testar
1. Entrar como administrador.
2. Abrir o catálogo e escolher «Cadastrar jogo».
3. Informar os dados e salvar. O jogo deve aparecer na lista.

Closes #12
```

## 5. Organização de pastas

A regra geral é: **código em `src/`, testes em `test/`, documentação nas demais pastas**. Cada tipo de arquivo tem um lugar só.

```
GameTrack/
├── README.md                      Apresentação do projeto e instruções
├── .gitignore                     Arquivos que o Git ignora
├── docs/
│   └── diagramas/
│       ├── diagrama_de_classes.png
│       ├── diagrama_de_pacotes.png
│       └── sequencia/             Um diagrama por funcionalidade (RF003 a RF010)
├── Requisitos/                    Documento de requisitos
├── Padrões Adotados/              Regras de verificação, padrões de Git e registros
│   └── registros/                 Uma revisão de requisitos por data
├── src/
│   ├── front/                     Interface Swing
│   │   ├── view/
│   │   ├── componentes/
│   │   ├── cliente/
│   │   └── dto/
│   └── back/                      Servidor Java
│       ├── routes/
│       ├── controllers/
│       ├── services/
│       ├── repositories/
│       ├── model/
│       ├── config/
│       └── seguranca/
├── sql/                           Scripts de criação e carga do banco (MySQL)
└── test/                          Testes unitários e de validação, espelhando src/
```

| Código | Regra |
|---|---|
| D1 | Código-fonte só em `src/`. As pastas `front` e `back` e os pacotes internos seguem o diagrama de pacotes. |
| D2 | Testes só em `test/`, na mesma estrutura de pacotes do código testado. |
| D3 | Documentos (requisitos, regras, relatórios) ficam em `Requisitos/` e `Padrões Adotados/`. Imagens e diagramas ficam em `docs/diagramas/`. |
| D4 | Scripts SQL ficam em `sql/`, numerados na ordem de execução (`01_criar_tabelas.sql`). |
| D5 | Pastas novas têm nome em minúsculas, sem acentos nem espaços. As pastas `Requisitos` e `Padrões Adotados` já existem e mantêm o nome. |
| D6 | Arquivos de imagem têm nome sem espaços nem acentos, para os links do README não quebrarem. |

## 6. Arquivo `.gitignore`

O arquivo `.gitignore`, na raiz, lista o que o Git não deve versionar. Ele está dividido em blocos comentados:

| Bloco | O que ignora | Por quê |
|---|---|---|
| Java | `*.class`, `*.jar`, `*.war`, `*.zip`… | São gerados na compilação. Qualquer um recompila a partir do código. |
| Build | `target/`, `build/`, `out/`, `bin/`, `.gradle/` | Pastas de saída do Maven e do Gradle. |
| Logs | `*.log`, `hs_err_pid*` | Registros de execução e de falhas da JVM. |
| IDE | `.vscode/*` (exceto `settings.json` e `extensions.json`), `.classpath`, `.project`, `.settings/`, `.idea/` | Configurações da máquina de cada pessoa. Só as configurações do projeto são compartilhadas. |
| Sistema | `.DS_Store`, `Thumbs.db`, `desktop.ini` | Lixo criado pelo sistema operacional. |
| Documentos | `~$*.docx`, `.~lock.*#`, `*.tmp` | Arquivos temporários criados pelo Word e pelo LibreOffice enquanto o documento está aberto. |
| Segredos | `.env`, `config.properties`, `*.pem`, `*.key` | Senhas e chaves **nunca** vão para o repositório, que é público. |
| Banco | `*.dump`, `*.bak` | Cópias locais do banco. Os scripts de `sql/` continuam versionados. |

### 6.1 Configuração do banco

A senha do MySQL fica em `config.properties`, que é ignorado. O repositório traz o modelo `config.example.properties`, com os campos e valores de exemplo, sem senha. Cada integrante copia o modelo, renomeia para `config.properties` e preenche os próprios dados.

### 6.2 Como conferir se uma regra funciona

```bash
git status                                    # arquivos ignorados não aparecem
git check-ignore -v caminho/do/arquivo        # mostra qual regra ignorou o arquivo
git add -A -n                                 # simula o add e lista o que seria versionado
```

Exemplo da demonstração, com arquivos criados de propósito:

| Arquivo | Resultado | Regra |
|---|---|---|
| `src/back/Main.java` | versionado | – |
| `src/back/Main.class` | ignorado | `*.class` |
| `target/app.jar` | ignorado | `target/` |
| `.vscode/launch.json` | ignorado | `.vscode/*` |
| `.vscode/settings.json` | versionado | exceção `!.vscode/settings.json` |
| `config.properties` | ignorado | `config.properties` |
| `config.example.properties` | versionado | – |
| `~$Requisitos.docx` | ignorado | `~$*.docx` |
| `sql/criar_banco.sql` | versionado | – |

Um arquivo que **já foi versionado** não passa a ser ignorado ao entrar no `.gitignore`. Nesse caso, remova-o do controle sem apagá-lo do disco: `git rm --cached caminho/do/arquivo`.

## 7. Issues e kanban

| Código | Regra |
|---|---|
| K1 | Cada item do kanban é uma issue, e o nome da issue começa com o código do requisito (por exemplo, `[RF003] Cadastrar jogo no catálogo`). |
| K2 | A branch, os commits e o pull request citam o código do requisito e a issue (`Closes #n`). Assim é possível ir do requisito até o código e voltar. |
| K3 | O pull request só é aprovado depois de o item chegar a «Teste de Sistema» no kanban. |

## 8. Versões e tags

| Código | Regra |
|---|---|
| V1 | As entregas (releases) recebem uma tag no formato `vX.Y.Z` (por exemplo, `v1.0.0`), criada na `main`. |
| V2 | Cada release tem uma descrição com as funcionalidades entregues e os requisitos atendidos. |