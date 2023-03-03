import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { OrdersFormService } from './orders-form.service';
import { OrdersService } from '../service/orders.service';
import { IOrders } from '../orders.model';
import { ICustomers } from 'app/entities/testChanges/customers/customers.model';
import { CustomersService } from 'app/entities/testChanges/customers/service/customers.service';

import { OrdersUpdateComponent } from './orders-update.component';

describe('Orders Management Update Component', () => {
  let comp: OrdersUpdateComponent;
  let fixture: ComponentFixture<OrdersUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let ordersFormService: OrdersFormService;
  let ordersService: OrdersService;
  let customersService: CustomersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [OrdersUpdateComponent],
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
      .overrideTemplate(OrdersUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(OrdersUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ordersFormService = TestBed.inject(OrdersFormService);
    ordersService = TestBed.inject(OrdersService);
    customersService = TestBed.inject(CustomersService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Customers query and add missing value', () => {
      const orders: IOrders = { id: 456 };
      const customer: ICustomers = { id: 79933 };
      orders.customer = customer;

      const customersCollection: ICustomers[] = [{ id: 70003 }];
      jest.spyOn(customersService, 'query').mockReturnValue(of(new HttpResponse({ body: customersCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomers[] = [...additionalCustomers, ...customersCollection];
      jest.spyOn(customersService, 'addCustomersToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ orders });
      comp.ngOnInit();

      expect(customersService.query).toHaveBeenCalled();
      expect(customersService.addCustomersToCollectionIfMissing).toHaveBeenCalledWith(
        customersCollection,
        ...additionalCustomers.map(expect.objectContaining)
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const orders: IOrders = { id: 456 };
      const customer: ICustomers = { id: 65126 };
      orders.customer = customer;

      activatedRoute.data = of({ orders });
      comp.ngOnInit();

      expect(comp.customersSharedCollection).toContain(customer);
      expect(comp.orders).toEqual(orders);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrders>>();
      const orders = { id: 123 };
      jest.spyOn(ordersFormService, 'getOrders').mockReturnValue(orders);
      jest.spyOn(ordersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orders });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orders }));
      saveSubject.complete();

      // THEN
      expect(ordersFormService.getOrders).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ordersService.update).toHaveBeenCalledWith(expect.objectContaining(orders));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrders>>();
      const orders = { id: 123 };
      jest.spyOn(ordersFormService, 'getOrders').mockReturnValue({ id: null });
      jest.spyOn(ordersService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orders: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: orders }));
      saveSubject.complete();

      // THEN
      expect(ordersFormService.getOrders).toHaveBeenCalled();
      expect(ordersService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IOrders>>();
      const orders = { id: 123 };
      jest.spyOn(ordersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ orders });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ordersService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCustomers', () => {
      it('Should forward to customersService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(customersService, 'compareCustomers');
        comp.compareCustomers(entity, entity2);
        expect(customersService.compareCustomers).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
