import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICars } from '../cars.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../cars.test-samples';

import { CarsService } from './cars.service';

const requireRestSample: ICars = {
  ...sampleWithRequiredData,
};

describe('Cars Service', () => {
  let service: CarsService;
  let httpMock: HttpTestingController;
  let expectedResult: ICars | ICars[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CarsService);
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

    it('should create a Cars', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const cars = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(cars).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Cars', () => {
      const cars = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(cars).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Cars', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Cars', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Cars', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCarsToCollectionIfMissing', () => {
      it('should add a Cars to an empty array', () => {
        const cars: ICars = sampleWithRequiredData;
        expectedResult = service.addCarsToCollectionIfMissing([], cars);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cars);
      });

      it('should not add a Cars to an array that contains it', () => {
        const cars: ICars = sampleWithRequiredData;
        const carsCollection: ICars[] = [
          {
            ...cars,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCarsToCollectionIfMissing(carsCollection, cars);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Cars to an array that doesn't contain it", () => {
        const cars: ICars = sampleWithRequiredData;
        const carsCollection: ICars[] = [sampleWithPartialData];
        expectedResult = service.addCarsToCollectionIfMissing(carsCollection, cars);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cars);
      });

      it('should add only unique Cars to an array', () => {
        const carsArray: ICars[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const carsCollection: ICars[] = [sampleWithRequiredData];
        expectedResult = service.addCarsToCollectionIfMissing(carsCollection, ...carsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const cars: ICars = sampleWithRequiredData;
        const cars2: ICars = sampleWithPartialData;
        expectedResult = service.addCarsToCollectionIfMissing([], cars, cars2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(cars);
        expect(expectedResult).toContain(cars2);
      });

      it('should accept null and undefined values', () => {
        const cars: ICars = sampleWithRequiredData;
        expectedResult = service.addCarsToCollectionIfMissing([], null, cars, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(cars);
      });

      it('should return initial array if no Cars is added', () => {
        const carsCollection: ICars[] = [sampleWithRequiredData];
        expectedResult = service.addCarsToCollectionIfMissing(carsCollection, undefined, null);
        expect(expectedResult).toEqual(carsCollection);
      });
    });

    describe('compareCars', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCars(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCars(entity1, entity2);
        const compareResult2 = service.compareCars(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCars(entity1, entity2);
        const compareResult2 = service.compareCars(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCars(entity1, entity2);
        const compareResult2 = service.compareCars(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
