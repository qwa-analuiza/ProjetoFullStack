import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Api } from './api';
import { AuthResposta, Perfil, Usuario } from './models';

const CHAVE_TOKEN = 'dk_token';
const CHAVE_USUARIO = 'dk_usuario';

function ler(chave: string): string | null {
  try { return localStorage.getItem(chave); } catch { return null; }
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = inject(Api);
  private router = inject(Router);

  usuario = signal<Usuario | null>(this.usuarioSalvo());
  logado = computed(() => this.usuario() !== null);
  perfil = computed(() => this.usuario()?.perfil ?? null);
  funcionario = computed(() => this.logado() && this.perfil() !== 'CLIENTE');
  get token(): string | null { return ler(CHAVE_TOKEN); }

  temPerfil(...perfis: Perfil[]): boolean {
    const atual = this.perfil();
    return atual !== null && perfis.includes(atual);
  }

  /** Tela inicial de cada perfil depois de entrar. */
  rotaInicial(): string {
    switch (this.perfil()) {
      case 'COZINHEIRO': return '/admin/pedidos';
      case 'MOTOBOY': return '/admin/entregas';
      case 'GERENTE':
      case 'ADMIN': return '/admin/dashboard';
      default: return '/';
    }
  }

  /** Nome do atalho de trabalho no menu: cada perfil vê o seu. */
  rotuloPainel(): string {
    switch (this.perfil()) {
      case 'COZINHEIRO': return 'Cozinha';
      case 'MOTOBOY': return 'Entregas';
      default: return 'Painel';
    }
  }

  async entrar(email: string, senha: string): Promise<Usuario> {
    return this.guardar(await this.api.post<AuthResposta>('/auth/login', { email, senha }));
  }

  async cadastrar(dados: { nome: string; email: string; senha: string; endereco: string; telefone: string }): Promise<Usuario> {
    return this.guardar(await this.api.post<AuthResposta>('/auth/cadastro', dados));
  }

  /** Atualiza os dados do usuário com o que está no servidor. */
  async atualizar() {
    const usuario = await this.api.get<Usuario>('/eu');
    this.usuario.set(usuario);
    try { localStorage.setItem(CHAVE_USUARIO, JSON.stringify(usuario)); } catch { /* sem storage */ }
  }

  sair() {
    try { localStorage.removeItem(CHAVE_TOKEN); localStorage.removeItem(CHAVE_USUARIO); } catch { /* sem storage */ }
    this.usuario.set(null);
    this.router.navigateByUrl('/');
  }

  private guardar(resposta: AuthResposta): Usuario {
    try {
      localStorage.setItem(CHAVE_TOKEN, resposta.token);
      localStorage.setItem(CHAVE_USUARIO, JSON.stringify(resposta.usuario));
    } catch { /* sem storage */ }
    this.usuario.set(resposta.usuario);
    return resposta.usuario;
  }

  private usuarioSalvo(): Usuario | null {
    try {
      const texto = ler(CHAVE_USUARIO);
      return ler(CHAVE_TOKEN) && texto ? JSON.parse(texto) : null;
    } catch { return null; }
  }
}
