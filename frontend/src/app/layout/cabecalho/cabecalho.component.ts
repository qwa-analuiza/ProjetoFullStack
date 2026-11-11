import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { CartService } from '../../core/cart.service';
import { IconeComponent } from '../../shared/icone/icone.component';
import { LogoComponent } from '../../shared/logo/logo.component';

@Component({
  selector: 'app-cabecalho',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, IconeComponent, LogoComponent],
  templateUrl: './cabecalho.component.html',
  styleUrl: './cabecalho.component.css'
})
export class CabecalhoComponent {
  auth = inject(AuthService);
  cart = inject(CartService);

  iniciais = computed(() => {
    const partes = (this.auth.usuario()?.nome ?? '').trim().split(/\s+/).filter(Boolean);
    return ((partes[0]?.[0] ?? '') + (partes.length > 1 ? partes[partes.length - 1][0] : '')).toUpperCase();
  });
}
