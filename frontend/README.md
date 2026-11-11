# Dark Kitchen — Frontend (Angular 18)

App de delivery ligado ao backend Spring Boot. Visual inspirado nos protótipos Brasa&Alma (creme, âmbar, preto-oliva, títulos grandes e apertados, cards bem arredondados). Sem bibliotecas de UI.

## Rodar
Pré-requisitos: Node 18.19+ (ou 20/22) e o backend em `http://localhost:8080`.

```bash
npm install
npm start          # http://localhost:4200
```
Build de produção: `npm run build`. Se o backend estiver em outro endereço, troque **uma linha** em `src/app/core/api.ts` (`API_URL`).

## Como o código está organizado
**Cada componente tem sua própria pasta com 3 arquivos:** `nome.component.ts` (lógica), `.html` (estrutura) e `.css` (estilo só dele).

```
src/
  styles.css                 só importa os arquivos abaixo
  styles/
    variaveis.css            TODAS as cores, raios, sombras, fontes e tamanhos (mude o visual aqui)
    base.css                 reset, tipografia, container, foco, movimento reduzido
    botoes.css               .btn e variações, .btn-icone, .stepper
    formularios.css          campos, selects, senha com "Mostrar", grades de formulário
    cartoes.css              .card, .chip, .selo (cores por status), .preco
    listas.css               .lista-cards, .linha, .resumo-linha, .dado
    feedback.css             alertas e esqueleto de carregamento
  app/
    core/                    api, auth, interceptor JWT, guards, carrinho, avisos, modelos (sem mudanças de regra)
    layout/                  cabecalho, rodape, nav-inferior (barra do celular)
    shared/                  icone, foto, logo, modal, toast, estado-vazio, cabecalho-secao,
                             prato-card, auth-layout (tela dividida), pipes/, utils/
    pages/
      home/                  home + hero/ faixa-passos/ cardapio/ chamada-final/
      prato/ carrinho/ login/ cadastro/ pedidos/ pedido/ perfil/
    admin/                   painel (abas) + fila, dashboard, ingredientes, estoque,
                             fornecedores, compras, pratos, usuarios
```

Regra prática: estilo que só uma peça usa fica no `.css` dela; estilo repetido em várias telas fica em `src/styles/`.

## Responsivo
Construído do celular para cima, com o mesmo conjunto de pontos de quebra em todo o projeto:

| Largura | O que muda |
|---|---|
| até 559px | uma coluna, menu na barra inferior, formulários empilhados |
| 560px+ | grades de 2 colunas nos formulários, textos extras no cabeçalho |
| 760px+ | faixa de passos em 4 colunas, indicadores do resumo em 4 |
| 900px+ | menu no cabeçalho (some a barra inferior), hero e login em 2 colunas, carrinho com resumo fixo ao lado, modais centralizados |

Tamanhos de título usam `clamp()` (variáveis `--t-hero`, `--t-h1`, `--t-h2`), então escalam sem salto. A grade de pratos usa `auto-fill` e se ajusta sozinha.

## Para criar um componente novo
Copie a pasta de um componente parecido, renomeie os 3 arquivos e ajuste `selector`, `templateUrl` e `styleUrl`. Use as variáveis de `variaveis.css` em vez de cores soltas.

## Telas
| Rota | Quem |
|---|---|
| `/` | todos: hero, como funciona, cardápio com busca e categorias |
| `/prato/:id`, `/carrinho` | todos (finalizar exige login) |
| `/login`, `/cadastro` | todos (tela cheia, sem cabeçalho) |
| `/pedidos`, `/pedidos/:id`, `/perfil` | logado |
| `/admin/pedidos` | cozinha, gerente, admin |
| `/admin/entregas` | motoboy, gerente, admin |
| `/admin/estoque`, `/admin/dashboard` | gerente, admin |
| `/admin/usuarios` | admin |

## Primeiro uso
1. Entre com o ADMIN configurado no backend.
2. No cardápio da página inicial, cadastre, edite ou remova pratos. A ficha técnica usa os ingredientes existentes no banco.
3. Saia, cadastre um cliente e faça um pedido. Em Painel → Pedidos, avance o status.

## Perfis e jornadas
Cada perfil cai na sua tela ao entrar e vê no menu o atalho do seu trabalho.

| Perfil | Entra em | O que faz |
|---|---|---|
| Cliente | `/` | escolhe pratos, finaliza (login só aqui), acompanha o pedido e vê o nome do motoboy |
| Cozinha | `/admin/pedidos` ("Cozinha") | confirma (baixa o estoque), inicia o preparo e marca pronto; vê só pedidos pendentes |
| Motoboy | `/admin/entregas` ("Entregas") | retira pedidos prontos, abre o endereço no mapa, liga para o cliente e marca entregue |
| Gerente | `/admin/dashboard` | resumo do dia, pedidos, entregas, estoque, ingredientes, compras e fornecedores |
| Admin | `/admin/dashboard` | tudo do gerente + equipe (criar, desativar e reativar) e o cardápio (botão "Cadastrar prato", Editar e Excluir na home) |

O Resumo mostra um guia de **Primeiros passos** (ingredientes → estoque → cardápio → equipe) enquanto algum passo estiver pendente.
