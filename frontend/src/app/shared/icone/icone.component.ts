import { Component, Input } from '@angular/core';

const ICONES: Record<string, string[]> = {
  home: ['M3 11l9-8 9 8', 'M5 10v10h14V10', 'M10 20v-6h4v6'],
  list: ['M8 6h13', 'M8 12h13', 'M8 18h13', 'M3 6h.01', 'M3 12h.01', 'M3 18h.01'],
  cart: ['M3 4h2l2.4 11.2a2 2 0 0 0 2 1.6h7.7a2 2 0 0 0 2-1.5L21 8H6', 'M10 21h.01', 'M18 21h.01'],
  user: ['M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2', 'M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8'],
  search: ['M11 19a8 8 0 1 0 0-16 8 8 0 0 0 0 16', 'M21 21l-4.3-4.3'],
  plus: ['M12 5v14', 'M5 12h14'],
  minus: ['M5 12h14'],
  arrow: ['M5 12h14', 'M13 6l6 6-6 6'],
  back: ['M19 12H5', 'M11 6l-6 6 6 6'],
  chef: ['M6 13.9V20h12v-6.1', 'M6 14a4 4 0 0 1 1-7.9 5 5 0 0 1 9.9 0A4 4 0 0 1 18 14', 'M6 17h12'],
  close: ['M18 6L6 18', 'M6 6l12 12'],
  pin: ['M20 10c0 6-8 12-8 12s-8-6-8-12a8 8 0 0 1 16 0', 'M12 13a3 3 0 1 0 0-6 3 3 0 0 0 0 6'],
  trash: ['M3 6h18', 'M8 6V4h8v2', 'M19 6l-1 14H6L5 6', 'M10 11v6', 'M14 11v6'],
  check: ['M5 13l4 4L19 7'],
  edit: ['M12 20h9', 'M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4z'],
  logout: ['M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4', 'M16 17l5-5-5-5', 'M21 12H9'],
  flame: ['M8.5 14.5A2.5 2.5 0 0 0 11 12c0-1.38-.5-2-1-3-1.07-2.14-.22-4.05 2-6 .5 2.5 2 4.9 4 6.5 2 1.6 3 3.5 3 5.5a7 7 0 1 1-14 0c0-1.15.43-2.29 1-3a2.5 2.5 0 0 0 2.5 2.5z'],
  utensils: ['M3 2v7a2 2 0 0 0 2 2h4a2 2 0 0 0 2-2V2', 'M7 2v20', 'M21 15V2a5 5 0 0 0-5 5v6a2 2 0 0 0 2 2h3zm0 0v7'],
  receipt: ['M4 2v20l3-2 2 2 3-2 3 2 2-2 3 2V2l-3 2-2-2-3 2-3-2-2 2z', 'M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H8', 'M12 17.5v-11'],
  box: ['M21 8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z', 'M3.3 7L12 12l8.7-5', 'M12 22V12'],
  truck: ['M14 18V6a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2v11a1 1 0 0 0 1 1h2', 'M15 18H9', 'M19 18h2a1 1 0 0 0 1-1v-3.65a1 1 0 0 0-.22-.62l-3.48-4.35A1 1 0 0 0 17.52 8H14', 'M19 18a2 2 0 1 1-4 0 2 2 0 0 1 4 0', 'M9 18a2 2 0 1 1-4 0 2 2 0 0 1 4 0'],
  basket: ['M2 11h20', 'M4.5 11l1.6 7.4a2 2 0 0 0 2 1.6h7.8a2 2 0 0 0 2-1.6l1.6-7.4', 'M9 11l3-7', 'M15 11l-3-7']
};

/** Ícones minimalistas (traço fino), todos com a mesma aparência. */
@Component({
  selector: 'app-icone',
  standalone: true,
  templateUrl: './icone.component.html',
  styleUrl: './icone.component.css'
})
export class IconeComponent {
  @Input() nome = '';
  caminhos() { return ICONES[this.nome] ?? []; }
}
