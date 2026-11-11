import { Component, OnInit, inject } from '@angular/core';
import { DecimalPipe, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api } from '../../core/api';
import { Aviso } from '../../core/aviso.service';
import { Ingrediente, Unidade } from '../../core/models';
import { IconeComponent } from '../../shared/icone/icone.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { CabecalhoSecaoComponent } from '../../shared/cabecalho-secao/cabecalho-secao.component';
import { EstadoVazioComponent } from '../../shared/estado-vazio/estado-vazio.component';

@Component({
  selector: 'app-ingredientes',
  standalone: true,
  imports: [FormsModule, DecimalPipe, CurrencyPipe, IconeComponent, ModalComponent, CabecalhoSecaoComponent, EstadoVazioComponent],
  templateUrl: './ingredientes.component.html',
  styleUrl: './ingredientes.component.css'
})
export class IngredientesComponent implements OnInit {
  private api = inject(Api);
  private aviso = inject(Aviso);

  lista: Ingrediente[] = [];
  unidades: Unidade[] = ['KG', 'G', 'L', 'ML', 'UN'];
  carregando = true;
  modal = false;
  salvando = false;
  editandoId: number | null = null;
  form = this.vazio();

  vazio() { return { nome: '', unidade: 'KG' as Unidade, estoqueMinimo: 0, custoUnitario: 0 }; }

  ngOnInit() { this.carregar(); }

  async carregar() {
    try { this.lista = await this.api.get<Ingrediente[]>('/ingredientes'); }
    catch (e) { this.aviso.erro(e); }
    finally { this.carregando = false; }
  }

  novo() { this.editandoId = null; this.form = this.vazio(); this.modal = true; }

  editar(i: Ingrediente) {
    this.editandoId = i.id;
    this.form = { nome: i.nome, unidade: i.unidade, estoqueMinimo: i.estoqueMinimo, custoUnitario: i.custoUnitario };
    this.modal = true;
  }

  async salvar() {
    this.salvando = true;
    try {
      const corpo = { ...this.form, nome: this.form.nome.trim() };
      if (this.editandoId) await this.api.put('/ingredientes/' + this.editandoId, corpo);
      else await this.api.post('/ingredientes', corpo);
      this.aviso.ok('Ingrediente salvo');
      this.modal = false;
      await this.carregar();
    } catch (e) { this.aviso.erro(e); }
    finally { this.salvando = false; }
  }

  async desativar(i: Ingrediente) {
    if (!confirm('Desativar "' + i.nome + '"?')) return;
    try { await this.api.delete('/ingredientes/' + i.id); this.aviso.ok('Ingrediente desativado'); await this.carregar(); }
    catch (e) { this.aviso.erro(e); }
  }
}
