import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { DarazUsersFormService } from './daraz-users-form.service';
import { DarazUsersService } from '../service/daraz-users.service';
import { IDarazUsers } from '../daraz-users.model';
import { IRoles } from 'app/entities/testChanges/roles/roles.model';
import { RolesService } from 'app/entities/testChanges/roles/service/roles.service';

import { DarazUsersUpdateComponent } from './daraz-users-update.component';

describe('DarazUsers Management Update Component', () => {
  let comp: DarazUsersUpdateComponent;
  let fixture: ComponentFixture<DarazUsersUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let darazUsersFormService: DarazUsersFormService;
  let darazUsersService: DarazUsersService;
  let rolesService: RolesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [DarazUsersUpdateComponent],
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
      .overrideTemplate(DarazUsersUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(DarazUsersUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    darazUsersFormService = TestBed.inject(DarazUsersFormService);
    darazUsersService = TestBed.inject(DarazUsersService);
    rolesService = TestBed.inject(RolesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call DarazUsers query and add missing value', () => {
      const darazUsers: IDarazUsers = { id: 456 };
      const manager: IDarazUsers = { id: 65701 };
      darazUsers.manager = manager;

      const darazUsersCollection: IDarazUsers[] = [{ id: 38862 }];
      jest.spyOn(darazUsersService, 'query').mockReturnValue(of(new HttpResponse({ body: darazUsersCollection })));
      const additionalDarazUsers = [manager];
      const expectedCollection: IDarazUsers[] = [...additionalDarazUsers, ...darazUsersCollection];
      jest.spyOn(darazUsersService, 'addDarazUsersToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ darazUsers });
      comp.ngOnInit();

      expect(darazUsersService.query).toHaveBeenCalled();
      expect(darazUsersService.addDarazUsersToCollectionIfMissing).toHaveBeenCalledWith(
        darazUsersCollection,
        ...additionalDarazUsers.map(expect.objectContaining)
      );
      expect(comp.darazUsersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Roles query and add missing value', () => {
      const darazUsers: IDarazUsers = { id: 456 };
      const roles: IRoles[] = [{ id: 62723 }];
      darazUsers.roles = roles;

      const rolesCollection: IRoles[] = [{ id: 4393 }];
      jest.spyOn(rolesService, 'query').mockReturnValue(of(new HttpResponse({ body: rolesCollection })));
      const additionalRoles = [...roles];
      const expectedCollection: IRoles[] = [...additionalRoles, ...rolesCollection];
      jest.spyOn(rolesService, 'addRolesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ darazUsers });
      comp.ngOnInit();

      expect(rolesService.query).toHaveBeenCalled();
      expect(rolesService.addRolesToCollectionIfMissing).toHaveBeenCalledWith(
        rolesCollection,
        ...additionalRoles.map(expect.objectContaining)
      );
      expect(comp.rolesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const darazUsers: IDarazUsers = { id: 456 };
      const manager: IDarazUsers = { id: 59461 };
      darazUsers.manager = manager;
      const role: IRoles = { id: 75021 };
      darazUsers.roles = [role];

      activatedRoute.data = of({ darazUsers });
      comp.ngOnInit();

      expect(comp.darazUsersSharedCollection).toContain(manager);
      expect(comp.rolesSharedCollection).toContain(role);
      expect(comp.darazUsers).toEqual(darazUsers);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDarazUsers>>();
      const darazUsers = { id: 123 };
      jest.spyOn(darazUsersFormService, 'getDarazUsers').mockReturnValue(darazUsers);
      jest.spyOn(darazUsersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ darazUsers });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: darazUsers }));
      saveSubject.complete();

      // THEN
      expect(darazUsersFormService.getDarazUsers).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(darazUsersService.update).toHaveBeenCalledWith(expect.objectContaining(darazUsers));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDarazUsers>>();
      const darazUsers = { id: 123 };
      jest.spyOn(darazUsersFormService, 'getDarazUsers').mockReturnValue({ id: null });
      jest.spyOn(darazUsersService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ darazUsers: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: darazUsers }));
      saveSubject.complete();

      // THEN
      expect(darazUsersFormService.getDarazUsers).toHaveBeenCalled();
      expect(darazUsersService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IDarazUsers>>();
      const darazUsers = { id: 123 };
      jest.spyOn(darazUsersService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ darazUsers });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(darazUsersService.update).toHaveBeenCalled();
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

    describe('compareRoles', () => {
      it('Should forward to rolesService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(rolesService, 'compareRoles');
        comp.compareRoles(entity, entity2);
        expect(rolesService.compareRoles).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
