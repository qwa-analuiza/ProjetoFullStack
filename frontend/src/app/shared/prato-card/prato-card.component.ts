import { Component, Input, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { PratoCardapio } from '../../core/models';
import { CartService } from '../../core/cart.service';
import { AuthService } from '../../core/auth.service';
import { Aviso } from '../../core/aviso.service';
import { FotoComponent } from '../foto/foto.component';
import { IconeComponent } from '../icone/icone.component';

@Component({
  selector: 'app-prato-card',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, FotoComponent, IconeComponent],
  templateUrl: './prato-card.component.html',
  styleUrl: './prato-card.component.css'
})
export class PratoCardComponent {
  @Input({ required: true }) prato!: PratoCardapio;
  private cart = inject(CartService);
  private aviso = inject(Aviso);
  auth = inject(AuthService);

  adicionar() {
    this.cart.adicionar(this.prato, 1);
    this.aviso.ok(this.prato.nome + ' adicionado ao carrinho');
  }
}
