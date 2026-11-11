import { Component, Input } from '@angular/core';
import { IconeComponent } from '../icone/icone.component';

/** Tela vazia com ícone, título, texto e (opcional) um botão projetado dentro dela. */
@Component({
  selector: 'app-estado-vazio',
  standalone: true,
  imports: [IconeComponent],
  templateUrl: './estado-vazio.component.html',
  styleUrl: './estado-vazio.component.css'
})
export class EstadoVazioComponent {
  @Input() icone = 'utensils';
  @Input({ required: true }) titulo = '';
  @Input() texto = '';
}
