import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PaymentMethodsFormService } from './payment-methods-form.service';
import { PaymentMethodsService } from '../service/payment-methods.service';
import { IPaymentMethods } from '../payment-methods.model';
import { ICustomers } from 'app/entities/testChanges/customers/customers.model';
import { CustomersService } from 'app/entities/testChanges/customers/service/customers.service';

import { PaymentMethodsUpdateComponent } from './payment-methods-update.component';

describe('PaymentMethods Management Update Component', () => {
  let comp: PaymentMethodsUpdateComponent;
  let fixture: ComponentFixture<PaymentMethodsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let paymentMethodsFormService: PaymentMethodsFormService;
  let paymentMethodsService: PaymentMethodsService;
  let customersService: CustomersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PaymentMethodsUpdateComponent],
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
      .overrideTemplate(PaymentMethodsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PaymentMethodsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    paymentMethodsFormService = TestBed.inject(PaymentMethodsFormService);
    paymentMethodsService = TestBed.inject(PaymentMethodsService);
    customersService = TestBed.inject(CustomersService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Customers query and add missing value', () => {
      const paymentMethods: IPaymentMethods = { id: 456 };
      const customer: ICustomers = { id: 12411 };
      paymentMethods.customer = customer;

      const customersCollection: ICustomers[] = [{ id: 64563 }];
      jest.spyOn(customersService, 'query').mockReturnValue(of(new HttpResponse({ body: customersCollection })));
      const additionalCustomers = [customer];
      const expectedCollection: ICustomers[] = [...additionalCustomers, ...customersCollection];
      jest.spyOn(customersService, 'addCustomersToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ paymentMethods });
      comp.ngOnInit();

      expect(customersService.query).toHaveBeenCalled();
      expect(customersService.addCustomersToCollectionIfMissing).toHaveBeenCalledWith(
        customersCollection,
        ...additionalCustomers.map(expect.objectContaining)
      );
      expect(comp.customersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const paymentMethods: IPaymentMethods = { id: 456 };
      const customer: ICustomers = { id: 87386 };
      paymentMethods.customer = customer;

      activatedRoute.data = of({ paymentMethods });
      comp.ngOnInit();

      expect(comp.customersSharedCollection).toContain(customer);
      expect(comp.paymentMethods).toEqual(paymentMethods);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPaymentMethods>>();
      const paymentMethods = { id: 123 };
      jest.spyOn(paymentMethodsFormService, 'getPaymentMethods').mockReturnValue(paymentMethods);
      jest.spyOn(paymentMethodsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paymentMethods });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: paymentMethods }));
      saveSubject.complete();

      // THEN
      expect(paymentMethodsFormService.getPaymentMethods).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(paymentMethodsService.update).toHaveBeenCalledWith(expect.objectContaining(paymentMethods));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPaymentMethods>>();
      const paymentMethods = { id: 123 };
      jest.spyOn(paymentMethodsFormService, 'getPaymentMethods').mockReturnValue({ id: null });
      jest.spyOn(paymentMethodsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paymentMethods: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: paymentMethods }));
      saveSubject.complete();

      // THEN
      expect(paymentMethodsFormService.getPaymentMethods).toHaveBeenCalled();
      expect(paymentMethodsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPaymentMethods>>();
      const paymentMethods = { id: 123 };
      jest.spyOn(paymentMethodsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paymentMethods });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(paymentMethodsService.update).toHaveBeenCalled();
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
