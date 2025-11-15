import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MenubarModule } from 'primeng/menubar';
import { ButtonModule } from 'primeng/button';
import { MenuItem } from 'primeng/api';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule, MenubarModule, ButtonModule],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {
  items: MenuItem[] = [];

  constructor(private authService: AuthService) {
    this.buildMenu();
  }

  buildMenu() {
    this.items = [
      {
        label: 'Dashboard',
        icon: 'pi pi-home',
        routerLink: '/dashboard'
      },
      {
        label: 'Insumos',
        icon: 'pi pi-box',
        routerLink: '/insumos'
      },
      {
        label: 'Movimentações',
        icon: 'pi pi-arrows-h',
        routerLink: '/movimentacoes'
      }
    ];

    if (this.authService.hasRole('ROLE_ADMIN')) {
      this.items.push({
        label: 'Usuários',
        icon: 'pi pi-users',
        routerLink: '/usuarios'
      });
    }
  }

  logout() {
    this.authService.logout();
  }
}
