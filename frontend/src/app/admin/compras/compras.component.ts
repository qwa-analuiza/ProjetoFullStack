import { Component, OnInit, inject } from '@angular/core';
import { CurrencyPipe, DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api } from '../../core/api';
import { Aviso } from '../../core/aviso.service';
import { Compra, Fornecedor, Ingrediente } from '../../core/models';
import { IconeComponent } from '../../shared/icone/icone.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { RotuloPipe } from '../../shared/pipes/rotulo.pipe';
import { CabecalhoSecaoComponent } from '../../shared/cabecalho-secao/cabecalho-secao.component';
import { EstadoVazioComponent } from '../../shared/estado-vazio/estado-vazio.component';

interface LinhaNova { ingredienteId: number | null; quantidade: number | null; custoUnitario: number | null; }
interface LinhaRecebimento { itemId: number; ingrediente: string; unidade: string; quantidadeRecebida: number; custoUnitario: number; validade: string; }

@Component({
  selector: 'app-compras',
  standalone: true,
  imports: [FormsModule, CurrencyPipe, DatePipe, DecimalPipe, IconeComponent, ModalComponent, RotuloPipe, CabecalhoSecaoComponent, EstadoVazioComponent],
  templateUrl: './compras.component.html',
  styleUrl: './compras.component.css'
})
export class ComprasComponent implements OnInit {
  private api = inject(Api);
  private aviso = inject(Aviso);

  lista: Compra[] = [];
  fornecedores: Fornecedor[] = [];
  ingredientes: Ingrediente[] = [];
  carregando = true;
  salvando = false;

  modalNova = false;
  fornecedorId: number | null = null;
  linhas: LinhaNova[] = [this.linhaVazia()];

  modalReceber = false;
  comprando?: Compra;
  recebimento: LinhaRecebimento[] = [];

  linhaVazia(): LinhaNova { return { ingredienteId: null, quantidade: null, custoUnitario: null }; }

  ngOnInit() { this.carregar(); }

  async carregar() {
    try {
      [this.lista, this.fornecedores, this.ingredientes] = await Promise.all([
        this.api.get<Compra[]>('/compras'),
        this.api.get<Fornecedor[]>('/fornecedores'),
        this.api.get<Ingrediente[]>('/ingredientes')
      ]);
    } catch (e) { this.aviso.erro(e); }
    finally { this.carregando = false; }
  }

  abrirNova() {
    if (this.fornecedores.length === 0 || this.ingredientes.length === 0) {
      this.aviso.erro('Cadastre ao menos um fornecedor e um ingrediente antes de criar uma compra.');
      return;
    }
    this.fornecedorId = null;
    this.linhas = [this.linhaVazia()];
    this.modalNova = true;
  }

  adicionarLinha() { this.linhas = [...this.linhas, this.linhaVazia()]; }
  removerLinha(n: number) { this.linhas = this.linhas.filter((_, i) => i !== n); }

  escolher(linha: LinhaNova, id: number) {
    linha.ingredienteId = id;
    const ing = this.ingredientes.find(i => i.id === id);
    if (ing && linha.custoUnitario === null) linha.custoUnitario = ing.custoUnitario;
  }

  async criar() {
    this.salvando = true;
    try {
      await this.api.post('/compras', { fornecedorId: this.fornecedorId, itens: this.linhas });
      this.aviso.ok('Compra criada');
      this.modalNova = false;
      await this.carregar();
    } catch (e) { this.aviso.erro(e); }
    finally { this.salvando = false; }
  }

  async enviar(c: Compra) { await this.acao(c, 'enviar', 'Compra marcada como enviada'); }

  async cancelar(c: Compra) {
    if (confirm('Cancelar a compra #' + c.id + '?')) await this.acao(c, 'cancelar', 'Compra cancelada');
  }

  private async acao(c: Compra, caminho: string, mensagem: string) {
    try { await this.api.post('/compras/' + c.id + '/' + caminho); this.aviso.ok(mensagem); await this.carregar(); }
    catch (e) { this.aviso.erro(e); }
  }

  abrirRecebimento(c: Compra) {
    this.comprando = c;
    this.recebimento = c.itens.map(i => ({
      itemId: i.id, ingrediente: i.ingrediente, unidade: i.unidade,
      quantidadeRecebida: i.quantidade, custoUnitario: i.custoUnitario, validade: ''
    }));
    this.modalReceber = true;
  }

  async receber() {
    if (!this.comprando) return;
    this.salvando = true;
    try {
      await this.api.post('/compras/' + this.comprando.id + '/receber', {
        itens: this.recebimento.map(r => ({
          itemId: r.itemId, quantidadeRecebida: r.quantidadeRecebida, custoUnitario: r.custoUnitario, validade: r.validade || null
        }))
      });
      this.aviso.ok('Compra recebida e estoque atualizado');
      this.modalReceber = false;
      await this.carregar();
    } catch (e) { this.aviso.erro(e); }
    finally { this.salvando = false; }
  }
}
