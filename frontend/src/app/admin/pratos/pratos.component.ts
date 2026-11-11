import { Component, OnInit, inject } from '@angular/core';
import { CurrencyPipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api } from '../../core/api';
import { Aviso } from '../../core/aviso.service';
import { Ingrediente, PratoAdmin } from '../../core/models';
import { FotoComponent } from '../../shared/foto/foto.component';
import { IconeComponent } from '../../shared/icone/icone.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { CabecalhoSecaoComponent } from '../../shared/cabecalho-secao/cabecalho-secao.component';
import { EstadoVazioComponent } from '../../shared/estado-vazio/estado-vazio.component';

interface LinhaFicha { ingredienteId: number | null; quantidade: number | null; }

@Component({
  selector: 'app-pratos',
  standalone: true,
  imports: [FormsModule, CurrencyPipe, DecimalPipe, FotoComponent, IconeComponent, ModalComponent, CabecalhoSecaoComponent, EstadoVazioComponent],
  templateUrl: './pratos.component.html',
  styleUrl: './pratos.component.css'
})
export class PratosComponent implements OnInit {
  private api = inject(Api);
  private aviso = inject(Aviso);

  lista: PratoAdmin[] = [];
  ingredientes: Ingrediente[] = [];
  carregando = true;
  modal = false;
  salvando = false;
  editandoId: number | null = null;
  form = this.vazio();
  ficha: LinhaFicha[] = [this.linhaVazia()];

  vazio() { return { nome: '', descricao: '', categoria: '', imagemUrl: '', preco: null as number | null, ativo: true }; }
  linhaVazia(): LinhaFicha { return { ingredienteId: null, quantidade: null }; }

  ngOnInit() { this.carregar(); }

  async carregar() {
    try {
      [this.lista, this.ingredientes] = await Promise.all([
        this.api.get<PratoAdmin[]>('/pratos'),
        this.api.get<Ingrediente[]>('/ingredientes')
      ]);
    } catch (e) { this.aviso.erro(e); }
    finally { this.carregando = false; }
  }

  novo() {
    if (this.ingredientes.length === 0) { this.aviso.erro('Cadastre os ingredientes antes de criar um prato.'); return; }
    this.editandoId = null;
    this.form = this.vazio();
    this.ficha = [this.linhaVazia()];
    this.modal = true;
  }

  editar(p: PratoAdmin) {
    this.editandoId = p.id;
    this.form = {
      nome: p.nome, descricao: p.descricao ?? '', categoria: p.categoria ?? '',
      imagemUrl: p.imagemUrl ?? '', preco: p.preco, ativo: p.ativo
    };
    this.ficha = p.ficha.map(f => ({ ingredienteId: f.ingredienteId, quantidade: f.quantidade }));
    this.modal = true;
  }

  adicionarLinha() { this.ficha = [...this.ficha, this.linhaVazia()]; }
  removerLinha(n: number) { this.ficha = this.ficha.filter((_, i) => i !== n); }

  async salvar() {
    this.salvando = true;
    try {
      const corpo = {
        nome: this.form.nome.trim(),
        descricao: this.form.descricao.trim() || null,
        categoria: this.form.categoria.trim() || null,
        imagemUrl: this.form.imagemUrl.trim() || null,
        preco: this.form.preco,
        ativo: this.form.ativo,
        ficha: this.ficha
      };
      if (this.editandoId) await this.api.put('/pratos/' + this.editandoId, corpo);
      else await this.api.post('/pratos', corpo);
      this.aviso.ok('Prato salvo');
      this.modal = false;
      await this.carregar();
    } catch (e) { this.aviso.erro(e); }
    finally { this.salvando = false; }
  }

  async desativar(p: PratoAdmin) {
    if (!confirm('Excluir "' + p.nome + '" do cardápio? O prato deixará de aparecer para novos pedidos.')) return;
    try { await this.api.delete('/pratos/' + p.id); this.aviso.ok('Prato removido do cardápio'); await this.carregar(); }
    catch (e) { this.aviso.erro(e); }
  }
}
