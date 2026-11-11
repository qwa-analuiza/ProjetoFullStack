import { Component, OnInit, inject } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Api } from '../../core/api';
import { Aviso } from '../../core/aviso.service';
import { AuthService } from '../../core/auth.service';
import { Dashboard, Ingrediente, Lote, PratoCardapio, Usuario } from '../../core/models';
import { IconeComponent } from '../../shared/icone/icone.component';
import { RotuloPipe } from '../../shared/pipes/rotulo.pipe';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CurrencyPipe, RouterLink, RotuloPipe, IconeComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private api = inject(Api);
  private aviso = inject(Aviso);
  private auth = inject(AuthService);
  d?: Dashboard;
  passos: Passo[] = [];
  carregando = true;

  get passosPendentes() { return this.passos.some(p => !p.feito); }

  get statusLista(): [string, number][] { return this.d ? Object.entries(this.d.pedidosPorStatus) : []; }

  async ngOnInit() {
    try { this.d = await this.api.get<Dashboard>('/dashboard'); }
    catch (e) { this.aviso.erro(e); }
    finally { this.carregando = false; }
    await this.montarPassos();
  }

  /** Guia de primeiros passos: marca o que já foi feito no banco e leva ao que falta. */
  private async montarPassos() {
    const admin = this.auth.temPerfil('ADMIN');
    try {
      const [ingredientes, lotes, cardapio, equipe] = await Promise.all([
        this.api.get<Ingrediente[]>('/ingredientes'),
        this.api.get<Lote[]>('/estoque/lotes'),
        this.api.get<PratoCardapio[]>('/cardapio'),
        admin ? this.api.get<Usuario[]>('/usuarios') : Promise.resolve([] as Usuario[])
      ]);
      this.passos = [
        { titulo: 'Cadastre os ingredientes', texto: 'São a base das fichas técnicas e do estoque.', link: '/admin/ingredientes', feito: ingredientes.length > 0 },
        { titulo: 'Dê entrada no estoque', texto: 'Informe quantidade, custo e validade de cada ingrediente.', link: '/admin/estoque', feito: lotes.length > 0 },
        admin
          ? { titulo: 'Monte o cardápio', texto: 'Crie os pratos com ficha técnica pelo botão "Cadastrar prato".', link: '/', fragmento: 'cardapio', feito: cardapio.length > 0 }
          : { titulo: 'Cardápio publicado', texto: 'Quem monta o cardápio é o administrador.', link: '/', fragmento: 'cardapio', feito: cardapio.length > 0 },
        ...(admin ? [{ titulo: 'Cadastre cozinha e motoboys', texto: 'Cada pessoa da equipe entra com o próprio acesso.', link: '/admin/usuarios',
          feito: equipe.some(u => u.perfil === 'COZINHEIRO') && equipe.some(u => u.perfil === 'MOTOBOY') }] : [])
      ];
    } catch { this.passos = []; }
  }
}

interface Passo { titulo: string; texto: string; link: string; fragmento?: string; feito: boolean; }
