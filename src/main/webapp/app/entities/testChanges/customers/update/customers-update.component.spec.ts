import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CustomersFormService } from './customers-form.service';
import { CustomersService } from '../service/customers.service';
import { ICustomers } from '../customers.model';

import { CustomersUpdateComponent } from './customers-update.component';

describe('Customers Management Update Component', () => {
  let comp: CustomersUpdateComponent;
  let fixture: ComponentFixture<CustomersUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let customersFormService: CustomersFormService;
  let customersService: CustomersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CustomersUpdateComponent],
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
      .overrideTemplate(CustomersUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CustomersUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    customersFormService = TestBed.inject(CustomersFormService);
    customersService = TestBed.inject(CustomersService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const customers: ICustomers = { id: 456 };

      activatedRoute.data = of({ customers });
      comp.ngOnInit();

      expect(comp.customers).toEqual(customers);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomers>>();
      const customers = { id: 123 };
      jest.spyOn(customersFormService, 'getCustomers').mockReturnValue(customers);
      jest.spyOn(customersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customers });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customers }));
      saveSubject.complete();

      // THEN
      expect(customersFormService.getCustomers).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(customersService.update).toHaveBeenCalledWith(expect.objectContaining(customers));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomers>>();
      const customers = { id: 123 };
      jest.spyOn(customersFormService, 'getCustomers').mockReturnValue({ id: null });
      jest.spyOn(customersService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customers: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: customers }));
      saveSubject.complete();

      // THEN
      expect(customersFormService.getCustomers).toHaveBeenCalled();
      expect(customersService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICustomers>>();
      const customers = { id: 123 };
      jest.spyOn(customersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ customers });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(customersService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
