import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { Aviso } from '../../core/aviso.service';
import { destinoSeguro } from '../../shared/utils/destino-seguro';
import { AuthLayoutComponent } from '../../shared/auth-layout/auth-layout.component';

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [RouterLink, FormsModule, AuthLayoutComponent],
  templateUrl: './cadastro.component.html',
  styleUrl: './cadastro.component.css'
})
export class CadastroComponent {
  private auth = inject(AuthService);
  private aviso = inject(Aviso);
  private router = inject(Router);
  rota = inject(ActivatedRoute);

  dados = { nome: '', email: '', senha: '', telefone: '', endereco: '' };
  mostrar = false;
  enviando = false;

  async cadastrar() {
    this.enviando = true;
    try {
      await this.auth.cadastrar({
        ...this.dados,
        nome: this.dados.nome.trim(),
        email: this.dados.email.trim()
      });
      this.aviso.ok('Conta criada! Bem-vindo(a).');
      this.router.navigateByUrl(destinoSeguro(this.rota.snapshot.queryParamMap.get('voltar')) ?? '/');
    } catch (e) {
      this.aviso.erro(e);
    } finally {
      this.enviando = false;
    }
  }
}
