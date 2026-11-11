import { Component, OnInit, inject } from '@angular/core';
import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api } from '../../core/api';
import { Aviso } from '../../core/aviso.service';
import { Alertas, Ingrediente, Lote } from '../../core/models';
import { IconeComponent } from '../../shared/icone/icone.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { CabecalhoSecaoComponent } from '../../shared/cabecalho-secao/cabecalho-secao.component';
import { EstadoVazioComponent } from '../../shared/estado-vazio/estado-vazio.component';

@Component({
  selector: 'app-estoque',
  standalone: true,
  imports: [FormsModule, CurrencyPipe, DatePipe, DecimalPipe, IconeComponent, ModalComponent, CabecalhoSecaoComponent, EstadoVazioComponent],
  templateUrl: './estoque.component.html',
  styleUrl: './estoque.component.css'
})
export class EstoqueComponent implements OnInit {
  private api = inject(Api);
  private aviso = inject(Aviso);

  alertas?: Alertas;
  lotes: Lote[] = [];
  ingredientes: Ingrediente[] = [];
  carregando = true;
  modal = false;
  salvando = false;
  form: { ingredienteId: number | null; quantidade: number | null; custoUnitario: number | null; validade: string } = this.vazio();

  vazio() { return { ingredienteId: null, quantidade: null, custoUnitario: null, validade: '' }; }

  ngOnInit() { this.carregar(); }

  async carregar() {
    try {
      [this.alertas, this.lotes, this.ingredientes] = await Promise.all([
        this.api.get<Alertas>('/estoque/alertas'),
        this.api.get<Lote[]>('/estoque/lotes'),
        this.api.get<Ingrediente[]>('/ingredientes')
      ]);
    } catch (e) { this.aviso.erro(e); }
    finally { this.carregando = false; }
  }

  abrirEntrada() { this.form = this.vazio(); this.modal = true; }

  escolher(id: number) {
    this.form.ingredienteId = id;
    const ing = this.ingredientes.find(i => i.id === id);
    if (ing && this.form.custoUnitario === null) this.form.custoUnitario = ing.custoUnitario;
  }

  async salvar() {
    this.salvando = true;
    try {
      await this.api.post('/estoque/entradas', { ...this.form, validade: this.form.validade || null });
      this.aviso.ok('Entrada registrada');
      this.modal = false;
      await this.carregar();
    } catch (e) { this.aviso.erro(e); }
    finally { this.salvando = false; }
  }

  async descartar(l: Lote) {
    if (!confirm('Descartar o lote #' + l.id + ' de ' + l.ingrediente + '? O saldo sai do estoque.')) return;
    try { await this.api.post('/estoque/lotes/' + l.id + '/descartar'); this.aviso.ok('Lote descartado'); await this.carregar(); }
    catch (e) { this.aviso.erro(e); }
  }
}
