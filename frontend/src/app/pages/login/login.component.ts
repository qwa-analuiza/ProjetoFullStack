import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../core/auth.service';
import { Aviso } from '../../core/aviso.service';
import { destinoSeguro } from '../../shared/utils/destino-seguro';
import { AuthLayoutComponent } from '../../shared/auth-layout/auth-layout.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, FormsModule, AuthLayoutComponent],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  private auth = inject(AuthService);
  private aviso = inject(Aviso);
  private router = inject(Router);
  rota = inject(ActivatedRoute);

  email = '';
  senha = '';
  mostrar = false;
  enviando = false;

  async entrar() {
    this.enviando = true;
    try {
      await this.auth.entrar(this.email.trim(), this.senha);
      const voltar = destinoSeguro(this.rota.snapshot.queryParamMap.get('voltar'));
      await this.router.navigateByUrl(voltar ?? this.auth.rotaInicial());
    } catch (e) {
      this.aviso.erro(e);
    } finally {
      this.enviando = false;
    }
  }
}
