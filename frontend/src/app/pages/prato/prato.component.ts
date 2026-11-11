import { Component, Input, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api } from '../../core/api';
import { Aviso } from '../../core/aviso.service';
import { CartService } from '../../core/cart.service';
import { AuthService } from '../../core/auth.service';
import { PratoCardapio } from '../../core/models';
import { FotoComponent } from '../../shared/foto/foto.component';
import { IconeComponent } from '../../shared/icone/icone.component';
import { EstadoVazioComponent } from '../../shared/estado-vazio/estado-vazio.component';

@Component({
  selector: 'app-prato',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, FormsModule, FotoComponent, IconeComponent, EstadoVazioComponent],
  templateUrl: './prato.component.html',
  styleUrl: './prato.component.css'
})
export class PratoComponent implements OnInit {
  @Input() id!: string;
  private api = inject(Api);
  private cart = inject(CartService);
  private aviso = inject(Aviso);
  private router = inject(Router);
  auth = inject(AuthService);

  prato?: PratoCardapio;
  carregando = true;
  quantidade = 1;
  observacao = '';

  async ngOnInit() {
    try {
      this.prato = await this.api.get<PratoCardapio>('/cardapio/' + this.id);
    } catch {
      this.prato = undefined;
    } finally {
      this.carregando = false;
    }
  }

  alterar(delta: number) { this.quantidade = Math.max(1, Math.min(50, this.quantidade + delta)); }

  adicionar() {
    if (!this.prato) return;
    this.cart.adicionar(this.prato, this.quantidade, this.observacao.trim());
    this.aviso.ok('Adicionado ao carrinho');
    this.router.navigateByUrl('/carrinho');
  }
}
