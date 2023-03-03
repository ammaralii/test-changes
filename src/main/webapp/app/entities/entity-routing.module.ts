import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'addresses',
        data: { pageTitle: 'Addresses' },
        loadChildren: () => import('./testChanges/addresses/addresses.module').then(m => m.TestChangesAddressesModule),
      },
      {
        path: 'cars',
        data: { pageTitle: 'Cars' },
        loadChildren: () => import('./testChanges/cars/cars.module').then(m => m.TestChangesCarsModule),
      },
      {
        path: 'categories',
        data: { pageTitle: 'Categories' },
        loadChildren: () => import('./testChanges/categories/categories.module').then(m => m.TestChangesCategoriesModule),
      },
      {
        path: 'colors',
        data: { pageTitle: 'Colors' },
        loadChildren: () => import('./testChanges/colors/colors.module').then(m => m.TestChangesColorsModule),
      },
      {
        path: 'customers',
        data: { pageTitle: 'Customers' },
        loadChildren: () => import('./testChanges/customers/customers.module').then(m => m.TestChangesCustomersModule),
      },
      {
        path: 'daraz-users',
        data: { pageTitle: 'DarazUsers' },
        loadChildren: () => import('./testChanges/daraz-users/daraz-users.module').then(m => m.TestChangesDarazUsersModule),
      },
      {
        path: 'order-delivery',
        data: { pageTitle: 'OrderDeliveries' },
        loadChildren: () => import('./testChanges/order-delivery/order-delivery.module').then(m => m.TestChangesOrderDeliveryModule),
      },
      {
        path: 'order-details',
        data: { pageTitle: 'OrderDetails' },
        loadChildren: () => import('./testChanges/order-details/order-details.module').then(m => m.TestChangesOrderDetailsModule),
      },
      {
        path: 'orders',
        data: { pageTitle: 'Orders' },
        loadChildren: () => import('./testChanges/orders/orders.module').then(m => m.TestChangesOrdersModule),
      },
      {
        path: 'payment-methods',
        data: { pageTitle: 'PaymentMethods' },
        loadChildren: () => import('./testChanges/payment-methods/payment-methods.module').then(m => m.TestChangesPaymentMethodsModule),
      },
      {
        path: 'product-details',
        data: { pageTitle: 'ProductDetails' },
        loadChildren: () => import('./testChanges/product-details/product-details.module').then(m => m.TestChangesProductDetailsModule),
      },
      {
        path: 'products',
        data: { pageTitle: 'Products' },
        loadChildren: () => import('./testChanges/products/products.module').then(m => m.TestChangesProductsModule),
      },
      {
        path: 'roles',
        data: { pageTitle: 'Roles' },
        loadChildren: () => import('./testChanges/roles/roles.module').then(m => m.TestChangesRolesModule),
      },
      {
        path: 'shipping-details',
        data: { pageTitle: 'ShippingDetails' },
        loadChildren: () => import('./testChanges/shipping-details/shipping-details.module').then(m => m.TestChangesShippingDetailsModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
