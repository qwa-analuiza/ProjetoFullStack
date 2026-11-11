import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { Perfil } from '../../core/models';

interface Aba { caminho: string; rotulo: string; perfis: Perfil[]; }

const ABAS: Aba[] = [
  { caminho: 'dashboard', rotulo: 'Resumo', perfis: ['GERENTE', 'ADMIN'] },
  { caminho: 'pedidos', rotulo: 'Pedidos', perfis: ['COZINHEIRO', 'GERENTE', 'ADMIN'] },
  { caminho: 'entregas', rotulo: 'Entregas', perfis: ['MOTOBOY', 'GERENTE', 'ADMIN'] },
  { caminho: 'estoque', rotulo: 'Estoque', perfis: ['GERENTE', 'ADMIN'] },
  { caminho: 'ingredientes', rotulo: 'Ingredientes', perfis: ['GERENTE', 'ADMIN'] },
  { caminho: 'compras', rotulo: 'Compras', perfis: ['GERENTE', 'ADMIN'] },
  { caminho: 'fornecedores', rotulo: 'Fornecedores', perfis: ['GERENTE', 'ADMIN'] },
  { caminho: 'usuarios', rotulo: 'Equipe', perfis: ['ADMIN'] }
];

@Component({
  selector: 'app-painel',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './painel.component.html',
  styleUrl: './painel.component.css'
})
export class PainelComponent {
  auth = inject(AuthService);
  abas = ABAS.filter(a => this.auth.temPerfil(...a.perfis));
}
