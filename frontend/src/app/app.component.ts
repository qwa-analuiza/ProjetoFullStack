import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { CabecalhoComponent } from './layout/cabecalho/cabecalho.component';
import { RodapeComponent } from './layout/rodape/rodape.component';
import { NavInferiorComponent } from './layout/nav-inferior/nav-inferior.component';
import { ToastComponent } from './shared/toast/toast.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CabecalhoComponent, RodapeComponent, NavInferiorComponent, ToastComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  private router = inject(Router);

  /** Login e cadastro ocupam a tela inteira, sem cabeçalho nem rodapé. */
  telaAuth = signal(this.ehAuth(this.router.url));

  constructor() {
    this.router.events
      .pipe(filter((e): e is NavigationEnd => e instanceof NavigationEnd), takeUntilDestroyed())
      .subscribe(e => this.telaAuth.set(this.ehAuth(e.urlAfterRedirects)));
  }

  private ehAuth(url: string) { return /^\/(login|cadastro)(\?|\/|$)/.test(url); }
}
