import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductoService } from '../services/producto.service';
import { Producto } from '../models/producto';

@Component({
  selector: 'app-productos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './productos.component.html',
  styleUrl: './productos.component.css'
})
export class ProductosComponent implements OnInit {
  private readonly productoService = inject(ProductoService);

  productos: Producto[] = [];
  pagina: number = 0;
  tamanio: number = 5;
  totalPaginas: number = 0;
  totalElementos: number = 0;
  primera: boolean = true;
  ultima: boolean = false;
  cargando: boolean = false;
  errorMensaje: string | null = null;

  // Actividad guiada: opciones de tamaño de página
  readonly opcionesTamanio: number[] = [5, 10, 20];

  // Reto adicional: búsqueda por nombre con paginación persistente
  terminoBusqueda: string = '';

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos(): void {
    this.cargando = true;
    this.errorMensaje = null;

    const busqueda = this.terminoBusqueda.trim();
    const observable = busqueda.length > 0
      ? this.productoService.buscarPorNombre(busqueda, this.pagina, this.tamanio)
      : this.productoService.listarPaginado(this.pagina, this.tamanio);

    observable.subscribe({
      next: (respuesta) => {
        this.productos = respuesta.content || [];
        this.totalPaginas = respuesta.totalPages || 0;
        this.totalElementos = respuesta.totalElements || 0;
        this.primera = respuesta.first ?? (this.pagina === 0);
        this.ultima = respuesta.last ?? (this.pagina >= this.totalPaginas - 1);
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar productos:', err);
        this.errorMensaje = 'No se pudo conectar con el backend de productos. Verifique que Spring Boot y MySQL estén en ejecución.';
        this.cargando = false;
      }
    });
  }

  anterior(): void {
    if (!this.primera && this.pagina > 0) {
      this.pagina--;
      this.cargarProductos();
    }
  }

  siguiente(): void {
    if (!this.ultima && this.totalPaginas > 0 && this.pagina < this.totalPaginas - 1) {
      this.pagina++;
      this.cargarProductos();
    }
  }

  // Actividad guiada: selector de tamaño de página
  cambiarTamanio(): void {
    this.pagina = 0;
    this.cargarProductos();
  }

  // Reto adicional: búsqueda por término
  buscar(): void {
    this.pagina = 0;
    this.cargarProductos();
  }

  limpiarBusqueda(): void {
    this.terminoBusqueda = '';
    this.pagina = 0;
    this.cargarProductos();
  }
}
