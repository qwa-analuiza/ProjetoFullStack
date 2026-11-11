import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Api } from '../../core/api';
import { AuthService } from '../../core/auth.service';
import { Aviso } from '../../core/aviso.service';
import { FuncionarioRequisicao, Perfil, Usuario } from '../../core/models';
import { IconeComponent } from '../../shared/icone/icone.component';
import { ModalComponent } from '../../shared/modal/modal.component';
import { RotuloPipe } from '../../shared/pipes/rotulo.pipe';
import { CabecalhoSecaoComponent } from '../../shared/cabecalho-secao/cabecalho-secao.component';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [FormsModule, IconeComponent, ModalComponent, RotuloPipe, CabecalhoSecaoComponent],
  templateUrl: './usuarios.component.html',
  styleUrl: './usuarios.component.css'
})
export class UsuariosComponent implements OnInit {
  private api = inject(Api);
  private aviso = inject(Aviso);
  auth = inject(AuthService);

  lista: Usuario[] = [];
  perfis: Exclude<Perfil, 'CLIENTE'>[] = ['COZINHEIRO', 'MOTOBOY', 'GERENTE', 'ADMIN'];
  carregando = true;
  modal = false;
  salvando = false;
  form = this.vazio();

  vazio() { return { nome: '', email: '', senha: '', perfil: 'COZINHEIRO' as Exclude<Perfil, 'CLIENTE'> }; }

  ngOnInit() { this.carregar(); }

  async carregar() {
    this.carregando = true;
    try { this.lista = await this.api.get<Usuario[]>('/usuarios'); }
    catch (e) { this.aviso.erro(e); }
    finally { this.carregando = false; }
  }

  novo() { this.form = this.vazio(); this.modal = true; }

  async salvar() {
    const nome = this.form.nome.trim();
    const email = this.form.email.trim().toLowerCase();
    if (!nome || !email || this.form.senha.length < 6) {
      this.aviso.erro('Preencha nome e e-mail válidos e use uma senha com pelo menos 6 caracteres.');
      return;
    }

    this.salvando = true;
    try {
      const dados: FuncionarioRequisicao = { ...this.form, nome, email };
      const funcionario = await this.api.post<Usuario>('/usuarios', dados);
      this.lista = [...this.lista.filter(usuario => usuario.id !== funcionario.id), funcionario];
      this.aviso.ok('Funcionário criado');
      this.modal = false;
    } catch (e) { this.aviso.erro(e); }
    finally { this.salvando = false; }
  }

  async reativar(u: Usuario) {
    try { await this.api.post('/usuarios/' + u.id + '/reativar'); this.aviso.ok('Acesso de ' + u.nome + ' reativado'); await this.carregar(); }
    catch (e) { this.aviso.erro(e); }
  }

  async desativar(u: Usuario) {
    if (!confirm('Desativar o acesso de "' + u.nome + '"?')) return;
    try { await this.api.delete('/usuarios/' + u.id); this.aviso.ok('Usuário desativado'); await this.carregar(); }
    catch (e) { this.aviso.erro(e); }
  }
}
