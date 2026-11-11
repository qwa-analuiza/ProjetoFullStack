import { Component, EventEmitter, Input, Output } from '@angular/core';
import { IconeComponent } from '../icone/icone.component';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [IconeComponent],
  templateUrl: './modal.component.html',
  styleUrl: './modal.component.css'
})
export class ModalComponent {
  @Input() titulo = '';
  @Output() fechar = new EventEmitter<void>();

  /**
   * Fecha só quando o clique é no fundo escuro. Não pode retornar `false`: o Angular chamaria
   * preventDefault() no clique que sobe do botão "Salvar" e o formulário nunca seria enviado.
   */
  cliqueNoFundo(evento: MouseEvent): void {
    if (evento.target === evento.currentTarget) this.fechar.emit();
  }
}
