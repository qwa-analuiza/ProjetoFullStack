import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { IconeComponent } from '../icone/icone.component';

@Component({
  selector: 'app-logo',
  standalone: true,
  imports: [RouterLink, IconeComponent],
  templateUrl: './logo.component.html',
  styleUrl: './logo.component.css'
})
export class LogoComponent {
  /** Versão para fundos escuros. */
  @Input() claro = false;
}
