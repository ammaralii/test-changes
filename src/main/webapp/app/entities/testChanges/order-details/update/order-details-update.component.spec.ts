import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrderDetailsFormService } from './order-details-form.service';
import { OrderDetailsService } from '../service/order-details.service';
import { IOrderDetails } from '../order-details.model';
import { IOrders } from 'app/entities/testChanges/orders/orders.model';
import { OrdersService } from 'app/entities/testChanges/orders/service/orders.service';
import { IProducts } from 'app/entities/testChanges/products/products.model';
import { ProductsService } from 'app/entities/testChanges/products/service/products.service';

import { OrderDetailsUpdateComponent } from './order-details-update.component';

describe('OrderDetails Management Update Component', () => {
  let comp: OrderDetailsUpdateComponent;
  let fixture: ComponentFixture<OrderDetailsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderDetailsFormService: OrderDetailsFormService;
  let orderDetailsService: OrderDetailsService;
  let ordersService: OrdersService;
  let productsService: ProductsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrderDetailsUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(OrderDetailsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderDetailsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderDetailsFormService = TestBed.inject(OrderDetailsFormService);
    orderDetailsService = TestBed.inject(OrderDetailsService);
    ordersService = TestBed.inject(OrdersService);
    productsService = TestBed.inject(ProductsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Orders query and add missing value', () => {
      const orderDetails: IOrderDetails = { id: 456 };
      const order: IOrders = { id: 4379 };
      orderDetails.order = order;

      const ordersCollection: IOrders[] = [{ id: 81839 }];
      jest.spyOn(ordersService, 'query').mockReturnValue(of(new HttpResponse({ body: ordersCollection })));
      const additionalOrders = [order];
      const expectedCollection: IOrders[] = [...additionalOrders, ...ordersCollection];
      jest.spyOn(ordersService, 'addOrdersToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderDetails });
      comp.ngOnInit();

      expect(ordersService.query).toHaveBeenCalled();
      expect(ordersService.addOrdersToCollectionIfMissing).toHaveBeenCalledWith(
        ordersCollection,
        ...additionalOrders.map(expect.objectContaining)
      );
      expect(comp.ordersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Products query and add missing value', () => {
      const orderDetails: IOrderDetails = { id: 456 };
      const product: IProducts = { id: 77120 };
      orderDetails.product = product;

      const productsCollection: IProducts[] = [{ id: 95700 }];
      jest.spyOn(productsService, 'query').mockReturnValue(of(new HttpResponse({ body: productsCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProducts[] = [...additionalProducts, ...productsCollection];
      jest.spyOn(productsService, 'addProductsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderDetails });
      comp.ngOnInit();

      expect(productsService.query).toHaveBeenCalled();
      expect(productsService.addProductsToCollectionIfMissing).toHaveBeenCalledWith(
        productsCollection,
        ...additionalProducts.map(expect.objectContaining)
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const orderDetails: IOrderDetails = { id: 456 };
      const order: IOrders = { id: 39649 };
      orderDetails.order = order;
      const product: IProducts = { id: 27111 };
      orderDetails.product = product;

      activatedRoute.data = of({ orderDetails });
      comp.ngOnInit();

      expect(comp.ordersSharedCollection).toContain(order);
      expect(comp.productsSharedCollection).toContain(product);
      expect(comp.orderDetails).toEqual(orderDetails);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderDetails>>();
      const orderDetails = { id: 123 };
      jest.spyOn(orderDetailsFormService, 'getOrderDetails').mockReturnValue(orderDetails);
      jest.spyOn(orderDetailsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderDetails }));
      saveSubject.complete();

      // THEN
      expect(orderDetailsFormService.getOrderDetails).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderDetailsService.update).toHaveBeenCalledWith(expect.objectContaining(orderDetails));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderDetails>>();
      const orderDetails = { id: 123 };
      jest.spyOn(orderDetailsFormService, 'getOrderDetails').mockReturnValue({ id: null });
      jest.spyOn(orderDetailsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderDetails: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderDetails }));
      saveSubject.complete();

      // THEN
      expect(orderDetailsFormService.getOrderDetails).toHaveBeenCalled();
      expect(orderDetailsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderDetails>>();
      const orderDetails = { id: 123 };
      jest.spyOn(orderDetailsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderDetailsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareOrders', () => {
      it('Should forward to ordersService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(ordersService, 'compareOrders');
        comp.compareOrders(entity, entity2);
        expect(ordersService.compareOrders).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareProducts', () => {
      it('Should forward to productsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(productsService, 'compareProducts');
        comp.compareProducts(entity, entity2);
        expect(productsService.compareProducts).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
