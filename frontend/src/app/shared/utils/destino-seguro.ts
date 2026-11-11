/** Só aceita destino interno do app (começando com "/"), para evitar redirecionamento externo. */
export function destinoSeguro(valor: string | null): string | null {
  return valor && valor.startsWith('/') && !valor.startsWith('//') ? valor : null;
}
