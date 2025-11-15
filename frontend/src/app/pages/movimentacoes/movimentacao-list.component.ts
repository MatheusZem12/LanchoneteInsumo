import { Component, OnInit, inject, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../components/navbar/navbar.component';
import { DataTableComponent } from '../../components/data-table/data-table.component';
import { ToastComponent } from '../../components/toast/toast.component';
import { DialogModule } from 'primeng/dialog';
import { ButtonModule } from 'primeng/button';
import { InputNumberModule } from 'primeng/inputnumber';
import { Select } from 'primeng/select';
import { DatePicker } from 'primeng/datepicker';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ConfirmationService } from 'primeng/api';
import { MovimentacaoInsumoService } from '../../../services/movimentacao-insumo.service';
import { InsumoService } from '../../../services/insumo.service';
import { MovimentacaoInsumo } from '../../../types/movimentacao-insumo.type';
import { Insumo } from '../../../types/insumo.type';
import { TipoMovimentacao } from '../../../types/tipo-movimentacao.enum';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-movimentacao-list',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    DataTableComponent,
    ToastComponent,
    DialogModule,
    ButtonModule,
    InputNumberModule,
    Select,
    DatePicker,
    ReactiveFormsModule,
    ConfirmDialogModule
  ],
  providers: [ConfirmationService],
  templateUrl: './movimentacao-list.component.html'
})
export class MovimentacaoListComponent implements OnInit {
  @ViewChild(ToastComponent) toast!: ToastComponent;

  private fb = inject(FormBuilder);
  private movimentacaoService = inject(MovimentacaoInsumoService);
  private insumoService = inject(InsumoService);
  private confirmationService = inject(ConfirmationService);
  private authService = inject(AuthService);

  movimentacoes: MovimentacaoInsumo[] = [];
  movimentacoesDisplay: any[] = [];
  insumos: Insumo[] = [];
  displayDialog = false;
  editingMovimentacao: any | null = null;
  loading = false;
  isAdmin = false;

  columns = [
    { field: 'insumo_codigo', header: 'Código' },
    { field: 'insumo_nome', header: 'Insumo' },
    { field: 'tipo_movimentacao', header: 'Tipo' },
    { field: 'quantidade', header: 'Quantidade' },
    { field: 'data', header: 'Data' }
  ];

  tiposMovimentacao = [
    { label: 'Entrada', value: TipoMovimentacao.ENTRADA },
    { label: 'Saída', value: TipoMovimentacao.SAIDA }
  ];

  movimentacaoForm: FormGroup;

  constructor() {
    this.movimentacaoForm = this.fb.group({
      insumo_id: [null, Validators.required],
      tipo_movimentacao: ['', Validators.required],
      quantidade: [1, [Validators.required, Validators.min(1)]],
      data: [new Date()]
    });
  }

  ngOnInit() {
    this.isAdmin = this.authService.hasRole('ROLE_ADMIN');
    this.loadInsumos(); // Load insumos for the form dropdown
    this.loadMovimentacoes();
  }

  loadData() {
    // Load insumos first, then movimentacoes
    this.insumoService.findAll().subscribe({
      next: (data) => {
        this.insumos = data;
        // Only load movimentacoes after insumos are loaded
        this.loadMovimentacoes();
      },
      error: (error) => {
        console.error('Error loading insumos:', error);
        this.toast?.showError('Erro ao carregar insumos');
      }
    });
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

  loadMovimentacoes() {
    this.movimentacaoService.findAll().subscribe({
      next: (data) => {
        this.movimentacoes = data;
        console.log('DEBUG - Primeira movimentação recebida:', data[0]);
        // Process for display (format date)
        this.movimentacoesDisplay = this.movimentacoes.map(m => ({
          ...m,
          data: m.data ? new Date(m.data).toLocaleString('pt-BR') : 'N/A'
        }));
        console.log('DEBUG - Primeira movimentação para display:', this.movimentacoesDisplay[0]);
        console.log('DEBUG - Campos disponíveis:', Object.keys(this.movimentacoesDisplay[0] || {}));
      },
      error: (error) => {
        console.error('Error loading movimentacoes:', error);
        this.toast?.showError('Erro ao carregar movimentações');
      }
    });
  }

  processMovimentacoesForDisplay() {
    // Not needed anymore - insumo_nome comes from backend
    this.movimentacoesDisplay = this.movimentacoes.map(m => ({
      ...m,
      data: m.data ? new Date(m.data).toLocaleString('pt-BR') : 'N/A'
    }));
  }

  openDialog(movimentacao?: any) {
    this.editingMovimentacao = movimentacao || null;
    
    if (movimentacao) {
      // Find the original movimentacao
      const original = this.movimentacoes.find(m => m.id === movimentacao.id);
      if (original) {
        this.movimentacaoForm.patchValue({
          insumo_id: original.insumo_id,
          tipo_movimentacao: original.tipo_movimentacao,
          quantidade: original.quantidade,
          data: original.data ? new Date(original.data) : new Date()
        });
      }
    } else {
      this.movimentacaoForm.reset({
        quantidade: 1,
        data: new Date()
      });
    }
    
    this.displayDialog = true;
  }

  closeDialog() {
    this.displayDialog = false;
    this.editingMovimentacao = null;
    this.movimentacaoForm.reset({
      quantidade: 1,
      data: new Date()
    });
  }

  saveMovimentacao() {
    if (this.movimentacaoForm.valid) {
      this.loading = true;
      const formValue = this.movimentacaoForm.value;
      
      // Get current user id from auth service
      const currentUser = this.authService['currentUserSubject'].value;
      const usuarioId = currentUser?.id || '1'; // Fallback to '1' if not available
      
      const movimentacao: MovimentacaoInsumo = {
        ...formValue,
        usuario_id: usuarioId,
        data: formValue.data ? this.formatDateForBackend(formValue.data) : this.formatDateForBackend(new Date())
      };

      const request = this.editingMovimentacao
        ? this.movimentacaoService.update(this.editingMovimentacao.id!, movimentacao)
        : this.movimentacaoService.create(movimentacao);

      request.subscribe({
        next: () => {
          this.toast?.showSuccess(
            this.editingMovimentacao ? 'Movimentação atualizada com sucesso' : 'Movimentação criada com sucesso'
          );
          this.loadMovimentacoes();
          this.closeDialog();
          this.loading = false;
        },
        error: (error) => {
          console.error('Error saving movimentacao:', error);
          this.toast?.showError('Erro ao salvar movimentação');
          this.loading = false;
        }
      });
    }
  }

  formatDateForBackend(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;
  }

  confirmDelete(movimentacao: any) {
    this.confirmationService.confirm({
      message: `Deseja realmente excluir esta movimentação?`,
      header: 'Confirmação',
      icon: 'pi pi-exclamation-triangle',
      acceptLabel: 'Sim',
      rejectLabel: 'Não',
      accept: () => {
        this.deleteMovimentacao(movimentacao);
      }
    });
  }

  deleteMovimentacao(movimentacao: any) {
    this.movimentacaoService.delete(movimentacao.id!).subscribe({
      next: () => {
        this.toast?.showSuccess('Movimentação excluída com sucesso');
        this.loadMovimentacoes();
      },
      error: (error) => {
        console.error('Error deleting movimentacao:', error);
        this.toast?.showError('Erro ao excluir movimentação');
      }
    });
  }
}
