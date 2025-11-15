import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { CardModule } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { PasswordModule } from 'primeng/password';
import { ButtonModule } from 'primeng/button';
import { MessageModule } from 'primeng/message';
import { AuthService } from '../../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CardModule,
    InputTextModule,
    PasswordModule,
    ButtonModule,
    MessageModule
  ],
  templateUrl: './login.component.html',
  styles: [`
    :host ::ng-deep {
      .p-card {
        box-shadow: 0 0 20px rgba(0, 0, 0, 0.1);
      }

      /* Login card specific spacing */
      .login-card {
        padding: 1.25rem; /* internal spacing */
        margin: 0.75rem;  /* external spacing */
        display: inline-block;
      }

      /* Ensure content inside card doesn't collapse padding */
      .login-card .p-card-content,
      .login-card .p-card-body {
        padding: 0;
      }

      /* Labels, inputs, helper text spacing */
      .login-card label {
        display: block;
        margin-bottom: 0.5rem;
      }

      .login-card input.p-inputtext,
      .login-card input {
        padding: 0.5rem 0.75rem;
        margin-bottom: 0.5rem;
        width: auto; /* allow card to wrap to content */
        min-width: 14rem; /* give a comfortable minimum */
        box-sizing: border-box;
      }

      .login-card small {
        margin-top: 0.25rem;
        display: block;
      }

      .login-card p-message {
        margin-bottom: 0.5rem;
        display: block;
      }

      .login-card .p-button {
        margin-top: 0.75rem;
        display: block;
      }

      /* Keep legacy password rules in case component uses p-password elsewhere */
      .p-password {
        width: 100%;
      }

      .p-password input {
        width: 100%;
      }
    }
  `]
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loginForm: FormGroup;
  loading = signal(false);
  errorMessage = signal('');

  constructor() {
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });

    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/dashboard']);
    }
  }

  onSubmit() {
    if (this.loginForm.valid) {
      this.loading.set(true);
      this.errorMessage.set('');

      this.authService.login(this.loginForm.value).subscribe({
        next: () => {
          this.router.navigate(['/dashboard']);
        },
        error: (error) => {
          this.loading.set(false);
          this.errorMessage.set('Email ou senha inválidos');
          console.error('Login error:', error);
        }
      });
    }
  }
}
