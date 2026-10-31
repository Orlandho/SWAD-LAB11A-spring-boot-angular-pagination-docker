import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProductoService } from './producto.service';
import { Pagina } from '../models/pagina';
import { Producto } from '../models/producto';

describe('ProductoService', () => {
  let service: ProductoService;
  let httpTesting: HttpTestingController;

  const mockPagina: Pagina<Producto> = {
    content: [
      { id: 1, nombre: 'Laptop Lenovo', precio: 2500, stock: 10, estadoStock: 'OK' },
      { id: 2, nombre: 'Mouse Logitech', precio: 80, stock: 25, estadoStock: 'OK' }
    ],
    totalElements: 2,
    totalPages: 1,
    size: 5,
    number: 0,
    first: true,
    last: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ProductoService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ProductoService);
    httpTesting = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTesting.verify();
  });

  it('debe crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('listarPaginado debe solicitar la ruta /api/productos/pagina con parametros page, size y sort', () => {
    service.listarPaginado(1, 10, 'nombre,asc').subscribe(data => {
      expect(data).toEqual(mockPagina);
      expect(data.content.length).toBe(2);
    });

    const req = httpTesting.expectOne((request) => {
      return (
        request.url === '/api/productos/pagina' &&
        request.params.get('page') === '1' &&
        request.params.get('size') === '10' &&
        request.params.get('sort') === 'nombre,asc'
      );
    });

    expect(req.request.method).toBe('GET');
    req.flush(mockPagina);
  });

  it('buscarPorNombre debe solicitar /api/productos/pagina/buscar con nombre y paginacion', () => {
    service.buscarPorNombre('laptop', 0, 5).subscribe(data => {
      expect(data.content[0].nombre).toBe('Laptop Lenovo');
    });

    const req = httpTesting.expectOne((request) => {
      return (
        request.url === '/api/productos/pagina/buscar' &&
        request.params.get('nombre') === 'laptop' &&
        request.params.get('page') === '0' &&
        request.params.get('size') === '5'
      );
    });

    expect(req.request.method).toBe('GET');
    req.flush(mockPagina);
  });
});
