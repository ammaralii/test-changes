import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../daraz-users.test-samples';

import { DarazUsersFormService } from './daraz-users-form.service';

describe('DarazUsers Form Service', () => {
  let service: DarazUsersFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DarazUsersFormService);
  });

  describe('Service methods', () => {
    describe('createDarazUsersFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createDarazUsersFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fullName: expect.any(Object),
            email: expect.any(Object),
            phone: expect.any(Object),
            manager: expect.any(Object),
            roles: expect.any(Object),
          })
        );
      });

      it('passing IDarazUsers should create a new form with FormGroup', () => {
        const formGroup = service.createDarazUsersFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            fullName: expect.any(Object),
            email: expect.any(Object),
            phone: expect.any(Object),
            manager: expect.any(Object),
            roles: expect.any(Object),
          })
        );
      });
    });

    describe('getDarazUsers', () => {
      it('should return NewDarazUsers for default DarazUsers initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createDarazUsersFormGroup(sampleWithNewData);

        const darazUsers = service.getDarazUsers(formGroup) as any;

        expect(darazUsers).toMatchObject(sampleWithNewData);
      });

      it('should return NewDarazUsers for empty DarazUsers initial value', () => {
        const formGroup = service.createDarazUsersFormGroup();

        const darazUsers = service.getDarazUsers(formGroup) as any;

        expect(darazUsers).toMatchObject({});
      });

      it('should return IDarazUsers', () => {
        const formGroup = service.createDarazUsersFormGroup(sampleWithRequiredData);

        const darazUsers = service.getDarazUsers(formGroup) as any;

        expect(darazUsers).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IDarazUsers should not enable id FormControl', () => {
        const formGroup = service.createDarazUsersFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewDarazUsers should disable id FormControl', () => {
        const formGroup = service.createDarazUsersFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
