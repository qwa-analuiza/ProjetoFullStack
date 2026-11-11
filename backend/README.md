# Dark Kitchen — Backend

Java 17+ · Spring Boot 3 · MySQL · JWT · Swagger.

## Rodar
1. Tenha o **MySQL** rodando (o projeto usa a porta **3307**; se o seu for 3306, troque na `spring.datasource.url`) e **Java 17+** e **Maven** instalados.
2. Defina a senha do MySQL na variável de ambiente **`DB_SENHA`** (e `DB_USUARIO`, se não for `root`). A senha não fica mais gravada no `application.properties`.
   No IntelliJ: *Run > Edit Configurations > Environment variables* → `DB_SENHA=sua_senha`.
3. Na pasta do projeto:
   ```bash
   mvn spring-boot:run
   ```
   O banco `darkkitchen` e todas as tabelas são criados automaticamente.

Swagger: http://localhost:8080/swagger-ui.html
Login inicial (ADMIN): `admin@darkkitchen.com` / `admin123` → copie o `token` e clique em **Authorize**.

## DBeaver
Nova conexão → MySQL → host `localhost`, porta `3306`, usuário e senha do seu MySQL.
Na primeira vez que conectar, baixe o driver quando o DBeaver pedir. Se aparecer
`Public Key Retrieval is not allowed`, em *Driver properties* defina `allowPublicKeyRetrieval=true`.
Depois de rodar a API uma vez, o banco `darkkitchen` aparece com as tabelas (clique com o botão direito → *Refresh*).

## Fluxo para testar do zero (todos como ADMIN)
1. `POST /api/ingredientes` — cria ingredientes (ex.: Carne, KG).
2. `POST /api/estoque/entradas` — dá entrada no estoque (quantidade, custo, validade).
3. `POST /api/pratos` — cria prato com ficha técnica (ingredienteId + quantidade) e preço.
4. `POST /api/auth/cadastro` — cadastra um cliente; `POST /api/pedidos` — faz um pedido.
5. `PATCH /api/painel/pedidos/{id}/status` com `CONFIRMADO` — baixa o estoque; depois `EM_PREPARO`, `PRONTO`, `SAIU_PARA_ENTREGA`, `ENTREGUE`.

## Quem acessa o quê
| Perfil | Acesso |
|---|---|
| (público) | `GET /api/cardapio`, `POST /api/auth/cadastro`, `POST /api/auth/login` |
| CLIENTE | `POST /api/pedidos`, `GET /api/pedidos`, `GET /api/pedidos/{id}`, `POST /api/pedidos/{id}/cancelar`, `GET /api/eu` |
| COZINHEIRO | `GET /api/painel/pedidos` (só pedidos pendentes: RECEBIDO a PRONTO), `PATCH /api/painel/pedidos/{id}/status` (só CONFIRMADO, EM_PREPARO, PRONTO) |
| MOTOBOY | `GET /api/entregas/disponiveis`, `GET /api/entregas/minhas`, `POST /api/entregas/{id}/retirar`, `POST /api/entregas/{id}/entregar` |
| GERENTE | painel completo + `/api/entregas` + `/api/ingredientes`, `/api/fornecedores`, `/api/estoque`, `/api/compras`, `/api/dashboard` |
| ADMIN | tudo + `/api/pratos` e `/api/usuarios` (criar gerente, cozinheiro e motoboy; desativar e `POST /{id}/reativar`) |

## Regras
- Status: RECEBIDO → CONFIRMADO → EM_PREPARO → PRONTO → SAIU_PARA_ENTREGA → ENTREGUE (cancelar só em RECEBIDO).
- Confirmar baixa o estoque (lote que vence primeiro sai primeiro; lote vencido não é usado). Faltou ingrediente → erro e nada muda.
- Custo do prato = soma (quantidade da ficha × custo do ingrediente). Preço e custo ficam gravados em cada item do pedido.
- Receber compra cria lotes e atualiza o custo do ingrediente (média ponderada).
- Pagamento simulado: sempre aprovado.
- Entrega: o cozinheiro marca PRONTO → o pedido aparece para os motoboys → o motoboy **retira** (vira o responsável, status SAIU_PARA_ENTREGA) → só ele conclui (**entregar** → ENTREGUE). Dois motoboys não pegam o mesmo pedido (a linha é travada no banco). Gerente e admin podem concluir qualquer entrega.
- O token JWT é conferido no banco a cada requisição: usuário desativado perde o acesso na hora e o perfil vale o que está gravado agora. Ninguém desativa o próprio acesso.
- O cozinheiro só enxerga pedidos de RECEBIDO a PRONTO; cliente e motoboy só abrem pedidos próprios em `/api/pedidos/{id}`.

## Banco de dados
As tabelas são criadas/atualizadas pelo Hibernate (`ddl-auto=update`). Ao subir, `AjusteDeBanco` converte a coluna `usuarios.perfil` de ENUM do MySQL para texto, para bancos criados antes do perfil MOTOBOY aceitarem o novo valor (se já for texto, nada muda). A coluna nova `pedidos.entregador_id` é criada sozinha.
