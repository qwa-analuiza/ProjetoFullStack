import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { Api } from '../../core/api';
import { Aviso } from '../../core/aviso.service';
import { Pedido } from '../../core/models';
import { RotuloPipe } from '../../shared/pipes/rotulo.pipe';
import { IconeComponent } from '../../shared/icone/icone.component';
import { EstadoVazioComponent } from '../../shared/estado-vazio/estado-vazio.component';

/** Tela do motoboy: retirar pedidos prontos, seguir a rota e concluir a entrega. */
@Component({
  selector: 'app-entregas',
  standalone: true,
  imports: [CurrencyPipe, DatePipe, RotuloPipe, IconeComponent, EstadoVazioComponent],
  templateUrl: './entregas.component.html',
  styleUrl: './entregas.component.css'
})
export class EntregasComponent implements OnInit, OnDestroy {
  private api = inject(Api);
  private aviso = inject(Aviso);

  disponiveis: Pedido[] = [];
  minhas: Pedido[] = [];
  carregando = true;
  ocupado: number | null = null;
  private timer?: ReturnType<typeof setInterval>;

  get emRota() { return this.minhas.filter(p => p.status === 'SAIU_PARA_ENTREGA'); }
  get entreguesHoje() { return this.minhas.filter(p => p.status === 'ENTREGUE'); }

  async ngOnInit() {
    await this.carregar();
    this.carregando = false;
    this.timer = setInterval(() => this.carregar(), 15000);
  }
  ngOnDestroy() { if (this.timer) clearInterval(this.timer); }

  async carregar() {
    try {
      [this.disponiveis, this.minhas] = await Promise.all([
        this.api.get<Pedido[]>('/entregas/disponiveis'),
        this.api.get<Pedido[]>('/entregas/minhas')
      ]);
    } catch (e) {
      if (this.carregando) this.aviso.erro(e);
    }
  }

  linkMapa(p: Pedido): string {
    return 'https://www.google.com/maps/search/?api=1&query=' + encodeURIComponent(p.enderecoEntrega ?? '');
  }

  linkTelefone(p: Pedido): string {
    return 'tel:' + (p.telefoneContato ?? '').replace(/[^\d+]/g, '');
  }

  async retirar(p: Pedido) {
    await this.acao(p, 'retirar', 'Pedido #' + p.id + ' retirado. Boa entrega!');
  }

  async entregar(p: Pedido) {
    if (!confirm('Confirmar que o pedido #' + p.id + ' foi entregue ao cliente?')) return;
    await this.acao(p, 'entregar', 'Pedido #' + p.id + ' entregue');
  }

  private async acao(p: Pedido, caminho: 'retirar' | 'entregar', mensagem: string) {
    this.ocupado = p.id;
    try {
      await this.api.post<Pedido>('/entregas/' + p.id + '/' + caminho);
      this.aviso.ok(mensagem);
    } catch (e) {
      this.aviso.erro(e);
    } finally {
      this.ocupado = null;
      await this.carregar();   // atualiza mesmo se der erro (ex.: outro motoboy já retirou)
    }
  }
}
