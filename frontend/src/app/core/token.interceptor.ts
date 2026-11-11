import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { tap } from 'rxjs';
import { AuthService } from './auth.service';
import { API_URL } from './api';

/** Envia o token JWT em toda chamada à API e desloga se ele expirar (401). */
export const tokenInterceptor: HttpInterceptorFn = (requisicao, proximo) => {
  const auth = inject(AuthService);
  const token = auth.token;
  const paraApi = requisicao.url.startsWith(API_URL);
  const enviada = token && paraApi
    ? requisicao.clone({ setHeaders: { Authorization: 'Bearer ' + token } })
    : requisicao;

  return proximo(enviada).pipe(
    tap({
      error: (e) => {
        const ehLogin = requisicao.url.includes('/auth/');
        if (e?.status === 401 && token && !ehLogin) auth.sair();
      }
    })
  );
};
