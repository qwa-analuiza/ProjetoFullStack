import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Api } from '../../../core/api';
import { Aviso } from '../../../core/aviso.service';
import { Ingrediente, ItemFichaRequisicao, PratoAdmin, PratoCardapio, PratoRequisicao } from '../../../core/models';
import { IconeComponent } from '../../../shared/icone/icone.component';
import { PratoCardComponent } from '../../../shared/prato-card/prato-card.component';
import { EstadoVazioComponent } from '../../../shared/estado-vazio/estado-vazio.component';
import { ModalComponent } from '../../../shared/modal/modal.component';

interface LinhaFicha { ingredienteId: number | null; quantidade: number | null; }

@Component({
  selector: 'app-cardapio',
  standalone: true,
  imports: [FormsModule, IconeComponent, PratoCardComponent, EstadoVazioComponent, ModalComponent],
  templateUrl: './cardapio.component.html',
  styleUrl: './cardapio.component.css'
})
export class CardapioComponent implements OnChanges {
  private api = inject(Api);
  private aviso = inject(Aviso);

  @Input() pratos: PratoCardapio[] = [];
  @Input() carregando = true;
  @Input() erro = '';
  @Input() podeEditar = false;
  @Output() recarregar = new EventEmitter<void>();

  busca = '';
  categoria = '';
  pratosAdmin: PratoAdmin[] = [];
  ingredientes: Ingrediente[] = [];
  carregandoAdmin = false;
  modal = false;
  salvando = false;
  editandoId: number | null = null;
  form = this.vazio();
  ficha: LinhaFicha[] = [this.linhaVazia()];

  ngOnChanges(changes: SimpleChanges) {
    if (changes['podeEditar']?.currentValue) {
      void this.carregarAdmin();
    } else if (changes['podeEditar']) {
      this.pratosAdmin = [];
      this.ingredientes = [];
    }
  }

  get categorias(): string[] {
    return [...new Set(this.pratos.map(p => p.categoria || 'Outros'))];
  }

  get filtrados(): PratoCardapio[] {
    const termo = this.busca.trim().toLowerCase();
    return this.pratos.filter(p =>
      (!this.categoria || (p.categoria || 'Outros') === this.categoria) &&
      (!termo || (p.nome + ' ' + (p.descricao || '')).toLowerCase().includes(termo)));
  }

  get inativos(): PratoAdmin[] { return this.pratosAdmin.filter(p => !p.ativo); }
  private pratoAdmin(id: number): PratoAdmin | undefined { return this.pratosAdmin.find(p => p.id === id); }
  temPratoAdmin(id: number): boolean { return this.pratoAdmin(id) !== undefined; }

  vazio() { return { nome: '', descricao: '', categoria: '', imagemUrl: '', preco: null as number | null, ativo: true }; }
  linhaVazia(): LinhaFicha { return { ingredienteId: null, quantidade: null }; }

  ingredientesDisponiveis(indice: number): Ingrediente[] {
    const selecionados = new Set(this.ficha
      .filter((_, i) => i !== indice)
      .map(linha => linha.ingredienteId)
      .filter((id): id is number => id !== null));
    return this.ingredientes.filter(ingrediente => !selecionados.has(ingrediente.id));
  }

  async carregarAdmin() {
    if (!this.podeEditar) return;
    this.carregandoAdmin = true;
    try {
      [this.pratosAdmin, this.ingredientes] = await Promise.all([
        this.api.get<PratoAdmin[]>('/pratos'),
        this.api.get<Ingrediente[]>('/ingredientes')
      ]);
    } catch (e) {
      this.pratosAdmin = [];
      this.ingredientes = [];
      this.aviso.erro(e);
    }
    finally { this.carregandoAdmin = false; }
  }

  novo() {
    if (this.ingredientes.length === 0) {
      this.aviso.erro('Cadastre os ingredientes antes de criar um prato.');
      return;
    }
    this.editandoId = null;
    this.form = this.vazio();
    this.ficha = [this.linhaVazia()];
    this.modal = true;
  }

  editar(prato: PratoAdmin) {
    this.editandoId = prato.id;
    this.form = {
      nome: prato.nome,
      descricao: prato.descricao ?? '',
      categoria: prato.categoria ?? '',
      imagemUrl: prato.imagemUrl ?? '',
      preco: prato.preco,
      ativo: prato.ativo
    };
    this.ficha = prato.ficha.map(item => ({
      ingredienteId: item.ingredienteId,
      quantidade: item.quantidade
    }));
    if (this.ficha.length === 0) this.ficha = [this.linhaVazia()];
    this.modal = true;
  }

  editarPorId(id: number) {
    const prato = this.pratoAdmin(id);
    if (!prato) {
      this.aviso.erro('Não foi possível carregar os dados deste prato. Tente novamente.');
      return;
    }
    this.editar(prato);
  }

  adicionarLinha() { this.ficha = [...this.ficha, this.linhaVazia()]; }
  removerLinha(indice: number) { this.ficha = this.ficha.filter((_, i) => i !== indice); }

  async salvar() {
    const ficha: ItemFichaRequisicao[] = [];
    for (const linha of this.ficha) {
      if (linha.ingredienteId === null || linha.quantidade === null || linha.quantidade <= 0) {
        this.aviso.erro('Selecione um ingrediente e informe uma quantidade maior que zero em cada linha da ficha técnica.');
        return;
      }
      ficha.push({ ingredienteId: linha.ingredienteId, quantidade: linha.quantidade });
    }

    if (new Set(ficha.map(linha => linha.ingredienteId)).size !== ficha.length) {
      this.aviso.erro('Cada ingrediente pode aparecer apenas uma vez na ficha técnica.');
      return;
    }

    if (this.form.preco === null || this.form.preco <= 0 || !this.form.nome.trim()) {
      this.aviso.erro('Informe o nome do prato e um preço maior que zero.');
      return;
    }

    this.salvando = true;
    try {
      const corpo: PratoRequisicao = {
        nome: this.form.nome.trim(),
        descricao: this.form.descricao.trim() || null,
        categoria: this.form.categoria.trim() || null,
        imagemUrl: this.form.imagemUrl.trim() || null,
        preco: this.form.preco,
        ativo: this.form.ativo,
        ficha
      };
      const prato = this.editandoId
        ? await this.api.put<PratoAdmin>('/pratos/' + this.editandoId, corpo)
        : await this.api.post<PratoAdmin>('/pratos', corpo);
      this.pratosAdmin = [...this.pratosAdmin.filter(item => item.id !== prato.id), prato];
      this.aviso.ok('Prato salvo');
      this.modal = false;
      this.recarregar.emit();
    } catch (e) { this.aviso.erro(e); }
    finally { this.salvando = false; }
  }

  async excluir(prato: PratoAdmin) {
    if (!confirm('Excluir "' + prato.nome + '" do cardápio? O prato deixará de aparecer para novos pedidos.')) return;
    try {
      await this.api.delete('/pratos/' + prato.id);
      this.aviso.ok('Prato removido do cardápio');
      this.recarregar.emit();
      await this.carregarAdmin();
    } catch (e) { this.aviso.erro(e); }
  }

  async excluirPorId(id: number) {
    const prato = this.pratoAdmin(id);
    if (!prato) {
      this.aviso.erro('Não foi possível carregar os dados deste prato. Tente novamente.');
      return;
    }
    await this.excluir(prato);
  }
}
