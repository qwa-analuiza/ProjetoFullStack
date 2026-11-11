import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { Perfil } from './models';

/** Só deixa passar quem está logado; senão manda para o login e volta depois. */
export const logadoGuard: CanActivateFn = (_rota, estado) => {
  if (inject(AuthService).logado()) return true;
  return inject(Router).createUrlTree(['/login'], { queryParams: { voltar: estado.url } });
};

/**
 * Equipe (admin, gerente, cozinha, motoboy) não compra: carrinho e "meus pedidos" levam ao painel.
 * Em "pedidos", quem tem acesso à cozinha vai para a fila; o motoboy, para as entregas.
 */
export const soClienteGuard = (tela: 'carrinho' | 'pedidos'): CanActivateFn => () => {
  const auth = inject(AuthService);
  if (!auth.funcionario()) return true;
  const destino = tela === 'pedidos'
    ? (auth.perfil() === 'MOTOBOY' ? '/admin/entregas' : '/admin/pedidos')
    : auth.rotaInicial();
  return inject(Router).createUrlTree([destino]);
};

/** Só deixa passar os perfis informados. */
export const perfilGuard = (...perfis: Perfil[]): CanActivateFn => () => {
  if (inject(AuthService).temPerfil(...perfis)) return true;
  return inject(Router).createUrlTree(['/']);
};
