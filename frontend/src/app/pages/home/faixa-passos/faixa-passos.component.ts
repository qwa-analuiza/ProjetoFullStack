import { Component } from '@angular/core';

@Component({
  selector: 'app-faixa-passos',
  standalone: true,
  templateUrl: './faixa-passos.component.html',
  styleUrl: './faixa-passos.component.css'
})
export class FaixaPassosComponent {
  passos = [
    { n: '01', titulo: 'Escolha no cardápio' },
    { n: '02', titulo: 'Finalize seu pedido' },
    { n: '03', titulo: 'Preparo na hora' },
    { n: '04', titulo: 'Entrega na sua porta' }
  ];
}
