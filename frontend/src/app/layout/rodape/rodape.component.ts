import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { LogoComponent } from '../../shared/logo/logo.component';

@Component({
  selector: 'app-rodape',
  standalone: true,
  imports: [RouterLink, LogoComponent],
  templateUrl: './rodape.component.html',
  styleUrl: './rodape.component.css'
})
export class RodapeComponent {
  auth = inject(AuthService);
  ano = new Date().getFullYear();
}
