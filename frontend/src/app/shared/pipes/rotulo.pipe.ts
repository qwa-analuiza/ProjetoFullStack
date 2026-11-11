import { Pipe, PipeTransform } from '@angular/core';

const MAPAS: Record<string, Record<string, string>> = {
  status: {
    RECEBIDO: 'Recebido', CONFIRMADO: 'Confirmado', EM_PREPARO: 'Em preparo', PRONTO: 'Pronto',
    SAIU_PARA_ENTREGA: 'Saiu para entrega', ENTREGUE: 'Entregue', CANCELADO: 'Cancelado'
  },
  pagamento: { PIX: 'Pix', CARTAO_CREDITO: 'Cartão de crédito', CARTAO_DEBITO: 'Cartão de débito' },
  perfil: { CLIENTE: 'Cliente', ADMIN: 'Administrador', GERENTE: 'Gerente', COZINHEIRO: 'Cozinheiro', MOTOBOY: 'Motoboy' },
  compra: { ABERTO: 'Aberta', ENVIADO: 'Enviada', RECEBIDO: 'Recebida', CANCELADO: 'Cancelada' }
};

/** Troca o código do backend (EM_PREPARO) pelo texto amigável (Em preparo). */
@Pipe({ name: 'rotulo', standalone: true })
export class RotuloPipe implements PipeTransform {
  transform(valor: string, tipo: 'status' | 'pagamento' | 'perfil' | 'compra'): string {
    return MAPAS[tipo]?.[valor] ?? valor;
  }
}
