import { Component, Input } from '@angular/core';

/** Título de uma seção do painel, com contagem opcional e o botão de ação projetado à direita. */
@Component({
  selector: 'app-cabecalho-secao',
  standalone: true,
  templateUrl: './cabecalho-secao.component.html',
  styleUrl: './cabecalho-secao.component.css'
})
export class CabecalhoSecaoComponent {
  @Input({ required: true }) titulo = '';
  @Input() contagem: number | null = null;
}
