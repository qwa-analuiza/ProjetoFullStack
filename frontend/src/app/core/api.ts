import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';

/** Endereço do backend: vem de src/environments (environment.prod.ts no build de produção). */
export const API_URL = environment.apiUrl;

@Injectable({ providedIn: 'root' })
export class Api {
  private http = inject(HttpClient);

  get<T>(caminho: string, params?: Record<string, string | number>) {
    return firstValueFrom(this.http.get<T>(API_URL + caminho, { params }));
  }
  post<T>(caminho: string, corpo?: unknown) {
    return firstValueFrom(this.http.post<T>(API_URL + caminho, corpo ?? {}));
  }
  put<T>(caminho: string, corpo: unknown) {
    return firstValueFrom(this.http.put<T>(API_URL + caminho, corpo));
  }
  patch<T>(caminho: string, corpo: unknown) {
    return firstValueFrom(this.http.patch<T>(API_URL + caminho, corpo));
  }
  delete(caminho: string) {
    return firstValueFrom(this.http.delete<void>(API_URL + caminho));
  }
}
