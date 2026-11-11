export type Perfil = 'CLIENTE' | 'ADMIN' | 'GERENTE' | 'COZINHEIRO' | 'MOTOBOY';
export type StatusPedido = 'RECEBIDO' | 'CONFIRMADO' | 'EM_PREPARO' | 'PRONTO' | 'SAIU_PARA_ENTREGA' | 'ENTREGUE' | 'CANCELADO';
export type FormaPagamento = 'PIX' | 'CARTAO_CREDITO' | 'CARTAO_DEBITO';
export type Unidade = 'KG' | 'G' | 'L' | 'ML' | 'UN';
export type StatusCompra = 'ABERTO' | 'ENVIADO' | 'RECEBIDO' | 'CANCELADO';

export interface Usuario { id: number; nome: string; email: string; telefone?: string; endereco?: string; perfil: Perfil; ativo: boolean; }
export interface AuthResposta { token: string; usuario: Usuario; }

export interface PratoCardapio { id: number; nome: string; descricao?: string; categoria?: string; imagemUrl?: string; preco: number; }

export interface ItemFichaResposta { ingredienteId: number; ingrediente: string; unidade: Unidade; quantidade: number; custoUnitario: number; custoDoItem: number; }
export interface PratoAdmin {
  id: number; nome: string; descricao?: string; categoria?: string; imagemUrl?: string; preco: number; ativo: boolean;
  custoTotal: number; margemBruta: number; margemPercentual: number; ficha: ItemFichaResposta[];
}
export interface ItemFichaRequisicao { ingredienteId: number; quantidade: number; }
export interface PratoRequisicao {
  nome: string; descricao: string | null; categoria: string | null; imagemUrl: string | null;
  preco: number; ativo: boolean; ficha: ItemFichaRequisicao[];
}
export interface FuncionarioRequisicao { nome: string; email: string; senha: string; perfil: Exclude<Perfil, 'CLIENTE'>; }

export interface ItemPedidoResposta { pratoId: number; prato: string; quantidade: number; observacao?: string; precoUnitario: number; subtotal: number; }
export interface Pedido {
  id: number; status: StatusPedido; formaPagamento: FormaPagamento; pagamentoAprovado: boolean; codigoPagamento?: string;
  cliente: string; entregador?: string | null; enderecoEntrega?: string; telefoneContato?: string; total: number; criadoEm: string; atualizadoEm: string;
  itens: ItemPedidoResposta[];
}

export interface Ingrediente { id: number; nome: string; unidade: Unidade; estoqueAtual: number; estoqueMinimo: number; custoUnitario: number; estoqueBaixo: boolean; }
export interface Fornecedor { id: number; nome: string; cnpj?: string; email?: string; telefone?: string; }
export interface Lote { id: number; ingredienteId: number; ingrediente: string; unidade: Unidade; quantidadeAtual: number; custoUnitario: number; validade?: string; vencido: boolean; }
export interface Alertas { estoqueBaixo: Ingrediente[]; lotesVencendoOuVencidos: Lote[]; }

export interface ItemCompraResposta { id: number; ingredienteId: number; ingrediente: string; unidade: Unidade; quantidade: number; custoUnitario: number; validade?: string; }
export interface Compra { id: number; fornecedorId: number; fornecedor: string; status: StatusCompra; total: number; criadoEm: string; recebidoEm?: string; itens: ItemCompraResposta[]; }

export interface Dashboard {
  pedidosHoje: number; faturamentoHoje: number; custoHoje: number; lucroBrutoHoje: number;
  pedidosPorStatus: Record<string, number>; ingredientesComEstoqueBaixo: number; lotesVencendoOuVencidos: number;
  maisVendidosHoje: { prato: string; quantidade: number }[];
}
