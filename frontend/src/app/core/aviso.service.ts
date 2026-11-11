import { Injectable, signal } from '@angular/core';

export interface Mensagem { id: number; tipo: 'ok' | 'erro'; texto: string; }

/** Mensagens rápidas (toast) de sucesso e erro. */
@Injectable({ providedIn: 'root' })
export class Aviso {
  mensagens = signal<Mensagem[]>([]);
  private contador = 0;

  ok(texto: string) { this.mostrar('ok', texto); }

  /** Aceita um texto ou o erro vindo do HttpClient. */
  erro(e: any) { this.mostrar('erro', typeof e === 'string' ? e : Aviso.texto(e)); }

  static texto(e: any): string {
    if (e?.status === 0) return 'Não foi possível conectar ao servidor. Ele está ligado?';
    const corpo = e?.error;
    if (corpo?.campos) return Object.entries(corpo.campos).map(([campo, msg]) => campo + ': ' + msg).join(' · ');
    // no login o servidor explica o motivo (senha errada, acesso desativado); fora dele, 401 é sessão vencida
    if (e?.status === 401) return corpo?.mensagem || 'Sua sessão expirou ou é inválida. Entre novamente.';
    if (e?.status === 403) return 'Seu perfil não tem permissão para esta ação.';
    if (e?.status === 404) return corpo?.mensagem || 'O registro solicitado não foi encontrado.';
    return corpo?.mensagem || 'Ocorreu um erro. Tente novamente.';
  }

  private mostrar(tipo: 'ok' | 'erro', texto: string) {
    const id = ++this.contador;
    this.mensagens.update(lista => [...lista, { id, tipo, texto }]);
    setTimeout(() => this.mensagens.update(lista => lista.filter(m => m.id !== id)), 4500);
  }
}
