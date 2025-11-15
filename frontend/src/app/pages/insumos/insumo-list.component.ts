import { Component, OnInit, inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { DataTableComponent } from '../../components/data-table/data-table.component';
import { ToastComponent } from '../../components/toast/toast.component';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { InsumoService } from '../../../services/insumo.service';
import { Insumo } from '../../../types/insumo.type';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-insumo-list',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    DataTableComponent,
    ToastComponent,
    DialogModule,
    ButtonModule,
    InputTextModule,
    InputNumberModule,
    ReactiveFormsModule,
    ConfirmDialogModule
  ],
  providers: [ConfirmationService],
  templateUrl: './insumo-list.component.html'
})
export class InsumoListComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  private fb = inject(FormBuilder);
  private insumoService = inject(InsumoService);
  private confirmationService = inject(ConfirmationService);
  private authService = inject(AuthService);

  insumos: Insumo[] = [];
  displayDialog = false;
  editingInsumo: Insumo | null = null;
  loading = false;
  isAdmin = false;

  columns = [
    { field: 'codigo', header: 'Código' },
    { field: 'nome', header: 'Nome' },
    { field: 'descricao', header: 'Descrição' },
    { field: 'quantidade_critica', header: 'Qtd. Crítica' },
    { field: 'quantidade_estoque', header: 'Qtd. Estoque' }
  ];

  insumoForm: FormGroup;

  constructor() {
    this.insumoForm = this.fb.group({
      codigo: ['', Validators.required],
      nome: ['', Validators.required],
      descricao: [''],
      quantidade_critica: [0, [Validators.required, Validators.min(0)]]
    });
  }

  ngOnInit() {
    this.isAdmin = this.authService.hasRole('ROLE_ADMIN');
    this.loadInsumos();
  }

  loadInsumos() {
    this.insumoService.findAll().subscribe({
      next: (data) => {
        this.insumos = data;
      },
      error: (error) => {
        console.error('Error loading insumos:', error);
        this.toast?.showError('Erro ao carregar insumos');
      }
    });
  }

  openDialog(insumo?: Insumo) {
    this.editingInsumo = insumo || null;
    
    if (insumo) {
      this.insumoForm.patchValue(insumo);
    } else {
      this.insumoForm.reset({ quantidade_critica: 0 });
    }
    
    this.displayDialog = true;
  }

  closeDialog() {
    this.displayDialog = false;
    this.editingInsumo = null;
    this.insumoForm.reset({ quantidade_critica: 0 });
  }

  saveInsumo() {
    if (this.insumoForm.valid) {
      this.loading = true;
      const insumo: Insumo = this.insumoForm.value;

      const request = this.editingInsumo
        ? this.insumoService.update(this.editingInsumo.id!, insumo)
        : this.insumoService.create(insumo);

      request.subscribe({
        next: () => {
          this.toast?.showSuccess(
            this.editingInsumo ? 'Insumo atualizado com sucesso' : 'Insumo criado com sucesso'
          );
          this.loadInsumos();
          this.closeDialog();
          this.loading = false;
        },
        error: (error) => {
          console.error('Error saving insumo:', error);
          this.toast?.showError('Erro ao salvar insumo');
          this.loading = false;
        }
      });
    }
  }

  confirmDelete(insumo: Insumo) {
    this.confirmationService.confirm({
      message: `Deseja realmente excluir o insumo ${insumo.nome}?`,
      header: 'Confirmação',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      accept: () => {
        this.deleteInsumo(insumo);
      }
    });
  }

  deleteInsumo(insumo: Insumo) {
    this.insumoService.delete(insumo.id!).subscribe({
      next: () => {
        this.toast?.showSuccess('Insumo excluído com sucesso');
        this.loadInsumos();
      },
      error: (error) => {
        console.error('Error deleting insumo:', error);
        this.toast?.showError('Erro ao excluir insumo');
      }
    });
  }
}
