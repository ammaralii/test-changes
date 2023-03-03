import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDarazUsers } from '../daraz-users.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../daraz-users.test-samples';

import { DarazUsersService } from './daraz-users.service';

const requireRestSample: IDarazUsers = {
  ...sampleWithRequiredData,
};

describe('DarazUsers Service', () => {
  let service: DarazUsersService;
  let httpMock: HttpTestingController;
  let expectedResult: IDarazUsers | IDarazUsers[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DarazUsersService);
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

    it('should create a DarazUsers', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const darazUsers = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(darazUsers).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a DarazUsers', () => {
      const darazUsers = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(darazUsers).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a DarazUsers', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of DarazUsers', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a DarazUsers', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addDarazUsersToCollectionIfMissing', () => {
      it('should add a DarazUsers to an empty array', () => {
        const darazUsers: IDarazUsers = sampleWithRequiredData;
        expectedResult = service.addDarazUsersToCollectionIfMissing([], darazUsers);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(darazUsers);
      });

      it('should not add a DarazUsers to an array that contains it', () => {
        const darazUsers: IDarazUsers = sampleWithRequiredData;
        const darazUsersCollection: IDarazUsers[] = [
          {
            ...darazUsers,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addDarazUsersToCollectionIfMissing(darazUsersCollection, darazUsers);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a DarazUsers to an array that doesn't contain it", () => {
        const darazUsers: IDarazUsers = sampleWithRequiredData;
        const darazUsersCollection: IDarazUsers[] = [sampleWithPartialData];
        expectedResult = service.addDarazUsersToCollectionIfMissing(darazUsersCollection, darazUsers);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(darazUsers);
      });

      it('should add only unique DarazUsers to an array', () => {
        const darazUsersArray: IDarazUsers[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const darazUsersCollection: IDarazUsers[] = [sampleWithRequiredData];
        expectedResult = service.addDarazUsersToCollectionIfMissing(darazUsersCollection, ...darazUsersArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const darazUsers: IDarazUsers = sampleWithRequiredData;
        const darazUsers2: IDarazUsers = sampleWithPartialData;
        expectedResult = service.addDarazUsersToCollectionIfMissing([], darazUsers, darazUsers2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(darazUsers);
        expect(expectedResult).toContain(darazUsers2);
      });

      it('should accept null and undefined values', () => {
        const darazUsers: IDarazUsers = sampleWithRequiredData;
        expectedResult = service.addDarazUsersToCollectionIfMissing([], null, darazUsers, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(darazUsers);
      });

      it('should return initial array if no DarazUsers is added', () => {
        const darazUsersCollection: IDarazUsers[] = [sampleWithRequiredData];
        expectedResult = service.addDarazUsersToCollectionIfMissing(darazUsersCollection, undefined, null);
        expect(expectedResult).toEqual(darazUsersCollection);
      });
    });

    describe('compareDarazUsers', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareDarazUsers(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareDarazUsers(entity1, entity2);
        const compareResult2 = service.compareDarazUsers(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareDarazUsers(entity1, entity2);
        const compareResult2 = service.compareDarazUsers(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareDarazUsers(entity1, entity2);
        const compareResult2 = service.compareDarazUsers(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
