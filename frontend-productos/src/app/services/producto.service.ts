import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Producto } from '../models/producto';
import { Pagina } from '../models/pagina';

@Injectable({
  providedIn: 'root'
})
export class ProductoService {
  private http = inject(HttpClient);
  private apiUrl = '/api/productos';

  listarPaginado(
    page: number = 0,
    size: number = 5,
    sort: string = 'id,asc'
  ): Observable<Pagina<Producto>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<Pagina<Producto>>(`${this.apiUrl}/pagina`, { params });
  }

  buscarPorNombre(
    nombre: string,
    page: number = 0,
    size: number = 5,
    sort: string = 'id,asc'
  ): Observable<Pagina<Producto>> {
    const params = new HttpParams()
      .set('nombre', nombre)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    return this.http.get<Pagina<Producto>>(`${this.apiUrl}/pagina/buscar`, { params });
  }
}
