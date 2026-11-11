import { Component, OnInit, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth.service';
import { IconeComponent } from '../../shared/icone/icone.component';
import { RotuloPipe } from '../../shared/pipes/rotulo.pipe';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [RouterLink, IconeComponent, RotuloPipe],
  templateUrl: './perfil.component.html',
  styleUrl: './perfil.component.css'
})
export class PerfilComponent implements OnInit {
  auth = inject(AuthService);
  async ngOnInit() { try { await this.auth.atualizar(); } catch { /* mantém os dados salvos */ } }
}
