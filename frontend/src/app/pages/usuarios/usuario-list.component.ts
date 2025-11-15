import { Component, OnInit, inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { DataTableComponent } from '../../components/data-table/data-table.component';
import { ToastComponent } from '../../components/toast/toast.component';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { UsuarioService } from '../../../services/usuario.service';
import { AuthService } from '../../../services/auth.service';
import { Usuario } from '../../../types/usuario.type';

@Component({
  selector: 'app-usuario-list',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    DataTableComponent,
    ToastComponent,
    DialogModule,
    ButtonModule,
    InputTextModule,
    PasswordModule,
    ReactiveFormsModule,
    ConfirmDialogModule
  ],
  providers: [ConfirmationService],
  templateUrl: './usuario-list.component.html'
})
export class UsuarioListComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  private fb = inject(FormBuilder);
  private usuarioService = inject(UsuarioService);
  private authService = inject(AuthService);
  private confirmationService = inject(ConfirmationService);

  usuarios: Usuario[] = [];
  displayDialog = false;
  editingUsuario: Usuario | null = null;
  loading = false;
  isAdmin = false;

  columns = [
    { field: 'nome', header: 'Nome' },
    { field: 'email', header: 'Email' },
    { field: 'telefone', header: 'Telefone' }
  ];

  usuarioForm: FormGroup;

  constructor() {
    this.usuarioForm = this.fb.group({
      nome: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      senha: ['', [Validators.required, Validators.minLength(6)]],
      telefone: [''],
      role: [[], Validators.required]
    });
  }

  ngOnInit() {
    this.isAdmin = this.authService.hasRole('ROLE_ADMIN');
    this.loadUsuarios();
  }

  loadUsuarios() {
    this.usuarioService.findAll().subscribe({
      next: (data) => {
        this.usuarios = data;
      },
      error: (error) => {
        console.error('Error loading usuarios:', error);
        this.toast?.showError('Erro ao carregar usuários');
      }
    });
  }

  openDialog(usuario?: Usuario) {
    this.editingUsuario = usuario || null;
    
    if (usuario) {
      this.usuarioForm.patchValue({
        nome: usuario.nome,
        email: usuario.email,
        telefone: usuario.telefone,
        role: usuario.role && usuario.role.length > 0 ? usuario.role[0] : ''
      });
      // Make senha optional when editing
      this.usuarioForm.get('senha')?.clearValidators();
      this.usuarioForm.get('senha')?.updateValueAndValidity();
    } else {
      this.usuarioForm.reset();
      // Make senha required when creating
      this.usuarioForm.get('senha')?.setValidators([Validators.required, Validators.minLength(6)]);
      this.usuarioForm.get('senha')?.updateValueAndValidity();
    }
    
    this.displayDialog = true;
  }

  closeDialog() {
    this.displayDialog = false;
    this.editingUsuario = null;
    this.usuarioForm.reset();
  }

  saveUsuario() {
    if (this.usuarioForm.valid) {
      this.loading = true;
      const formValue = this.usuarioForm.value;
      
      const usuario: Usuario = {
        ...formValue,
        id: this.editingUsuario?.id,
        role: formValue.role ? [formValue.role] : [] // Convert string to array
      };

      const request = this.editingUsuario
        ? this.usuarioService.update(this.editingUsuario.id!, usuario)
        : this.usuarioService.create(usuario);

      request.subscribe({
        next: () => {
          this.toast?.showSuccess(
            this.editingUsuario ? 'Usuário atualizado com sucesso' : 'Usuário criado com sucesso'
          );
          this.loadUsuarios();
          this.closeDialog();
          this.loading = false;
        },
        error: (error) => {
          console.error('Error saving usuario:', error);
          this.toast?.showError('Erro ao salvar usuário');
          this.loading = false;
        }
      });
    }
  }

  confirmDelete(usuario: Usuario) {
    this.confirmationService.confirm({
      message: `Deseja realmente excluir o usuário ${usuario.nome}?`,
      header: 'Confirmação',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      accept: () => {
        this.deleteUsuario(usuario);
      }
    });
  }

  deleteUsuario(usuario: Usuario) {
    this.usuarioService.delete(usuario.id!).subscribe({
      next: () => {
        this.toast?.showSuccess('Usuário excluído com sucesso');
        this.loadUsuarios();
      },
      error: (error) => {
        console.error('Error deleting usuario:', error);
        this.toast?.showError('Erro ao excluir usuário');
      }
    });
  }
}
