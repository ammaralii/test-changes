import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AddressesFormService } from './addresses-form.service';
import { AddressesService } from '../service/addresses.service';
import { IAddresses } from '../addresses.model';
import { IDarazUsers } from 'app/entities/testChanges/daraz-users/daraz-users.model';
import { DarazUsersService } from 'app/entities/testChanges/daraz-users/service/daraz-users.service';

import { AddressesUpdateComponent } from './addresses-update.component';

describe('Addresses Management Update Component', () => {
  let comp: AddressesUpdateComponent;
  let fixture: ComponentFixture<AddressesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let addressesFormService: AddressesFormService;
  let addressesService: AddressesService;
  let darazUsersService: DarazUsersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AddressesUpdateComponent],
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
      .overrideTemplate(AddressesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AddressesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    addressesFormService = TestBed.inject(AddressesFormService);
    addressesService = TestBed.inject(AddressesService);
    darazUsersService = TestBed.inject(DarazUsersService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call DarazUsers query and add missing value', () => {
      const addresses: IAddresses = { id: 456 };
      const user: IDarazUsers = { id: 48008 };
      addresses.user = user;

      const darazUsersCollection: IDarazUsers[] = [{ id: 81913 }];
      jest.spyOn(darazUsersService, 'query').mockReturnValue(of(new HttpResponse({ body: darazUsersCollection })));
      const additionalDarazUsers = [user];
      const expectedCollection: IDarazUsers[] = [...additionalDarazUsers, ...darazUsersCollection];
      jest.spyOn(darazUsersService, 'addDarazUsersToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ addresses });
      comp.ngOnInit();

      expect(darazUsersService.query).toHaveBeenCalled();
      expect(darazUsersService.addDarazUsersToCollectionIfMissing).toHaveBeenCalledWith(
        darazUsersCollection,
        ...additionalDarazUsers.map(expect.objectContaining)
      );
      expect(comp.darazUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const addresses: IAddresses = { id: 456 };
      const user: IDarazUsers = { id: 13777 };
      addresses.user = user;

      activatedRoute.data = of({ addresses });
      comp.ngOnInit();

      expect(comp.darazUsersSharedCollection).toContain(user);
      expect(comp.addresses).toEqual(addresses);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAddresses>>();
      const addresses = { id: 123 };
      jest.spyOn(addressesFormService, 'getAddresses').mockReturnValue(addresses);
      jest.spyOn(addressesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ addresses });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: addresses }));
      saveSubject.complete();

      // THEN
      expect(addressesFormService.getAddresses).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(addressesService.update).toHaveBeenCalledWith(expect.objectContaining(addresses));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAddresses>>();
      const addresses = { id: 123 };
      jest.spyOn(addressesFormService, 'getAddresses').mockReturnValue({ id: null });
      jest.spyOn(addressesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ addresses: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: addresses }));
      saveSubject.complete();

      // THEN
      expect(addressesFormService.getAddresses).toHaveBeenCalled();
      expect(addressesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAddresses>>();
      const addresses = { id: 123 };
      jest.spyOn(addressesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ addresses });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(addressesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
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
