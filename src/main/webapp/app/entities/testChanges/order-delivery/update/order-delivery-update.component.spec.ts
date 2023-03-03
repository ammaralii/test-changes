import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrderDeliveryFormService } from './order-delivery-form.service';
import { OrderDeliveryService } from '../service/order-delivery.service';
import { IOrderDelivery } from '../order-delivery.model';
import { IOrders } from 'app/entities/testChanges/orders/orders.model';
import { OrdersService } from 'app/entities/testChanges/orders/service/orders.service';
import { IShippingDetails } from 'app/entities/testChanges/shipping-details/shipping-details.model';
import { ShippingDetailsService } from 'app/entities/testChanges/shipping-details/service/shipping-details.service';
import { IDarazUsers } from 'app/entities/testChanges/daraz-users/daraz-users.model';
import { DarazUsersService } from 'app/entities/testChanges/daraz-users/service/daraz-users.service';

import { OrderDeliveryUpdateComponent } from './order-delivery-update.component';

describe('OrderDelivery Management Update Component', () => {
  let comp: OrderDeliveryUpdateComponent;
  let fixture: ComponentFixture<OrderDeliveryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let orderDeliveryFormService: OrderDeliveryFormService;
  let orderDeliveryService: OrderDeliveryService;
  let ordersService: OrdersService;
  let shippingDetailsService: ShippingDetailsService;
  let darazUsersService: DarazUsersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrderDeliveryUpdateComponent],
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
      .overrideTemplate(OrderDeliveryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrderDeliveryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    orderDeliveryFormService = TestBed.inject(OrderDeliveryFormService);
    orderDeliveryService = TestBed.inject(OrderDeliveryService);
    ordersService = TestBed.inject(OrdersService);
    shippingDetailsService = TestBed.inject(ShippingDetailsService);
    darazUsersService = TestBed.inject(DarazUsersService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Orders query and add missing value', () => {
      const orderDelivery: IOrderDelivery = { id: 456 };
      const order: IOrders = { id: 79804 };
      orderDelivery.order = order;

      const ordersCollection: IOrders[] = [{ id: 14217 }];
      jest.spyOn(ordersService, 'query').mockReturnValue(of(new HttpResponse({ body: ordersCollection })));
      const additionalOrders = [order];
      const expectedCollection: IOrders[] = [...additionalOrders, ...ordersCollection];
      jest.spyOn(ordersService, 'addOrdersToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderDelivery });
      comp.ngOnInit();

      expect(ordersService.query).toHaveBeenCalled();
      expect(ordersService.addOrdersToCollectionIfMissing).toHaveBeenCalledWith(
        ordersCollection,
        ...additionalOrders.map(expect.objectContaining)
      );
      expect(comp.ordersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ShippingDetails query and add missing value', () => {
      const orderDelivery: IOrderDelivery = { id: 456 };
      const shippingAddress: IShippingDetails = { id: 17221 };
      orderDelivery.shippingAddress = shippingAddress;

      const shippingDetailsCollection: IShippingDetails[] = [{ id: 47014 }];
      jest.spyOn(shippingDetailsService, 'query').mockReturnValue(of(new HttpResponse({ body: shippingDetailsCollection })));
      const additionalShippingDetails = [shippingAddress];
      const expectedCollection: IShippingDetails[] = [...additionalShippingDetails, ...shippingDetailsCollection];
      jest.spyOn(shippingDetailsService, 'addShippingDetailsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderDelivery });
      comp.ngOnInit();

      expect(shippingDetailsService.query).toHaveBeenCalled();
      expect(shippingDetailsService.addShippingDetailsToCollectionIfMissing).toHaveBeenCalledWith(
        shippingDetailsCollection,
        ...additionalShippingDetails.map(expect.objectContaining)
      );
      expect(comp.shippingDetailsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call DarazUsers query and add missing value', () => {
      const orderDelivery: IOrderDelivery = { id: 456 };
      const deliveryManager: IDarazUsers = { id: 25318 };
      orderDelivery.deliveryManager = deliveryManager;
      const deliveryBoy: IDarazUsers = { id: 76914 };
      orderDelivery.deliveryBoy = deliveryBoy;

      const darazUsersCollection: IDarazUsers[] = [{ id: 46702 }];
      jest.spyOn(darazUsersService, 'query').mockReturnValue(of(new HttpResponse({ body: darazUsersCollection })));
      const additionalDarazUsers = [deliveryManager, deliveryBoy];
      const expectedCollection: IDarazUsers[] = [...additionalDarazUsers, ...darazUsersCollection];
      jest.spyOn(darazUsersService, 'addDarazUsersToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orderDelivery });
      comp.ngOnInit();

      expect(darazUsersService.query).toHaveBeenCalled();
      expect(darazUsersService.addDarazUsersToCollectionIfMissing).toHaveBeenCalledWith(
        darazUsersCollection,
        ...additionalDarazUsers.map(expect.objectContaining)
      );
      expect(comp.darazUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const orderDelivery: IOrderDelivery = { id: 456 };
      const order: IOrders = { id: 2905 };
      orderDelivery.order = order;
      const shippingAddress: IShippingDetails = { id: 33472 };
      orderDelivery.shippingAddress = shippingAddress;
      const deliveryManager: IDarazUsers = { id: 52765 };
      orderDelivery.deliveryManager = deliveryManager;
      const deliveryBoy: IDarazUsers = { id: 89145 };
      orderDelivery.deliveryBoy = deliveryBoy;

      activatedRoute.data = of({ orderDelivery });
      comp.ngOnInit();

      expect(comp.ordersSharedCollection).toContain(order);
      expect(comp.shippingDetailsSharedCollection).toContain(shippingAddress);
      expect(comp.darazUsersSharedCollection).toContain(deliveryManager);
      expect(comp.darazUsersSharedCollection).toContain(deliveryBoy);
      expect(comp.orderDelivery).toEqual(orderDelivery);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderDelivery>>();
      const orderDelivery = { id: 123 };
      jest.spyOn(orderDeliveryFormService, 'getOrderDelivery').mockReturnValue(orderDelivery);
      jest.spyOn(orderDeliveryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderDelivery });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderDelivery }));
      saveSubject.complete();

      // THEN
      expect(orderDeliveryFormService.getOrderDelivery).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(orderDeliveryService.update).toHaveBeenCalledWith(expect.objectContaining(orderDelivery));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderDelivery>>();
      const orderDelivery = { id: 123 };
      jest.spyOn(orderDeliveryFormService, 'getOrderDelivery').mockReturnValue({ id: null });
      jest.spyOn(orderDeliveryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderDelivery: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orderDelivery }));
      saveSubject.complete();

      // THEN
      expect(orderDeliveryFormService.getOrderDelivery).toHaveBeenCalled();
      expect(orderDeliveryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrderDelivery>>();
      const orderDelivery = { id: 123 };
      jest.spyOn(orderDeliveryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orderDelivery });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(orderDeliveryService.update).toHaveBeenCalled();
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

    describe('compareShippingDetails', () => {
      it('Should forward to shippingDetailsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(shippingDetailsService, 'compareShippingDetails');
        comp.compareShippingDetails(entity, entity2);
        expect(shippingDetailsService.compareShippingDetails).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareDarazUsers', () => {
      it('Should forward to darazUsersService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(darazUsersService, 'compareDarazUsers');
        comp.compareDarazUsers(entity, entity2);
        expect(darazUsersService.compareDarazUsers).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
