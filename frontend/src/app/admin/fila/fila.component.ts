import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Api } from '../../core/api';
import { AuthService } from '../../core/auth.service';
import { Aviso } from '../../core/aviso.service';
import { Pedido, StatusPedido } from '../../core/models';
import { RotuloPipe } from '../../shared/pipes/rotulo.pipe';
import { EstadoVazioComponent } from '../../shared/estado-vazio/estado-vazio.component';

const PROXIMO: Partial<Record<StatusPedido, { status: StatusPedido; rotulo: string }>> = {
  RECEBIDO: { status: 'CONFIRMADO', rotulo: 'Confirmar' },
  CONFIRMADO: { status: 'EM_PREPARO', rotulo: 'Iniciar preparo' },
  EM_PREPARO: { status: 'PRONTO', rotulo: 'Marcar como pronto' },
  PRONTO: { status: 'SAIU_PARA_ENTREGA', rotulo: 'Saiu para entrega' },
  SAIU_PARA_ENTREGA: { status: 'ENTREGUE', rotulo: 'Marcar entregue' }
};
const DO_COZINHEIRO: StatusPedido[] = ['CONFIRMADO', 'EM_PREPARO', 'PRONTO'];

@Component({
  selector: 'app-fila',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, RotuloPipe, EstadoVazioComponent],
  templateUrl: './fila.component.html',
  styleUrl: './fila.component.css'
})
export class FilaComponent implements OnInit, OnDestroy {
  private api = inject(Api);
  private aviso = inject(Aviso);
  private auth = inject(AuthService);

  filtros: { valor: StatusPedido | ''; rotulo: string }[] = [
    { valor: '', rotulo: 'Em andamento' },
    { valor: 'RECEBIDO', rotulo: 'Recebidos' },
    { valor: 'CONFIRMADO', rotulo: 'Confirmados' },
    { valor: 'EM_PREPARO', rotulo: 'Em preparo' },
    { valor: 'PRONTO', rotulo: 'Prontos' },
    { valor: 'SAIU_PARA_ENTREGA', rotulo: 'Em entrega' },
    { valor: 'ENTREGUE', rotulo: 'Entregues' },
    { valor: 'CANCELADO', rotulo: 'Cancelados' }
  ];
  filtro: StatusPedido | '' = '';
  pedidos: Pedido[] = [];
  resumoPedidos: Pedido[] = [];
  carregando = true;
  ocupado: number | null = null;
  private timer?: ReturnType<typeof setInterval>;

  get cozinheiro() { return this.auth.perfil() === 'COZINHEIRO'; }
  get filtrosVisiveis() {
    return this.cozinheiro
      ? this.filtros.filter(f => !['SAIU_PARA_ENTREGA', 'ENTREGUE', 'CANCELADO'].includes(f.valor))
      : this.filtros;
  }
  get porcoesNaCozinha() { return this.contarPorcoes(['EM_PREPARO']); }
  get porcoesAguardandoCozinha() { return this.contarPorcoes(['RECEBIDO', 'CONFIRMADO']); }
  get porcoesProntas() { return this.contarPorcoes(['PRONTO']); }

  situacaoCozinha(status: StatusPedido): { texto: string; classe: string } {
    switch (status) {
      case 'RECEBIDO': return { texto: 'Aguardando confirmação', classe: 'aguardando' };
      case 'CONFIRMADO': return { texto: 'Aguardando preparo', classe: 'aguardando' };
      case 'EM_PREPARO': return { texto: 'Na cozinha', classe: 'na-cozinha' };
      case 'PRONTO': return { texto: 'Pronto, aguardando motoboy', classe: 'pronto' };
      case 'SAIU_PARA_ENTREGA': return { texto: 'Saiu para entrega', classe: 'finalizado' };
      case 'ENTREGUE': return { texto: 'Entregue', classe: 'finalizado' };
      case 'CANCELADO': return { texto: 'Cancelado', classe: 'cancelado' };
    }
  }

  private contarPorcoes(statuses: StatusPedido[]): number {
    return this.resumoPedidos
      .filter(pedido => statuses.includes(pedido.status))
      .reduce((total, pedido) => total + pedido.itens.reduce((soma, item) => soma + item.quantidade, 0), 0);
  }

  async ngOnInit() {
    await this.carregar();
    this.carregando = false;
    this.timer = setInterval(() => this.carregar(), 15000);
  }
  ngOnDestroy() { if (this.timer) clearInterval(this.timer); }

  async filtrar(valor: StatusPedido | '') {
    this.filtro = valor;
    this.carregando = true;
    await this.carregar();
    this.carregando = false;
  }

  async carregar() {
    try {
      if (this.filtro) {
        [this.resumoPedidos, this.pedidos] = await Promise.all([
          this.api.get<Pedido[]>('/painel/pedidos'),
          this.api.get<Pedido[]>('/painel/pedidos', { status: this.filtro })
        ]);
      } else {
        this.pedidos = await this.api.get<Pedido[]>('/painel/pedidos');
        this.resumoPedidos = this.pedidos;
      }
    } catch (e) {
      if (this.carregando) this.aviso.erro(e);
    }
  }

  /** Próximo passo permitido para o perfil de quem está logado. */
  proximo(p: Pedido) {
    const prox = PROXIMO[p.status];
    if (!prox) return null;
    return this.cozinheiro && !DO_COZINHEIRO.includes(prox.status) ? null : prox;
  }

  async mudar(p: Pedido, status: StatusPedido) {
    if (status === 'CANCELADO' && !confirm('Cancelar o pedido #' + p.id + '?')) return;
    this.ocupado = p.id;
    try {
      await this.api.patch<Pedido>('/painel/pedidos/' + p.id + '/status', { status });
      this.aviso.ok('Pedido #' + p.id + ' atualizado');
      await this.carregar();
    } catch (e) {
      this.aviso.erro(e);
    } finally {
      this.ocupado = null;
    }
  }
}
