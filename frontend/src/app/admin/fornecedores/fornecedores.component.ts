import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Api } from '../../core/api';
import { Aviso } from '../../core/aviso.service';
import { Fornecedor } from '../../core/models';
import { IconeComponent } from '../../shared/icone/icone.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { CabecalhoSecaoComponent } from '../../shared/cabecalho-secao/cabecalho-secao.component';
import { EstadoVazioComponent } from '../../shared/estado-vazio/estado-vazio.component';

@Component({
  selector: 'app-fornecedores',
  standalone: true,
  imports: [FormsModule, IconeComponent, ModalComponent, CabecalhoSecaoComponent, EstadoVazioComponent],
  templateUrl: './fornecedores.component.html',
  styleUrl: './fornecedores.component.css'
})
export class FornecedoresComponent implements OnInit {
  private api = inject(Api);
  private aviso = inject(Aviso);

  lista: Fornecedor[] = [];
  carregando = true;
  modal = false;
  salvando = false;
  editandoId: number | null = null;
  form = { nome: '', cnpj: '', email: '', telefone: '' };

  ngOnInit() { this.carregar(); }

  detalhe(f: Fornecedor) { return [f.cnpj, f.telefone, f.email].filter(Boolean).join(' · ') || 'Sem contato cadastrado'; }

  async carregar() {
    try { this.lista = await this.api.get<Fornecedor[]>('/fornecedores'); }
    catch (e) { this.aviso.erro(e); }
    finally { this.carregando = false; }
  }

  novo() { this.editandoId = null; this.form = { nome: '', cnpj: '', email: '', telefone: '' }; this.modal = true; }

  editar(f: Fornecedor) {
    this.editandoId = f.id;
    this.form = { nome: f.nome, cnpj: f.cnpj ?? '', email: f.email ?? '', telefone: f.telefone ?? '' };
    this.modal = true;
  }

  async salvar() {
    this.salvando = true;
    try {
      const corpo = {
        nome: this.form.nome.trim(), cnpj: this.form.cnpj.trim() || null,
        email: this.form.email.trim() || null, telefone: this.form.telefone.trim() || null
      };
      if (this.editandoId) await this.api.put('/fornecedores/' + this.editandoId, corpo);
      else await this.api.post('/fornecedores', corpo);
      this.aviso.ok('Fornecedor salvo');
      this.modal = false;
      await this.carregar();
    } catch (e) { this.aviso.erro(e); }
    finally { this.salvando = false; }
  }

  async desativar(f: Fornecedor) {
    if (!confirm('Desativar "' + f.nome + '"?')) return;
    try { await this.api.delete('/fornecedores/' + f.id); this.aviso.ok('Fornecedor desativado'); await this.carregar(); }
    catch (e) { this.aviso.erro(e); }
  }
}
