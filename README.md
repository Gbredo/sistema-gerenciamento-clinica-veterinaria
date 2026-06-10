# Sistema de Gerenciamento de Clínica Veterinária

Atividade Prática — Arquitetura Hexagonal com Java  
Disciplina: Análise e Desenvolvimento de Sistemas — PUC Goiás

---

## Compilação e Execução

Todos os comandos devem ser executados a partir do diretório raiz do projeto.

### 1. Compilar

```bash
find . -name "*.java" > sources.txt
javac -d out @sources.txt
```

### 2. Executar a aplicação principal

```bash
java -cp out com.clinica.apresentacao.Main
```

### 3. Executar os testes com fakes (assertions habilitadas)

```bash
java -ea -cp out com.clinica.test.TestesDominio
```

> **Atenção:** a flag `-ea` é obrigatória para que os blocos `assert` sejam avaliados pela JVM. Sem ela, as asserções são silenciosamente ignoradas.

---

## Decisões Arquiteturais

### Por que usamos Portas (interfaces) para isolar o domínio

Na Arquitetura Hexagonal, o domínio é o núcleo da aplicação e não pode depender de nenhuma tecnologia externa — nem banco de dados, nem console, nem biblioteca de terceiros. As **Portas** (interfaces Java) são o mecanismo que torna isso possível: o domínio declara *o que precisa* em termos de comportamento (salvar um animal, notificar um tutor), sem saber *como* isso será feito.

Isso garante que qualquer classe dentro de `com.clinica.dominio` compile e execute sem qualquer import de infraestrutura. A prova disso está nos adaptadores intercambiáveis de notificação: trocar `NotificacaoConsole` por `NotificacaoCsv` exige zero alterações no domínio — basta passar uma implementação diferente da porta `PortaNotificacaoTutor` ao construtor do serviço.

### Por que a Main funciona como Composition Root

A classe `Main` é o único ponto do sistema onde classes concretas de infraestrutura são instanciadas. Ela conhece tanto o domínio quanto a infraestrutura e é responsável por conectar os dois — injetando os adaptadores nas portas via construtor do `ServicoAgendaConsulta`.

Esse padrão (Composition Root) concentra todo o acoplamento concreto em um único lugar, mantendo o resto do sistema desacoplado. A demonstração da troca de adaptador no `main` — instanciando um segundo `ServicoAgendaConsulta` com `NotificacaoCsv` sem alterar nenhuma linha do domínio — é a evidência prática desse isolamento.

---

## Adaptadores Implementados

### Portas de Saída → Adaptadores de Persistência

| Porta (interface)               | Adaptador (implementação)         | Localização                                        |
|---------------------------------|-----------------------------------|----------------------------------------------------|
| `PortaAnimalRepositorio`        | `AnimalRepositorioMemoria`        | `infraestrutura/adaptador/persistencia/`           |
| `PortaVeterinarioRepositorio`   | `VeterinarioRepositorioMemoria`   | `infraestrutura/adaptador/persistencia/`           |
| `PortaConsultaRepositorio`      | `ConsultaRepositorioMemoria`      | `infraestrutura/adaptador/persistencia/`           |

Os três adaptadores utilizam `HashMap<Long, Entidade>` como estrutura interna e geram IDs sequenciais automaticamente quando a entidade chega com `id == null`.

### Porta de Saída → Adaptadores de Notificação

| Porta (interface)         | Adaptador (implementação)   | Comportamento                                                              |
|---------------------------|-----------------------------|----------------------------------------------------------------------------|
| `PortaNotificacaoTutor`   | `NotificacaoConsole`        | Imprime as notificações formatadas no console via `System.out.printf`      |
| `PortaNotificacaoTutor`   | `NotificacaoCsv`            | Acrescenta cada notificação como linha CSV em `notificacoes.csv` (append), criando o cabeçalho automaticamente na primeira escrita |

### Porta de Entrada → Serviço de Domínio

| Porta (interface)       | Implementação               | Localização               |
|-------------------------|-----------------------------|---------------------------|
| `PortaAgendaConsulta`   | `ServicoAgendaConsulta`     | `dominio/servico/`        |

O `ServicoAgendaConsulta` orquestra os cinco casos de uso do sistema (agendar, realizar, cancelar, histórico e agenda) dependendo exclusivamente das quatro portas de saída injetadas via construtor — sem nenhum import de infraestrutura.

---

## Estrutura de Pacotes

```
src/com/clinica/
│
├── dominio/                        ← NÚCLEO — zero dependências externas
│   ├── modelo/
│   │   ├── Animal.java
│   │   ├── Veterinario.java
│   │   ├── Consulta.java
│   │   ├── TipoConsulta.java       (enum)
│   │   └── SituacaoConsulta.java   (enum)
│   ├── excecao/
│   │   ├── AnimalNaoEncontradoException.java
│   │   └── VeterinarioIndisponivelException.java
│   ├── porta/
│   │   ├── entrada/
│   │   │   └── PortaAgendaConsulta.java
│   │   └── saida/
│   │       ├── PortaAnimalRepositorio.java
│   │       ├── PortaVeterinarioRepositorio.java
│   │       ├── PortaConsultaRepositorio.java
│   │       └── PortaNotificacaoTutor.java
│   └── servico/
│       └── ServicoAgendaConsulta.java
│
├── infraestrutura/
│   └── adaptador/
│       ├── persistencia/
│       │   ├── AnimalRepositorioMemoria.java
│       │   ├── VeterinarioRepositorioMemoria.java
│       │   └── ConsultaRepositorioMemoria.java
│       └── notificacao/
│           ├── NotificacaoConsole.java
│           └── NotificacaoCsv.java
│
├── apresentacao/
│   └── Main.java
│
└── test/
    └── TestesDominio.java
```
