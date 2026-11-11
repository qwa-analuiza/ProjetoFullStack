import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { PratoCardapio } from '../../../core/models';
import { FotoComponent } from '../../../shared/foto/foto.component';
import { IconeComponent } from '../../../shared/icone/icone.component';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [RouterLink, FotoComponent, IconeComponent],
  templateUrl: './hero.component.html',
  styleUrl: './hero.component.css'
})
export class HeroComponent {
  @Input() destaque?: PratoCardapio;
  @Input() segundo?: PratoCardapio;
  @Input() total = 0;
}
