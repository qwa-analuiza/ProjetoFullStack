import { Component, Input } from '@angular/core';
import { IconeComponent } from '../icone/icone.component';

/** Foto do prato. Preenche o espaço do elemento pai; sem imagem (ou se falhar), mostra um espaço reservado. */
@Component({
  selector: 'app-foto',
  standalone: true,
  imports: [IconeComponent],
  templateUrl: './foto.component.html',
  styleUrl: './foto.component.css'
})
export class FotoComponent {
  @Input() url?: string | null;
  @Input() nome?: string | null;
  falhou = false;
}
