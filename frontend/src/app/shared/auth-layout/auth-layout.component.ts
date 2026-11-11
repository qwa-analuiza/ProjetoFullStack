import { Component, Input, inject } from '@angular/core';
import { ActivatedRoute, RouterLink, RouterLinkActive } from '@angular/router';
import { LogoComponent } from '../logo/logo.component';

/** Tela dividida (painel escuro + formulário) usada por login e cadastro. O formulário entra por projeção. */
@Component({
  selector: 'app-auth-layout',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, LogoComponent],
  templateUrl: './auth-layout.component.html',
  styleUrl: './auth-layout.component.css'
})
export class AuthLayoutComponent {
  @Input({ required: true }) eyebrow = '';
  @Input({ required: true }) titulo = '';
  @Input() subtitulo = '';
  rota = inject(ActivatedRoute);
}
