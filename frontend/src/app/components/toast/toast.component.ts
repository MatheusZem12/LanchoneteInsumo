import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule, ToastModule],
  providers: [MessageService],
  templateUrl: './toast.component.html'
})
export class ToastComponent {
  private messageService = inject(MessageService);

  showSuccess(message: string, detail?: string) {
    this.messageService.add({
      severity: 'success',
      summary: message,
      detail: detail
    });
  }

  showError(message: string, detail?: string) {
    this.messageService.add({
      severity: 'error',
      summary: message,
      detail: detail
    });
  }

  showWarning(message: string, detail?: string) {
    this.messageService.add({
      severity: 'warn',
      summary: message,
      detail: detail
    });
  }

  showInfo(message: string, detail?: string) {
    this.messageService.add({
      severity: 'info',
      summary: message,
      detail: detail
    });
  }
}
