import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Api } from '../../core/api';
import { Aviso } from '../../core/aviso.service';
import { Pedido } from '../../core/models';
import { RotuloPipe } from '../../shared/pipes/rotulo.pipe';
import { EstadoVazioComponent } from '../../shared/estado-vazio/estado-vazio.component';

@Component({
  selector: 'app-pedidos',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, DatePipe, RotuloPipe, EstadoVazioComponent],
  templateUrl: './pedidos.component.html',
  styleUrl: './pedidos.component.css'
})
export class PedidosComponent implements OnInit {
  private api = inject(Api);
  private aviso = inject(Aviso);
  pedidos: Pedido[] = [];
  carregando = true;

  async ngOnInit() {
    try {
      this.pedidos = await this.api.get<Pedido[]>('/pedidos');
    } catch (e) {
      this.aviso.erro(e);
    } finally {
      this.carregando = false;
    }
  }

  resumo(p: Pedido) { return p.itens.map(i => i.quantidade + '× ' + i.prato).join(', '); }
}
