# Regras de Verificação e Análise de Requisitos

| | |
|---|---|
| **Projeto** | GameTrack |
| **Pasta** | Padrões Adotados |
| **Equipe** | Rafael Alves Bortolotti e Pedro Saunders Alves Durazzo |
| **Versão** | 1.0 |

## 1. Objetivo

 Este documento é o padrão da equipe do GameTrack para escrever, verificar e analisar requisitos.

Todas as regras foram extraídas do capítulo 1 («Análise de Requisitos») do livro *Engenharia de Software Aplicada – Fundamentos*, de Rogério Magela (MAGELA, 2006), e cada uma indica a seção e a página de origem. As páginas seguem a numeração impressa do capítulo.

### 1.1 Como ler as regras

Cada regra tem um **código**, o **texto da regra**, a **base no livro** e um **teste**. O teste é sempre uma pergunta de sim ou não, respondida por contagem, por busca de palavras em uma lista fechada ou por comparação de campos. Assim, dois revisores diferentes chegam ao mesmo resultado, sem depender de opinião.

| Prefixo | Assunto | Seção do livro |
|---|---|---|
| **N** | Nomenclatura dos requisitos | 3, 4, 7.2.5 e 7.3.9 |
| **F** | Ficha (atributos) do requisito | 5 e 6 |
| **R** | Redação do requisito (especificação) | 7.2 |
| **Q** | Qualidade do conjunto de requisitos (análise) | 7.3 |
| **G** | Gerência de mudanças | 7.4 e 7.5 |

### 1.2 Números fixados pela equipe

Em alguns pontos o livro dá a orientação, mas não dá o número (por exemplo, «evite frases grandes»). Para a regra ficar objetiva, a equipe fixou os valores abaixo. Eles só mudam pelo procedimento da regra G5.

| Parâmetro | Valor | Por quê |
|---|---|---|
| **L** – máximo de palavras da descrição | 12 | O exemplo correto do livro tem 10 palavras (p. 18); a equipe permite uma folga de 2 |
| **H** – máximo de níveis no identificador | 3 | O livro mostra até três níveis, como em SR1.3.1 (Figura 1.2, p. 3) |
| **V** – máximo de passos de verificação | 5 | Mantém a verificação curta e executável |

## 2. Nomenclatura

O livro define dois tipos de requisito segundo o aspecto (p. 3): o **requisito funcional (RF)**, que define O QUE o software deve fornecer e seu comportamento na interação com o ambiente externo, e o **requisito não funcional (RNF)**, que restringe a forma como os requisitos funcionais fornecem seus serviços e se comportam durante a execução. A equipe adota as siglas RF e RNF, com número sequencial e, para os detalhamentos, a numeração hierárquica que o livro usa (URQ1, URQ1.1, p. 22).

| Código | Regra | Base | Teste (sim/não) |
|---|---|---|---|
| N1 | O identificador tem o formato `RF-nn` ou `RNF-nn`, com filhos `RF-nn.n` e `RF-nn.n.n`. Expressão: `^(RF\|RNF)-[0-9]{2}(\.[0-9]{1,2}){0,2}$` | 3 (p. 3); 7.3.9 (p. 22) | O identificador casa com a expressão? |
| N2 | O identificador é único e **nunca reaproveitado**. Requisito vetado continua na lista, com a Situação `Vetado` | 7.3.9 (p. 22); 5.2 (p. 12) | Algum identificador aparece duas vezes ou sumiu da lista? (deve ser não) |
| N3 | O identificador de um filho começa com o identificador do pai, seguido de ponto e do número do filho (RF-03.1 é filho de RF-03) | 7.2.5 (p. 19); Figura 1.2 (p. 3) | O prefixo do filho é igual ao identificador do campo Pai? |
| N4 | Cada caso de uso é identificado como `UC-nn`, com o mesmo número do RF macro que ele representa | 4 (p. 9-10) | O `UC-nn` tem o mesmo nn do RF macro correspondente? |
| N5 | O RNF informa o campo **Restringe** (um ou mais RF macro) e o campo **Categoria** (uma das 15 da seção 3 do livro) | 3 (p. 6-9); 6 (p. 13) | Os dois campos estão preenchidos e a categoria pertence à lista da seção 3? |

## 3. Ficha do requisito

O livro explica que os requisitos têm atributos porque **não são fixos no tempo** (seção 5, p. 11). Por isso, todo requisito do GameTrack é registrado com a ficha abaixo.

| Campo | Valores permitidos | Base |
|---|---|---|
| ID | Conforme N1 | 7.3.9 |
| Descrição | Conforme as regras R | 7.2 |
| Grupo | Conta, Biblioteca, Categorias, Tags, Reviews e Notas, Tempo Jogado | 7.2.7; 7.3.11 |
| Pai | ID de um RF existente, ou `—` se for requisito macro | 7.2.5 |
| Restringe | Apenas RNF: ID de um ou mais RF macro | 7.2.9; 6 |
| Categoria | Apenas RNF: Corretude, Confiabilidade, Robustez, Desempenho, Facilidade de Uso, Verificabilidade, Facilidade de Manutenção, Reutilização, Portabilidade, Compreensibilidade, Interoperabilidade, Produtividade, Desvio de Planejamento, Visibilidade, Segurança | 3.1 a 3.15 (p. 7-9) |
| Benefício | Crítico, Importante, Útil | 5.1 (p. 11) |
| Situação | Proposto, Incorporado, Aprovado, Vetado, **mais a data da última mudança** | 5.2 (p. 11-12) |
| Fonte | `Quem solicitou; motivo` | 5.3 (p. 12) |
| Esforço | Alto, Médio, Baixo | 5.4 (p. 12) |
| Risco | Alto, Médio, Baixo | 5.5 (p. 12) |
| Estabilidade | Alto, Médio, Baixo | 5.6 (p. 12) |
| Versão | `V1`, `V2`, ... (versão em que o requisito aparece pela primeira vez) | 5.7 (p. 12-13) |
| Coordenador | Nome de um integrante da equipe | 5.8 (p. 13) |
| Verificação | Até V passos numerados e o resultado esperado | 7.3.4 (p. 21) |
| Apoio técnico | Opcional: links, documentos ou vídeos | 5.9 (p. 13) |

| Código | Regra | Base | Teste (sim/não) |
|---|---|---|---|
| F1 | Todo requisito tem todos os campos obrigatórios preenchidos, e cada campo de lista contém um valor da lista | 5 (p. 11-13) | Existe campo vazio ou valor fora da lista? (deve ser não) |
| F2 | A data da última mudança de Situação está registrada, no formato `AAAA-MM-DD` | 5.2 (p. 11) | A data existe e casa com o formato? |
| F3 | Somente requisitos com Situação `Aprovado` e Versão de destino escolhida são implementados | 5.7 (p. 12) | Algum requisito em implementação não está `Aprovado`? (deve ser não) |

## 4. Regras de redação (seção 7.2 do livro)

Estas regras valem para a **descrição** de cada requisito. O livro as apresenta como um guia de especificação (p. 18-19), e a equipe as transformou em testes de sim ou não. As listas citadas (palavras proibidas, glossário, verbos e palavras de apoio) estão nos Apêndices A e B.

| Código | Regra | Base | Teste (sim/não) |
|---|---|---|---|
| R1 | A descrição começa com «O software DEVE» ou «O software não DEVE» | 7.2 (p. 18): convenção «O software DEVE...» | As primeiras palavras são uma das duas formas? |
| R2 | A descrição tem **uma única** ocorrência de «DEVE» (um requisito por vez) | 7.2.1 (p. 18): o exemplo incorreto junta dois requisitos com «E não DEVE» | Contando «DEVE», o resultado é 1? |
| R3 | A descrição não contém nenhuma palavra da lista de palavras proibidas (Apêndice B.1), sem diferenciar maiúsculas de minúsculas | 7.2.2 (p. 18) | A busca pelas palavras da lista retorna zero ocorrências? |
| R4 | A descrição tem no máximo **L** palavras e um único ponto final, no fim | 7.2.3 (p. 18): «evite frases grandes» | Palavras ≤ 12 e pontos finais = 1? |
| R5 | A descrição não contém pronome possessivo: meu, minha, meus, minhas, nosso, nossa, nossos, nossas, seu, sua, seus, suas | 7.2.3 (p. 18): o exemplo incorreto usa «de nossa estimada empresa» | A busca pelos possessivos retorna zero ocorrências? |
| R6 | Toda palavra da descrição está no glossário (Apêndice A), na lista de verbos ou na lista de palavras de apoio (Apêndice B.2 e B.3) | 7.2.4 (p. 18): vocabulário fechado, com glossário | Existe palavra fora das três listas? (deve ser não) |
| R7 | Um requisito com filho é detalhado apenas por filhos cujo identificador siga N3, e o identificador tem no máximo **H** níveis | 7.2.5 (p. 19): estrutura hierárquica | O pai existe, N3 vale e o número de níveis é ≤ 3? |
| R8 | A criação ou a alteração de um requisito atualiza a ficha (Benefício, Situação, Esforço, Risco etc.) **no mesmo momento**, sem deixar para depois | 7.2.6 (p. 19) | Todos os campos da ficha foram revisados na data da mudança? |
| R9 | Todo requisito tem um Grupo da lista fechada e o documento de requisitos é apresentado agrupado por Grupo | 7.2.7 (p. 19) | O campo Grupo está preenchido e o documento está ordenado por Grupo? |
| R10 | Todo RF macro (sem pai) corresponde a **um** caso de uso, e todo caso de uso corresponde a **um** RF macro | 7.2.8 (p. 19); 4 (p. 9-10), regras BR1 e BR3 | A relação entre RF macro e casos de uso é de um para um? |
| R11 | Todo RF macro é restringido por ao menos um RNF, e ao menos um deles é de Corretude ou de Confiabilidade. O RNF de Corretude informa o índice alto, médio ou baixo | 7.2.9 (p. 19); 3.1 e 3.2 (p. 7) | Existe RNF com Restringe = RF macro, de categoria Corretude ou Confiabilidade? |

## 5. Regras de análise (seção 7.3 do livro)

A análise garante a **consistência e a qualidade** dos requisitos já especificados (p. 19). O livro recomenda que ela seja feita por **outro analista**, e não por quem especificou os requisitos, porque a mesma mente repete os mesmos erros (p. 19). Os onze itens de qualidade do livro viraram os testes abaixo.

| Código | Item de qualidade | Base | Teste (sim/não) |
|---|---|---|---|
| Q1 | **Correta:** o requisito satisfaz uma necessidade real do usuário (a área C do diagrama de Venn deve ser nula) | 7.3.1 (p. 20) | O requisito está ligado a ao menos uma necessidade do Apêndice C e tem Fonte preenchida? |
| Q2 | **Completa:** não existe necessidade sem requisito (a área A deve estar dentro da área C) | 7.3.2 (p. 20) | Toda necessidade do Apêndice C tem ao menos um RF associado? |
| Q3 | **Sem ambiguidade:** o requisito tem uma só interpretação. Vale R2 e os conectores «E» e «OU» são proibidos (R3), sem exceção. Se a frase precisar de conector, ela é dividida em dois requisitos | 7.3.3 (p. 20-21) | R2 e R3 passam? |
| Q4 | **Verificável:** existe um processo finito para conferir o requisito. A Verificação tem no máximo **V** passos e um resultado esperado observável. Todo RNF traz um número com unidade ou o nome de um padrão ou produto externo. Nenhum adjetivo ou advérbio da lista B.1 aparece | 7.3.4 (p. 21): o livro troca «interface amigável» por «padrão de interface do KDE» | Passos ≤ 5, resultado esperado preenchido e RNF com número, unidade ou padrão nomeado? |
| Q5 | **Consistente:** (a) cada conceito tem **um só** termo no glossário, e os termos remetidos (coluna «Não usar») não aparecem nos requisitos; (b) dois requisitos com o mesmo verbo e o mesmo substantivo do glossário, um com «DEVE» e outro com «não DEVE», são listados e resolvidos por registro; (c) não existem dois requisitos com a mesma descrição | 7.3.5 (p. 21): o livro cita «Degrau tarifário / Região tarifária» e o par «DEVE enviar» / «não DEVE permitir» | Zero termos remetidos, zero pares sem resolução e zero descrições repetidas? |
| Q6 | **Compreensível:** a especificação serve ao usuário e ao desenvolvedor, por isso não usa notação formal. A descrição não contém os símbolos ∀ ∃ ⇒ → ∧ ∨ ¬ ∈ ⊂, e os termos do negócio estão no glossário | 7.3.6 (p. 21) | A busca pelos símbolos retorna zero e R6 passa? |
| Q7 | **Modificável:** o requisito é pequeno e conciso (R4) e, ao ser alterado, **mantém o identificador**; a mudança vira uma linha no histórico (G2) | 7.3.7 (p. 21-22) | R4 passa e o identificador é o mesmo entre as versões? |
| Q8 | **Rastreável para trás:** a origem do requisito está no campo Fonte, com quem solicitou e o motivo | 7.3.8 (p. 22): o exemplo do tempo de dez segundos pergunta «de onde surgiu esse requisito?» | O campo Fonte tem as duas partes preenchidas? |
| Q9 | **Rastreável para frente:** a matriz de rastreabilidade (RTM) liga cada RF ao caso de uso, aos RNF que o restringem e, conforme a fase, ao elemento de modelo e ao arquivo de código. Não existe requisito de teste separado: o teste vem dos RNF de Corretude e Confiabilidade | 7.3.9 (p. 22); 6 (p. 13) | Nenhuma célula das colunas exigidas pela fase atual está vazia? |
| Q10 | **Independente de projeto:** o requisito não impõe estrutura de projeto nem tecnologia. A descrição não contém termo das listas B.4 e B.5 | 7.3.10 (p. 22): o livro proíbe «o sistema deve ser composto pelos módulos A, B e C» | A busca pelos termos retorna zero ocorrências? |
| Q11 | **Organizada:** a especificação não é uma lista de frases. Ela segue duas dimensões: o **Grupo** (organização pelo problema) e a **hierarquia** (campo Pai). Todo Grupo tem ao menos um requisito | 7.3.11 (p. 22-23) | O documento está ordenado por Grupo e depois por ID, e nenhum Grupo está vazio? |

## 6. Procedimento de verificação e análise

1. **Quem revisa:** um integrante **diferente** do autor do requisito (7.3, p. 19).
2. **O que se aplica:** cada requisito é testado contra todas as regras N, F, R e Q aplicáveis. As regras Q2, Q9 e Q11 valem para o conjunto, e as demais, para cada requisito.
3. **Resultado:** o requisito só passa para `Aprovado` se a resposta for a esperada em **100%** das regras. Se alguma falhar, o revisor anota o código da regra violada e o autor corrige antes de uma nova revisão.
4. **Registro:** cada revisão é salva em `Padrões Adotados/registros/AAAA-MM-DD-revisao.md`, com a tabela abaixo.

| Requisito | N1-N5 | F1-F3 | R1-R6 | R7-R11 | Q1-Q4 | Q5-Q8 | Q9-Q11 | Revisor | Resultado |
|---|---|---|---|---|---|---|---|---|---|
| RF-01 | | | | | | | | | |

## 7. Gerência de mudanças (seções 7.4 e 7.5 do livro)

O livro alerta que, logo depois da aprovação, os requisitos já começam a se modificar, e que a gerência de mudanças é «a tarefa mais árdua e mais crítica» da engenharia de requisitos (p. 24). Por isso a equipe definiu a política de mudança **antes** de ter requisitos aprovados, como o livro pede (7.5.5, p. 25).

| Código | Regra | Base | Teste (sim/não) |
|---|---|---|---|
| G1 | Toda proposta de mudança é aberta como **Issue** do GitHub, com o rótulo `requisito` e os campos: ID do requisito, motivo, solicitante, data e requisitos impactados (obtidos da RTM). Pedidos por outro meio não valem | 7.4.1 (p. 23): o pedido de alteração é formalizado em um pequeno formulário; 7.5.3 (p. 25) | A Issue tem os cinco campos preenchidos? |
| G2 | Cada alteração gera uma linha no histórico do requisito, com data, solicitante, motivo e versão | 7.5.3 (p. 25): histórico de cada atualização, com datas, usuário solicitante e motivo | Existe uma linha no histórico para cada alteração? |
| G3 | RF macro que gere um **novo caso de uso** só passa a `Aprovado` com o aceite do professor da disciplina, registrado na Issue. O professor faz o papel do financiador citado no livro | 7.5.1 (p. 25): os macro que implicam novos casos de uso só entram com aprovação do financiador | O aceite do professor está anexado à Issue? |
| G4 | O modelo e o código só mudam depois que o requisito correspondente muda. A mensagem de todo commit que altera modelo ou código cita o ID do requisito, no formato `RF-nn: resumo (#issue)` | 7.5.2 (p. 25) | A mensagem do commit contém um ID que casa com N1? |
| G5 | Qualquer alteração **neste documento** (regras, parâmetros L, H e V, glossário, listas) também é aberta como Issue, aumenta a versão do documento e é revisada por outro integrante | 7.5.5 (p. 25) | A versão do documento aumentou e há Issue referenciada? |

### 7.1 Política de mudança do GameTrack

A seção 7.5.5 (p. 25) lista cinco perguntas que toda política de mudança deve responder. As respostas da equipe:

| Pergunta do livro | Resposta do GameTrack |
|---|---|
| Quais ferramentas, técnicas e convenções de controle de versões e de requisitos serão usadas? | Git e GitHub para o controle de versões, e arquivos Markdown na pasta `Padrões Adotados/` para o documento de requisitos |
| Como novos requisitos e alterações serão propostos, analisados, negociados e comunicados? | Propostos por Issue (G1), analisados pela revisão da seção 6, negociados nos comentários da Issue e comunicados pelo fechamento da Issue e pelo commit (G4) |
| Quais estados serão usados e quem muda o estado? | Os estados são os valores de Situação (Proposto, Incorporado, Aprovado, Vetado), e quem muda o estado é o Coordenador do requisito |
| Como avaliar o impacto de uma mudança? | Consultando a RTM (Q9): todo RF, caso de uso e RNF ligado ao requisito alterado entra na Issue |
| Como a mudança será refletida nos planos e metas? | Pelo campo Versão (F1) e pelo quadro de tarefas do projeto |

## 8. Exemplos de verificação

Os exemplos abaixo foram conferidos contra as regras N1, R1 a R6, Q4, Q5, Q6 e Q10 por um script, e o resultado confere com a coluna «Resultado».

### 8.1 Requisitos que passam

| ID | Descrição | Palavras | Resultado |
|---|---|---|---|
| RF-01 | O software DEVE permitir o cadastro de jogos. | 8 | Passa |
| RF-01.1 | O software DEVE exigir uma plataforma para cada jogo. | 9 | Passa |
| RF-01.2 | O software DEVE exigir um gênero para cada jogo. | 9 | Passa |
| RF-02 | O software DEVE permitir criar categorias. | 6 | Passa |
| RF-03 | O software DEVE permitir aplicar uma tag a um jogo. | 10 | Passa |
| RF-04 | O software DEVE permitir o registro de reviews. | 8 | Passa |
| RF-04.1 | O software DEVE exigir nota de 1 a 5 estrelas na review. | 12 | Passa |
| RF-05 | O software DEVE permitir o registro do tempo jogado. | 9 | Passa |
| RNF-01 | O software DEVE listar a biblioteca em no máximo 2 segundos. | 11 | Passa (Q4: número com unidade; categoria Desempenho) |
| RNF-02 | O software DEVE exigir senha com no mínimo 8 caracteres. | 10 | Passa (Q4: número com unidade; categoria Segurança) |

### 8.2 Requisitos que não passam

| Descrição | Regras violadas | Motivo |
|---|---|---|
| RF3 – O software deverá ser amigável e permitir cadastrar jogos e plataformas. | N1, R1, R2, R3, R6, Q4 | ID sem hífen e sem dois dígitos; não usa «DEVE»; contém «amigável» e «e»; «deverá» e «ser» fora das listas |
| RF-06 – O software DEVE permitir o registro de nossas resenhas. | R5, R6, Q5 | Possessivo «nossas»; «resenhas» é termo remetido ao glossário (o termo é «review») |
| RF-07 – O software DEVE gravar jogos em tabela MySQL. | R6, Q10 | «gravar» fora da lista de verbos; «tabela» e «MySQL» impõem o projeto |
| RF-08 – O software DEVE permitir excluir jogos. | Q5 (b) | Entra em par com o RF-09 |
| RF-09 – O software não DEVE permitir excluir jogos. | Q5 (b) | Contradiz o RF-08; o par só segue adiante com registro de resolução |

## 9. Referência e origem das regras

MAGELA, Rogério. *Engenharia de Software Aplicada – Fundamentos*. Rio de Janeiro: Alta Books, 2006. Capítulo 1: Análise de Requisitos, seções 3, 4, 5, 6, 7.2, 7.3, 7.4 e 7.5.

| Vem do livro | Definido pela equipe |
|---|---|
| As regras R1 a R11 (seção 7.2), Q1 a Q11 (seção 7.3), as categorias de RNF (seção 3), os atributos (seção 5), a necessidade de ligar RF macro e caso de uso (seção 4) e os princípios de gerência de mudança (seções 7.4 e 7.5) | Os números L, H e V; as siglas e o formato dos identificadores (N1 a N5); as listas dos Apêndices A e B; os grupos de requisitos; os testes de sim ou não; a forma de registro da revisão; as regras G1 a G5 |

---

## Apêndice A – Glossário

Todo termo usado nas descrições dos requisitos está aqui (R6). Quando um conceito tem vários nomes, só **um** é permitido (Q5); os outros ficam na coluna «Não usar».

| Termo | Definição | Não usar |
|---|---|---|
| Software | O sistema web GameTrack | sistema, aplicação, site, GameTrack |
| Usuário | Pessoa que usa o software e é dona de uma biblioteca | jogador, gamer, cliente |
| Conta | Identificação do usuário no software | perfil, login |
| Senha | Segredo que protege a conta | — |
| Jogo | Título de videogame guardado na biblioteca | título, game |
| Biblioteca | Conjunto de todos os jogos de um usuário, de todas as plataformas | coleção, acervo |
| Plataforma | Console ou computador em que o jogo é jogado | console |
| Gênero | Classificação do jogo por estilo | estilo, tipo |
| Categoria | Grupo de jogos criado pelo próprio usuário | lista, pasta |
| Tag | Marcador de status aplicado a um jogo | etiqueta, rótulo |
| Status | Estado de conclusão de um jogo, indicado por uma tag | — |
| Review | Texto que o usuário escreve sobre um jogo concluído | resenha, crítica, comentário |
| Nota | Valor de 1 a 5 estrelas dado a um jogo concluído | avaliação, pontuação |
| Tempo jogado | Total de tempo que o usuário dedicou a um jogo | horas jogadas |
| Cadastro | Inclusão de um jogo, de uma categoria ou de uma conta no software | inclusão, adição |
| Registro | Inclusão de review, nota ou tempo jogado de um jogo | lançamento |
| Consulta | Exibição de dados já guardados | busca, pesquisa |
| Alteração | Mudança de dados já guardados | edição, atualização |
| Exclusão | Remoção de dados guardados | remoção |
| Navegador | Programa usado pelo usuário para acessar o software | browser |

O termo «Situação» é reservado ao atributo do requisito (seção 3). Para o estado de conclusão de um jogo usa-se «Status».

## Apêndice B – Listas fechadas

### B.1 Palavras proibidas (R3, Q3 e Q4)

**Lista do livro (7.2.2, p. 18):** E, OU, SOMENTE SE, EXCETO, SE NECESSÁRIO, MAS, CONTUDO, ENTRETANTO, USUALMENTE, GERALMENTE, FREQÜENTEMENTE (hoje escrita «frequentemente»), TIPICAMENTE, AMIGÁVEL, VERSÁTIL, FLEXÍVEL, APROXIMADAMENTE, TÃO LOGO QUANTO POSSÍVEL, TALVEZ, PROVAVELMENTE.

**Palavras de mesma ideia adotadas pela equipe** (o livro pede também as «palavras que forneçam a mesma ideia»): porém, todavia, normalmente, eventualmente, quase sempre, rápido, rápida, rapidamente, fácil, simples, intuitivo, intuitiva, bonito, bonita, divertido, divertida, agradável, moderno, moderna, bom, boa, melhor, vários, muitos, poucos, etc.

### B.2 Verbos permitidos (R6)

permitir, exigir, impedir, rejeitar, bloquear, exibir, listar, informar, registrar, cadastrar, consultar, alterar, excluir, criar, aplicar, atribuir, escrever, organizar, filtrar, agrupar, ordenar, calcular, somar, validar, autenticar, armazenar, responder, funcionar.

### B.3 Palavras de apoio livres (R6)

- **Artigos:** o, a, os, as, um, uma, uns, umas.
- **Preposições e contrações:** de, do, da, dos, das, em, no, na, nos, nas, para, por, com, sem, até, ao, à, entre, sobre.
- **Outras:** não, que, cada, todo, toda, todos, todas, mesmo, mesma, diferente.
- **Números e unidades:** algarismos, segundo, segundos, minuto, minutos, caractere, caracteres, estrela, estrelas.
- **Limites:** máximo e mínimo, somente com um número na mesma frase.
- **Nomes das tags:** Concluído, Sendo Jogado, Abandonado, Platinado.
- **Padrões ou produtos externos (somente em RNF, Q4):** Google Chrome, Mozilla Firefox, Microsoft Edge.

### B.4 Termos de estrutura de projeto proibidos (Q10)

módulo, componente, subsistema, camada, classe, objeto, tabela, banco de dados, arquitetura, API, servidor, framework.

### B.5 Tecnologias proibidas nos requisitos (Q10)

Java, MySQL, SQL, HTML, CSS, JavaScript, Spring, Maven, Git, GitHub, VSCode, Docker.

## Apêndice C – Necessidades do usuário (Q1 e Q2)

Cada necessidade precisa de ao menos um RF. Elas foram tiradas das funcionalidades do README do projeto (seção 1.3).

| ID | Necessidade | Origem |
|---|---|---|
| NEC-01 | Manter uma biblioteca única com jogos de todas as plataformas | README, seção 1.2 |
| NEC-02 | Cadastrar, consultar, alterar e excluir jogos da biblioteca | README, seção 1.3 |
| NEC-03 | Organizar os jogos por plataforma, gênero e status | README, seção 1.3 |
| NEC-04 | Criar categorias personalizadas | README, seção 1.3 |
| NEC-05 | Aplicar tags de status aos jogos | README, seção 1.3 |
| NEC-06 | Escrever reviews e dar notas de 1 a 5 estrelas a jogos concluídos | README, seção 1.3 |
| NEC-07 | Registrar o tempo jogado em cada jogo | README, seção 1.3 |
| NEC-08 | Identificar o usuário para manter sua biblioteca individual | Equipe (a confirmar) |