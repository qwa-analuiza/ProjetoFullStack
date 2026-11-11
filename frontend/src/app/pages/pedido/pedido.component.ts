import { Component, Input, OnDestroy, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Api } from '../../core/api';
import { Aviso } from '../../core/aviso.service';
import { Pedido, StatusPedido } from '../../core/models';
import { IconeComponent } from '../../shared/icone/icone.component';
import { RotuloPipe } from '../../shared/pipes/rotulo.pipe';
import { EstadoVazioComponent } from '../../shared/estado-vazio/estado-vazio.component';

@Component({
  selector: 'app-pedido',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, DatePipe, IconeComponent, RotuloPipe, EstadoVazioComponent],
  templateUrl: './pedido.component.html',
  styleUrl: './pedido.component.css'
})
export class PedidoComponent implements OnInit, OnDestroy {
  @Input() id!: string;
  private api = inject(Api);
  private aviso = inject(Aviso);

  pedido?: Pedido;
  carregando = true;
  etapas: StatusPedido[] = ['RECEBIDO', 'CONFIRMADO', 'EM_PREPARO', 'PRONTO', 'SAIU_PARA_ENTREGA', 'ENTREGUE'];
  private timer?: ReturnType<typeof setInterval>;

  get indice() { return this.pedido ? this.etapas.indexOf(this.pedido.status) : -1; }

  async ngOnInit() {
    await this.carregar();
    this.carregando = false;
    // atualiza sozinho a cada 10 segundos enquanto o pedido está em andamento
    this.timer = setInterval(() => {
      if (this.pedido && this.pedido.status !== 'ENTREGUE' && this.pedido.status !== 'CANCELADO') this.carregar();
    }, 10000);
  }

  ngOnDestroy() { if (this.timer) clearInterval(this.timer); }

  async carregar() {
    try {
      this.pedido = await this.api.get<Pedido>('/pedidos/' + this.id);
    } catch {
      if (!this.pedido) this.pedido = undefined;
    }
  }

  async cancelar() {
    if (!confirm('Cancelar este pedido?')) return;
    try {
      this.pedido = await this.api.post<Pedido>('/pedidos/' + this.id + '/cancelar');
      this.aviso.ok('Pedido cancelado');
    } catch (e) {
      this.aviso.erro(e);
    }
  }
}
