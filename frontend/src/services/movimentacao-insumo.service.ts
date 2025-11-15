import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment.dev';
import { MovimentacaoInsumo } from '../types/movimentacao-insumo.type';

@Injectable({
  providedIn: 'root'
})
export class MovimentacaoInsumoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/lanchonete/api/movimentacoes_insumos`;

  findAll(): Observable<MovimentacaoInsumo[]> {
    return this.http.get<MovimentacaoInsumo[]>(this.apiUrl);
  }

  findById(id: number): Observable<MovimentacaoInsumo> {
    return this.http.get<MovimentacaoInsumo>(`${this.apiUrl}/${id}`);
  }

  create(movimentacao: MovimentacaoInsumo): Observable<MovimentacaoInsumo> {
    return this.http.post<MovimentacaoInsumo>(this.apiUrl, movimentacao);
  }

  update(id: number, movimentacao: MovimentacaoInsumo): Observable<MovimentacaoInsumo> {
    return this.http.put<MovimentacaoInsumo>(`${this.apiUrl}/${id}`, movimentacao);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
