import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment.dev';
import { Insumo } from '../types/insumo.type';

@Injectable({
  providedIn: 'root'
})
export class InsumoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = `${environment.apiUrl}/lanchonete/api/insumos`;

  findAll(): Observable<Insumo[]> {
    return this.http.get<Insumo[]>(this.apiUrl);
  }

  findById(id: number): Observable<Insumo> {
    return this.http.get<Insumo>(`${this.apiUrl}/${id}`);
  }

  create(insumo: Insumo): Observable<Insumo> {
    return this.http.post<Insumo>(this.apiUrl, insumo);
  }

  update(id: number, insumo: Insumo): Observable<Insumo> {
    return this.http.put<Insumo>(`${this.apiUrl}/${id}`, insumo);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
