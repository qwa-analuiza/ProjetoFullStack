import { inject } from '@angular/core';
import { Router, Routes } from '@angular/router';
import { AuthService } from './core/auth.service';
import { logadoGuard, perfilGuard, soClienteGuard } from './core/guards';

const equipe = perfilGuard('COZINHEIRO', 'MOTOBOY', 'GERENTE', 'ADMIN');
const cozinha = perfilGuard('COZINHEIRO', 'GERENTE', 'ADMIN');
const entregas = perfilGuard('MOTOBOY', 'GERENTE', 'ADMIN');
const gestao = perfilGuard('GERENTE', 'ADMIN');
const soAdmin = perfilGuard('ADMIN');

export const routes: Routes = [
  { path: '', pathMatch: 'full', loadComponent: () => import('./pages/home/home.component').then(m => m.HomeComponent) },
  { path: 'prato/:id', loadComponent: () => import('./pages/prato/prato.component').then(m => m.PratoComponent) },
  { path: 'carrinho', canActivate: [soClienteGuard('carrinho')], loadComponent: () => import('./pages/carrinho/carrinho.component').then(m => m.CarrinhoComponent) },
  { path: 'login', loadComponent: () => import('./pages/login/login.component').then(m => m.LoginComponent) },
  { path: 'cadastro', loadComponent: () => import('./pages/cadastro/cadastro.component').then(m => m.CadastroComponent) },
  { path: 'pedidos', canActivate: [logadoGuard, soClienteGuard('pedidos')], loadComponent: () => import('./pages/pedidos/pedidos.component').then(m => m.PedidosComponent) },
  { path: 'pedidos/:id', canActivate: [logadoGuard], loadComponent: () => import('./pages/pedido/pedido.component').then(m => m.PedidoComponent) },
  { path: 'perfil', canActivate: [logadoGuard], loadComponent: () => import('./pages/perfil/perfil.component').then(m => m.PerfilComponent) },
  {
    path: 'admin',
    canActivate: [logadoGuard, equipe],
    loadComponent: () => import('./admin/painel/painel.component').then(m => m.PainelComponent),
    children: [
      {
        path: '', pathMatch: 'full', children: [],
        canActivate: [() => inject(Router).createUrlTree([inject(AuthService).rotaInicial()])]
      },
      { path: 'pedidos', canActivate: [cozinha], loadComponent: () => import('./admin/fila/fila.component').then(m => m.FilaComponent) },
      { path: 'entregas', canActivate: [entregas], loadComponent: () => import('./admin/entregas/entregas.component').then(m => m.EntregasComponent) },
      { path: 'dashboard', canActivate: [gestao], loadComponent: () => import('./admin/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      { path: 'ingredientes', canActivate: [gestao], loadComponent: () => import('./admin/ingredientes/ingredientes.component').then(m => m.IngredientesComponent) },
      { path: 'estoque', canActivate: [gestao], loadComponent: () => import('./admin/estoque/estoque.component').then(m => m.EstoqueComponent) },
      { path: 'fornecedores', canActivate: [gestao], loadComponent: () => import('./admin/fornecedores/fornecedores.component').then(m => m.FornecedoresComponent) },
      { path: 'compras', canActivate: [gestao], loadComponent: () => import('./admin/compras/compras.component').then(m => m.ComprasComponent) },
      { path: 'usuarios', canActivate: [soAdmin], loadComponent: () => import('./admin/usuarios/usuarios.component').then(m => m.UsuariosComponent) }
    ]
  },
  { path: '**', redirectTo: '' }
];
