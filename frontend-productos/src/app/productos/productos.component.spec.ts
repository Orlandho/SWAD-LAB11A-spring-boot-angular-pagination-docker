import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductosComponent } from './productos.component';
import { ProductoService } from '../services/producto.service';
import { of, throwError } from 'rxjs';
import { Pagina } from '../models/pagina';
import { Producto } from '../models/producto';
import { vi } from 'vitest';

describe('ProductosComponent', () => {
  let component: ProductosComponent;
  let fixture: ComponentFixture<ProductosComponent>;
  let mockProductoService: {
    listarPaginado: ReturnType<typeof vi.fn>;
    buscarPorNombre: ReturnType<typeof vi.fn>;
  };

  const mockPagina: Pagina<Producto> = {
    content: [
      { id: 1, nombre: 'Laptop Lenovo', precio: 2500, stock: 10, estadoStock: 'OK' },
      { id: 2, nombre: 'UPS 1000VA', precio: 520, stock: 3, estadoStock: 'INSUFICIENTE' }
    ],
    totalElements: 2,
    totalPages: 1,
    size: 5,
    number: 0,
    first: true,
    last: true
  };

  beforeEach(async () => {
    mockProductoService = {
      listarPaginado: vi.fn().mockReturnValue(of(mockPagina)),
      buscarPorNombre: vi.fn().mockReturnValue(of(mockPagina))
    };

    await TestBed.configureTestingModule({
      imports: [ProductosComponent],
      providers: [
        { provide: ProductoService, useValue: mockProductoService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ProductosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debe crearse e inicializar la lista de productos', () => {
    expect(component).toBeTruthy();
    expect(mockProductoService.listarPaginado).toHaveBeenCalledWith(0, 5);
    expect(component.productos.length).toBe(2);
    expect(component.totalElementos).toBe(2);
    expect(component.primera).toBe(true);
    expect(component.ultima).toBe(true);
  });

  it('cambiarTamanio debe reiniciar a pagina 0 y recargar', () => {
    component.pagina = 3;
    component.tamanio = 10;
    component.cambiarTamanio();

    expect(component.pagina).toBe(0);
    expect(mockProductoService.listarPaginado).toHaveBeenCalledWith(0, 10);
  });

  it('buscar debe llamar a buscarPorNombre si hay termino de busqueda', () => {
    component.terminoBusqueda = 'Laptop';
    component.buscar();

    expect(component.pagina).toBe(0);
    expect(mockProductoService.buscarPorNombre).toHaveBeenCalledWith('Laptop', 0, 5);
  });

  it('limpiarBusqueda debe limpiar el termino y recargar catalogo completo', () => {
    component.terminoBusqueda = 'Monitor';
    component.limpiarBusqueda();

    expect(component.terminoBusqueda).toBe('');
    expect(component.pagina).toBe(0);
    expect(mockProductoService.listarPaginado).toHaveBeenCalledWith(0, 5);
  });

  it('anterior() no debe retroceder si esta en la primera pagina', () => {
    component.pagina = 0;
    component.primera = true;
    mockProductoService.listarPaginado.mockClear();

    component.anterior();
    expect(mockProductoService.listarPaginado).not.toHaveBeenCalled();
  });

  it('siguiente() debe avanzar si no esta en la ultima pagina', () => {
    component.pagina = 0;
    component.ultima = false;
    component.totalPaginas = 3;

    component.siguiente();
    expect(component.pagina).toBe(1);
  });

  it('debe manejar errores del backend mostrando mensaje al usuario', () => {
    mockProductoService.listarPaginado.mockReturnValue(throwError(() => new Error('Error de red')));
    component.cargarProductos();

    expect(component.cargando).toBe(false);
    expect(component.errorMensaje).toContain('No se pudo conectar');
  });
});
