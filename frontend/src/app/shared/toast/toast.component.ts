import { Component, inject } from '@angular/core';
import { Aviso } from '../../core/aviso.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  templateUrl: './toast.component.html',
  styleUrl: './toast.component.css'
})
export class ToastComponent {
  aviso = inject(Aviso);
}
