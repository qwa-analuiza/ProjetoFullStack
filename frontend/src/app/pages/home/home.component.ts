import { Component, OnInit, inject } from '@angular/core';
import { Api } from '../../core/api';
import { Aviso } from '../../core/aviso.service';
import { PratoCardapio } from '../../core/models';
import { AuthService } from '../../core/auth.service';
import { HeroComponent } from './hero/hero.component';
import { FaixaPassosComponent } from './faixa-passos/faixa-passos.component';
import { CardapioComponent } from './cardapio/cardapio.component';
import { ChamadaFinalComponent } from './chamada-final/chamada-final.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [HeroComponent, FaixaPassosComponent, CardapioComponent, ChamadaFinalComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  private api = inject(Api);
  private auth = inject(AuthService);

  pratos: PratoCardapio[] = [];
  carregando = true;
  erro = '';

  get podeEditarCardapio(): boolean { return this.auth.temPerfil('ADMIN'); }

  ngOnInit() { this.carregar(); }

  async carregar() {
    this.carregando = true;
    this.erro = '';
    try {
      this.pratos = await this.api.get<PratoCardapio[]>('/cardapio');
    } catch (e) {
      this.erro = Aviso.texto(e);
    } finally {
      this.carregando = false;
    }
  }

  /** Prato que ilustra o topo da página: o primeiro que tem foto. */
  get destaque(): PratoCardapio | undefined { return this.pratos.find(p => p.imagemUrl) ?? this.pratos[0]; }

  /** Segundo prato com foto, para o detalhe do topo. */
  get segundo(): PratoCardapio | undefined {
    return this.pratos.filter(p => p.imagemUrl && p.id !== this.destaque?.id)[0];
  }
}
