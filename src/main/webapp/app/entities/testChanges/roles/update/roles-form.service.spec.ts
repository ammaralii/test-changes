import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../roles.test-samples';

import { RolesFormService } from './roles-form.service';

describe('Roles Form Service', () => {
  let service: RolesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RolesFormService);
  });

  describe('Service methods', () => {
    describe('createRolesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createRolesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            rolePrId: expect.any(Object),
            name: expect.any(Object),
            users: expect.any(Object),
          })
        );
      });

      it('passing IRoles should create a new form with FormGroup', () => {
        const formGroup = service.createRolesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            rolePrId: expect.any(Object),
            name: expect.any(Object),
            users: expect.any(Object),
          })
        );
      });
    });

    describe('getRoles', () => {
      it('should return NewRoles for default Roles initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createRolesFormGroup(sampleWithNewData);

        const roles = service.getRoles(formGroup) as any;

        expect(roles).toMatchObject(sampleWithNewData);
      });

      it('should return NewRoles for empty Roles initial value', () => {
        const formGroup = service.createRolesFormGroup();

        const roles = service.getRoles(formGroup) as any;

        expect(roles).toMatchObject({});
      });

      it('should return IRoles', () => {
        const formGroup = service.createRolesFormGroup(sampleWithRequiredData);

        const roles = service.getRoles(formGroup) as any;

        expect(roles).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IRoles should not enable id FormControl', () => {
        const formGroup = service.createRolesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewRoles should disable id FormControl', () => {
        const formGroup = service.createRolesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
