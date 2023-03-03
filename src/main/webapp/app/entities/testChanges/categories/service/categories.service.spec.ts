import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ICategories } from '../categories.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../categories.test-samples';

import { CategoriesService } from './categories.service';

const requireRestSample: ICategories = {
  ...sampleWithRequiredData,
};

describe('Categories Service', () => {
  let service: CategoriesService;
  let httpMock: HttpTestingController;
  let expectedResult: ICategories | ICategories[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(CategoriesService);
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

    it('should create a Categories', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const categories = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(categories).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Categories', () => {
      const categories = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(categories).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Categories', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Categories', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Categories', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addCategoriesToCollectionIfMissing', () => {
      it('should add a Categories to an empty array', () => {
        const categories: ICategories = sampleWithRequiredData;
        expectedResult = service.addCategoriesToCollectionIfMissing([], categories);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(categories);
      });

      it('should not add a Categories to an array that contains it', () => {
        const categories: ICategories = sampleWithRequiredData;
        const categoriesCollection: ICategories[] = [
          {
            ...categories,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCategoriesToCollectionIfMissing(categoriesCollection, categories);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Categories to an array that doesn't contain it", () => {
        const categories: ICategories = sampleWithRequiredData;
        const categoriesCollection: ICategories[] = [sampleWithPartialData];
        expectedResult = service.addCategoriesToCollectionIfMissing(categoriesCollection, categories);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(categories);
      });

      it('should add only unique Categories to an array', () => {
        const categoriesArray: ICategories[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const categoriesCollection: ICategories[] = [sampleWithRequiredData];
        expectedResult = service.addCategoriesToCollectionIfMissing(categoriesCollection, ...categoriesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const categories: ICategories = sampleWithRequiredData;
        const categories2: ICategories = sampleWithPartialData;
        expectedResult = service.addCategoriesToCollectionIfMissing([], categories, categories2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(categories);
        expect(expectedResult).toContain(categories2);
      });

      it('should accept null and undefined values', () => {
        const categories: ICategories = sampleWithRequiredData;
        expectedResult = service.addCategoriesToCollectionIfMissing([], null, categories, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(categories);
      });

      it('should return initial array if no Categories is added', () => {
        const categoriesCollection: ICategories[] = [sampleWithRequiredData];
        expectedResult = service.addCategoriesToCollectionIfMissing(categoriesCollection, undefined, null);
        expect(expectedResult).toEqual(categoriesCollection);
      });
    });

    describe('compareCategories', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCategories(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareCategories(entity1, entity2);
        const compareResult2 = service.compareCategories(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareCategories(entity1, entity2);
        const compareResult2 = service.compareCategories(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareCategories(entity1, entity2);
        const compareResult2 = service.compareCategories(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
