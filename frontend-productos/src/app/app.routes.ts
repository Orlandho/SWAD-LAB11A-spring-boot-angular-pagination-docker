import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'productos',
    pathMatch: 'full'
  },
  {
    path: 'productos',
    loadComponent: () =>
      import('./productos/productos.component').then(m => m.ProductosComponent)
  },
  {
    path: '**',
    redirectTo: 'productos'
  }
];
