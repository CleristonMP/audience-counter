# Plano de ação da revisão técnica

## Objetivo

Corrigir os riscos de crash e perda de dados encontrados na revisão do aplicativo, melhorar o uso de coroutines e null safety e evoluir a arquitetura sem concentrar todas as mudanças em uma única entrega.

## Estratégia de execução

As correções serão entregues em fases pequenas e verificáveis. Cada bug corrigido deve receber um teste de regressão. As três primeiras fases tratam os riscos imediatos; as demais consolidam a arquitetura e reduzem a duplicação.

## Acompanhamento

| Fase | Escopo | Estado |
| --- | --- | --- |
| 0 | Rede de segurança e testes de caracterização | Em andamento |
| 1 | Persistência resiliente | Concluída |
| 2 | Salvamento assíncrono confiável | Concluída |
| 3 | ViewModel e estado único | Concluída |
| 4 | Restauração de estado | Concluída |
| 5 | Tipagem e validação | Concluída |
| 6 | Correção da contagem por fileiras | Pendente |
| 7 | Legibilidade e redução de duplicação | Pendente |
| 8 | Validação final | Pendente |

## Fase 0 — Criar uma rede de segurança

### Objetivo

Registrar o comportamento válido atual e preparar testes de regressão antes das mudanças de produção.

### Ações

- Corrigir o teste de data para executar `getCurrentFormattedDate()` em vez de uma implementação duplicada no teste.
- Cobrir o DataStore com testes de persistência, limpeza, ordenação e limite de 100 registros.
- Cobrir a associação entre o número da fileira e sua contagem.
- Adicionar testes para registros vazios, incompletos, com quantidade inválida e listas parcialmente corrompidas.
- Definir que registros inválidos devem ser ignorados sem descartar registros válidos.
- Confirmar compilação e testes unitários antes do início da Fase 1.

### Critérios de conclusão

- A implementação de produção, e não uma cópia criada no teste, é exercitada.
- Os comportamentos válidos atuais estão caracterizados.
- Cada risco conhecido possui um caso de regressão planejado ou implementado.
- A suíte que representa comportamentos já suportados permanece verde.

## Fase 1 — Blindar a persistência

### Objetivo

Impedir que dados inválidos ou falhas de leitura encerrem a coleta do DataStore ou derrubem a aplicação.

### Ações

- Extrair codificação e decodificação para funções testáveis.
- Substituir destructuring e `toInt()` por parsing defensivo com `split` limitado e `toIntOrNull()`.
- Ignorar apenas registros inválidos, preservando os demais.
- Tratar `IOException` no fluxo do DataStore sem capturar `CancellationException`.
- Avaliar JSON ou Proto DataStore como substituição futura da serialização por delimitadores.

### Critérios de conclusão

- Conteúdo vazio, incompleto ou inválido não causa crash.
- Registros válidos continuam disponíveis quando outro registro está corrompido.
- Todos os cenários possuem testes unitários.

### Decisão técnica

O formato legado com delimitadores foi preservado nesta fase para manter compatibilidade com os dados já instalados. A migração para JSON ou Proto DataStore será reavaliada junto da introdução de `AudienceRecord`, na Fase 5; até lá, a leitura defensiva impede que registros malformados interrompam o fluxo.

## Fase 2 — Corrigir o fluxo assíncrono de salvamento

### Objetivo

Evitar perda de contagem, cliques concorrentes e falhas silenciosas durante gravações.

### Ações

- Manter `isSaving` ativo durante toda a operação suspensa.
- Zerar a contagem somente após sucesso da persistência.
- Preservar a contagem e apresentar erro quando a gravação falhar.
- Aplicar o mesmo fluxo a salvar, salvar o total das fileiras e limpar o histórico.
- Substituir gravações de listas completas por operações atômicas, como `addAudience` e `clearAudiences`.
- Gerar o timestamp no momento do salvamento.

### Critérios de conclusão

- Falhas não apagam dados da tela.
- Cliques rápidos não iniciam operações duplicadas.
- O timestamp representa o momento efetivo do salvamento.

### Decisão técnica

Até a introdução do ViewModel na Fase 3, `AudienceCounterWithTabs` coordena uma única operação de persistência por vez e apresenta erros por `Snackbar`. O DataStore expõe operações atômicas de adição e limpeza; a UI deixa de montar e sobrescrever a lista completa. A contagem só é zerada pelo callback de sucesso da gravação.

## Fase 3 — Introduzir ViewModel e estado único

### Objetivo

Separar a apresentação das regras de negócio e da persistência.

### Ações

- Criar `AudienceRepository` e `AudienceCounterViewModel`.
- Representar a tela com um `AudienceCounterUiState` imutável.
- Expor estado por `StateFlow` e coletá-lo com `collectAsStateWithLifecycle()`.
- Executar gravações com `viewModelScope`.
- Remover `lifecycleScope` e conhecimento do DataStore dos callbacks da UI.
- Modelar incrementos, decrementos, salvamento, limpeza e fileiras como eventos claros.

### Critérios de conclusão

- A Activity apenas configura e apresenta a tela.
- Composables não iniciam coroutines de persistência.
- Regras de negócio podem ser testadas sem Compose.

### Decisão técnica

O fluxo passou a usar `AudienceRepository`, `AudienceCounterViewModel`, `AudienceCounterUiState` e eventos `AudienceCounterAction`. O ViewModel concentra contagem direta, progresso por fileiras, aba selecionada, histórico, carregamento e erros; gravações são executadas com `viewModelScope`. A UI coleta o `StateFlow` com `collectAsStateWithLifecycle()` e mantém localmente apenas estado estritamente visual, como a abertura dos diálogos de confirmação.

## Fase 4 — Preservar estado durante recriações

### Objetivo

Evitar perda da contagem ao girar a tela, alternar abas ou recriar a Activity.

### Ações

- Manter o estado operacional no ViewModel.
- Usar `SavedStateHandle` para contagens, fileira atual, fileiras concluídas e aba selecionada.
- Reservar `rememberSaveable` para estado estritamente visual e local.
- Definir se sessões incompletas devem sobreviver ao encerramento do processo.

### Critérios de conclusão

- Rotação e troca de abas preservam o progresso.
- O comportamento após encerramento do processo está definido e testado.

### Decisão técnica

Contagem direta, aba selecionada, quantidade e posição das fileiras, pessoas na fileira atual e fileiras concluídas são mantidas no `SavedStateHandle`. Assim, o progresso sobrevive a mudanças de configuração e à recriação do processo enquanto a tarefa do aplicativo puder ser restaurada pelo Android. `isSaving` e erros permanecem transitórios. Uma sessão incompleta não é persistida como dado durável após remoção explícita da tarefa, limpeza de dados ou novo início independente; essa escolha evita misturar rascunhos com o histórico confirmado.

## Fase 5 — Melhorar tipagem e null safety

### Objetivo

Tornar estados inválidos difíceis de representar e eliminar formatos ambíguos.

### Ações

- Substituir `Pair<String, Int>` por `AudienceRecord`.
- Persistir timestamp em formato estável e formatá-lo somente na UI.
- Substituir índices de abas por um enum.
- Validar limites de contagem e número de fileiras.
- Proteger incrementos e somas contra overflow.
- Remover o `lateinit` após introduzir a injeção das dependências.

### Critérios de conclusão

- A camada de domínio não expõe `Pair<String, Int>`.
- Entradas inválidas não alteram o estado.
- Conversões numéricas inseguras foram removidas.

### Decisão técnica

O histórico passa a usar `AudienceRecord`, com timestamp em milissegundos desde a época Unix e contagem positiva. A serialização continua aceitando datas do formato legado para preservar instalações existentes, mas toda nova gravação usa o timestamp numérico estável; a formatação localizada ocorre somente na UI. As abas são representadas por `AudienceCounterTab`, o número de fileiras é limitado a 10.000 e contadores não ultrapassam `Int.MAX_VALUE`. Entradas numéricas inválidas são ignoradas, totais são calculados como `Long` antes da conversão validada e nenhum `lateinit` permanece no código de produção.

## Fase 6 — Corrigir a contagem por fileiras

### Objetivo

Associar corretamente cada contagem à sua fileira e simplificar o componente.

### Ações

- Fazer `RowCountDisplay` receber `List<Int>` em vez de `SnapshotStateList<Int>`.
- Corrigir a inversão do índice sem inversão correspondente dos valores.
- Caso a ordem visual seja reversa, inverter pares de índice e valor juntos.
- Testar listas com pelo menos três valores distintos.

### Critérios de conclusão

- `[10, 20, 30]` é exibido como fileiras 1, 2 e 3 com os respectivos valores.
- Os testes verificam todos os itens exibidos.

## Fase 7 — Reduzir duplicação e melhorar legibilidade

### Objetivo

Evitar regras duplicadas entre portrait e landscape e tornar as APIs dos componentes mais claras.

### Ações

- Extrair histórico, controles, ações de salvamento e resumo em componentes compartilhados.
- Manter nos layouts de orientação apenas a composição espacial.
- Usar parâmetros nomeados nas chamadas longas.
- Padronizar nomes de estado e eventos.
- Mover cores fixas para o tema.
- Remover comentários redundantes e mutações de estado nos previews.

### Critérios de conclusão

- Regras de negócio não aparecem nos layouts de orientação.
- Componentes recebem modelos imutáveis e eventos explícitos.
- Correções compartilhadas não precisam ser repetidas.

## Fase 8 — Ampliar testes e realizar validação final

### Objetivo

Confirmar que a evolução arquitetural não introduziu regressões.

### Ações

- Cobrir ViewModel, repositório, parsing, restauração e falhas assíncronas com testes unitários.
- Cobrir salvamento, erro, rotação, troca de abas e orientações com testes Compose.
- Executar testes unitários, testes instrumentados, compilação debug e lint.
- Fazer validação manual com dados persistidos inválidos e falha simulada de gravação.

### Critérios de conclusão

- Build, testes e lint passam.
- Todos os bugs encontrados na revisão possuem testes de regressão.
- Não existe perda silenciosa de dados.

## Ordem das entregas

1. Testes de caracterização e parsing seguro.
2. Salvamento assíncrono e tratamento de erros.
3. Timestamp e associação das fileiras.
4. Modelos de domínio e repositório.
5. ViewModel e estado único.
6. Restauração de estado.
7. Redução da duplicação.
8. Validação completa.
