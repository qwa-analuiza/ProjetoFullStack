import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Api } from '../../core/api';
import { AuthService } from '../../core/auth.service';
import { Aviso } from '../../core/aviso.service';
import { CartService } from '../../core/cart.service';
import { FormaPagamento, Pedido } from '../../core/models';
import { FotoComponent } from '../../shared/foto/foto.component';
import { IconeComponent } from '../../shared/icone/icone.component';
import { EstadoVazioComponent } from '../../shared/estado-vazio/estado-vazio.component';

@Component({
  selector: 'app-carrinho',
  standalone: true,
  imports: [RouterLink, CurrencyPipe, FormsModule, FotoComponent, IconeComponent, EstadoVazioComponent],
  templateUrl: './carrinho.component.html',
  styleUrl: './carrinho.component.css'
})
export class CarrinhoComponent {
  cart = inject(CartService);
  auth = inject(AuthService);
  private api = inject(Api);
  private aviso = inject(Aviso);
  private router = inject(Router);

  formas: { valor: FormaPagamento; rotulo: string }[] = [
    { valor: 'PIX', rotulo: 'Pix' },
    { valor: 'CARTAO_CREDITO', rotulo: 'Crédito' },
    { valor: 'CARTAO_DEBITO', rotulo: 'Débito' }
  ];
  forma: FormaPagamento = 'PIX';
  endereco = this.auth.usuario()?.endereco ?? '';
  telefone = this.auth.usuario()?.telefone ?? '';
  enviando = false;

  async finalizar() {
    this.enviando = true;
    try {
      const pedido = await this.api.post<Pedido>('/pedidos', {
        itens: this.cart.itens().map(i => ({ pratoId: i.pratoId, quantidade: i.quantidade, observacao: i.observacao || null })),
        formaPagamento: this.forma,
        enderecoEntrega: this.endereco.trim(),
        telefoneContato: this.telefone.trim()
      });
      this.cart.limpar();
      this.aviso.ok('Pedido #' + pedido.id + ' enviado!');
      this.router.navigate(['/pedidos', pedido.id]);
    } catch (e) {
      this.aviso.erro(e);
    } finally {
      this.enviando = false;
    }
  }
}
