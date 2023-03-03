import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IRoles } from '../roles.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../roles.test-samples';

import { RolesService } from './roles.service';

const requireRestSample: IRoles = {
  ...sampleWithRequiredData,
};

describe('Roles Service', () => {
  let service: RolesService;
  let httpMock: HttpTestingController;
  let expectedResult: IRoles | IRoles[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(RolesService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Roles', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const roles = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(roles).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Roles', () => {
      const roles = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(roles).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Roles', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Roles', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Roles', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addRolesToCollectionIfMissing', () => {
      it('should add a Roles to an empty array', () => {
        const roles: IRoles = sampleWithRequiredData;
        expectedResult = service.addRolesToCollectionIfMissing([], roles);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(roles);
      });

      it('should not add a Roles to an array that contains it', () => {
        const roles: IRoles = sampleWithRequiredData;
        const rolesCollection: IRoles[] = [
          {
            ...roles,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addRolesToCollectionIfMissing(rolesCollection, roles);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Roles to an array that doesn't contain it", () => {
        const roles: IRoles = sampleWithRequiredData;
        const rolesCollection: IRoles[] = [sampleWithPartialData];
        expectedResult = service.addRolesToCollectionIfMissing(rolesCollection, roles);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(roles);
      });

      it('should add only unique Roles to an array', () => {
        const rolesArray: IRoles[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const rolesCollection: IRoles[] = [sampleWithRequiredData];
        expectedResult = service.addRolesToCollectionIfMissing(rolesCollection, ...rolesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const roles: IRoles = sampleWithRequiredData;
        const roles2: IRoles = sampleWithPartialData;
        expectedResult = service.addRolesToCollectionIfMissing([], roles, roles2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(roles);
        expect(expectedResult).toContain(roles2);
      });

      it('should accept null and undefined values', () => {
        const roles: IRoles = sampleWithRequiredData;
        expectedResult = service.addRolesToCollectionIfMissing([], null, roles, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(roles);
      });

      it('should return initial array if no Roles is added', () => {
        const rolesCollection: IRoles[] = [sampleWithRequiredData];
        expectedResult = service.addRolesToCollectionIfMissing(rolesCollection, undefined, null);
        expect(expectedResult).toEqual(rolesCollection);
      });
    });

    describe('compareRoles', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareRoles(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareRoles(entity1, entity2);
        const compareResult2 = service.compareRoles(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareRoles(entity1, entity2);
        const compareResult2 = service.compareRoles(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareRoles(entity1, entity2);
        const compareResult2 = service.compareRoles(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
