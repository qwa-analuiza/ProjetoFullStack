import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/auth.service';
import { IconeComponent } from '../../../shared/icone/icone.component';

@Component({
  selector: 'app-chamada-final',
  standalone: true,
  imports: [RouterLink, IconeComponent],
  templateUrl: './chamada-final.component.html',
  styleUrl: './chamada-final.component.css'
})
export class ChamadaFinalComponent {
  auth = inject(AuthService);
}
