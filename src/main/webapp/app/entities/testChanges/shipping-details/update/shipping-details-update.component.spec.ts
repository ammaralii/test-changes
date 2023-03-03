import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ShippingDetailsFormService } from './shipping-details-form.service';
import { ShippingDetailsService } from '../service/shipping-details.service';
import { IShippingDetails } from '../shipping-details.model';
import { IOrders } from 'app/entities/testChanges/orders/orders.model';
import { OrdersService } from 'app/entities/testChanges/orders/service/orders.service';

import { ShippingDetailsUpdateComponent } from './shipping-details-update.component';

describe('ShippingDetails Management Update Component', () => {
  let comp: ShippingDetailsUpdateComponent;
  let fixture: ComponentFixture<ShippingDetailsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let shippingDetailsFormService: ShippingDetailsFormService;
  let shippingDetailsService: ShippingDetailsService;
  let ordersService: OrdersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ShippingDetailsUpdateComponent],
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
      .overrideTemplate(ShippingDetailsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ShippingDetailsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    shippingDetailsFormService = TestBed.inject(ShippingDetailsFormService);
    shippingDetailsService = TestBed.inject(ShippingDetailsService);
    ordersService = TestBed.inject(OrdersService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Orders query and add missing value', () => {
      const shippingDetails: IShippingDetails = { id: 456 };
      const order: IOrders = { id: 75230 };
      shippingDetails.order = order;

      const ordersCollection: IOrders[] = [{ id: 24394 }];
      jest.spyOn(ordersService, 'query').mockReturnValue(of(new HttpResponse({ body: ordersCollection })));
      const additionalOrders = [order];
      const expectedCollection: IOrders[] = [...additionalOrders, ...ordersCollection];
      jest.spyOn(ordersService, 'addOrdersToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ shippingDetails });
      comp.ngOnInit();

      expect(ordersService.query).toHaveBeenCalled();
      expect(ordersService.addOrdersToCollectionIfMissing).toHaveBeenCalledWith(
        ordersCollection,
        ...additionalOrders.map(expect.objectContaining)
      );
      expect(comp.ordersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const shippingDetails: IShippingDetails = { id: 456 };
      const order: IOrders = { id: 70702 };
      shippingDetails.order = order;

      activatedRoute.data = of({ shippingDetails });
      comp.ngOnInit();

      expect(comp.ordersSharedCollection).toContain(order);
      expect(comp.shippingDetails).toEqual(shippingDetails);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IShippingDetails>>();
      const shippingDetails = { id: 123 };
      jest.spyOn(shippingDetailsFormService, 'getShippingDetails').mockReturnValue(shippingDetails);
      jest.spyOn(shippingDetailsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shippingDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: shippingDetails }));
      saveSubject.complete();

      // THEN
      expect(shippingDetailsFormService.getShippingDetails).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(shippingDetailsService.update).toHaveBeenCalledWith(expect.objectContaining(shippingDetails));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IShippingDetails>>();
      const shippingDetails = { id: 123 };
      jest.spyOn(shippingDetailsFormService, 'getShippingDetails').mockReturnValue({ id: null });
      jest.spyOn(shippingDetailsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shippingDetails: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: shippingDetails }));
      saveSubject.complete();

      // THEN
      expect(shippingDetailsFormService.getShippingDetails).toHaveBeenCalled();
      expect(shippingDetailsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IShippingDetails>>();
      const shippingDetails = { id: 123 };
      jest.spyOn(shippingDetailsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ shippingDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(shippingDetailsService.update).toHaveBeenCalled();
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
  });
});
