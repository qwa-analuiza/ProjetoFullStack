import { Injectable, computed, signal } from '@angular/core';
import { PratoCardapio } from './models';

export interface ItemCarrinho {
  pratoId: number; nome: string; preco: number; imagemUrl?: string; quantidade: number; observacao?: string;
}

const CHAVE = 'dk_carrinho';

@Injectable({ providedIn: 'root' })
export class CartService {
  itens = signal<ItemCarrinho[]>(this.carregar());
  quantidade = computed(() => this.itens().reduce((soma, i) => soma + i.quantidade, 0));
  total = computed(() => this.itens().reduce((soma, i) => soma + i.preco * i.quantidade, 0));

  adicionar(prato: PratoCardapio, quantidade = 1, observacao?: string) {
    const lista = [...this.itens()];
    const existente = lista.find(i => i.pratoId === prato.id && (i.observacao || '') === (observacao || ''));
    if (existente) {
      existente.quantidade = Math.min(50, existente.quantidade + quantidade);
    } else {
      lista.push({
        pratoId: prato.id, nome: prato.nome, preco: prato.preco, imagemUrl: prato.imagemUrl,
        quantidade: Math.min(50, quantidade), observacao: observacao || undefined
      });
    }
    this.salvar(lista);
  }

  alterar(item: ItemCarrinho, delta: number) {
    const nova = Math.min(50, item.quantidade + delta);
    if (nova < 1) return this.remover(item);
    this.salvar(this.itens().map(i => (i === item ? { ...i, quantidade: nova } : i)));
  }

  remover(item: ItemCarrinho) { this.salvar(this.itens().filter(i => i !== item)); }
  limpar() { this.salvar([]); }

  private salvar(lista: ItemCarrinho[]) {
    this.itens.set(lista);
    try { localStorage.setItem(CHAVE, JSON.stringify(lista)); } catch { /* sem storage */ }
  }

  private carregar(): ItemCarrinho[] {
    try { return JSON.parse(localStorage.getItem(CHAVE) || '[]'); } catch { return []; }
  }
}
