import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { CartService } from '../../core/cart.service';
import { IconeComponent } from '../../shared/icone/icone.component';

/** Barra fixa no rodapé da tela, só aparece no celular e no tablet. */
@Component({
  selector: 'app-nav-inferior',
  standalone: true,
  imports: [RouterLink, RouterLinkActive, IconeComponent],
  templateUrl: './nav-inferior.component.html',
  styleUrl: './nav-inferior.component.css'
})
export class NavInferiorComponent {
  auth = inject(AuthService);
  cart = inject(CartService);
}
