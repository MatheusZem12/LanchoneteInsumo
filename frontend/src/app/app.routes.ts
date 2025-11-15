import { Routes } from '@angular/router';
import { authGuard } from '../guards/auth.guard';
import { roleGuard } from '../guards/role.guard';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { InsumoListComponent } from './pages/insumos/insumo-list.component';
import { MovimentacaoListComponent } from './pages/movimentacoes/movimentacao-list.component';
import { UsuarioListComponent } from './pages/usuarios/usuario-list.component';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'dashboard',
    component: DashboardComponent,
    canActivate: [authGuard]
  },
  {
    path: 'insumos',
    component: InsumoListComponent,
    canActivate: [authGuard]
  },
  {
    path: 'movimentacoes',
    component: MovimentacaoListComponent,
    canActivate: [authGuard]
  },
  {
    path: 'usuarios',
    component: UsuarioListComponent,
    canActivate: [roleGuard(['ROLE_ADMIN'])]
  },
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];
