import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IColors } from '../colors.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../colors.test-samples';

import { ColorsService } from './colors.service';

const requireRestSample: IColors = {
  ...sampleWithRequiredData,
};

describe('Colors Service', () => {
  let service: ColorsService;
  let httpMock: HttpTestingController;
  let expectedResult: IColors | IColors[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ColorsService);
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

    it('should create a Colors', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const colors = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(colors).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Colors', () => {
      const colors = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(colors).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Colors', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Colors', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Colors', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addColorsToCollectionIfMissing', () => {
      it('should add a Colors to an empty array', () => {
        const colors: IColors = sampleWithRequiredData;
        expectedResult = service.addColorsToCollectionIfMissing([], colors);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(colors);
      });

      it('should not add a Colors to an array that contains it', () => {
        const colors: IColors = sampleWithRequiredData;
        const colorsCollection: IColors[] = [
          {
            ...colors,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addColorsToCollectionIfMissing(colorsCollection, colors);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Colors to an array that doesn't contain it", () => {
        const colors: IColors = sampleWithRequiredData;
        const colorsCollection: IColors[] = [sampleWithPartialData];
        expectedResult = service.addColorsToCollectionIfMissing(colorsCollection, colors);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(colors);
      });

      it('should add only unique Colors to an array', () => {
        const colorsArray: IColors[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const colorsCollection: IColors[] = [sampleWithRequiredData];
        expectedResult = service.addColorsToCollectionIfMissing(colorsCollection, ...colorsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const colors: IColors = sampleWithRequiredData;
        const colors2: IColors = sampleWithPartialData;
        expectedResult = service.addColorsToCollectionIfMissing([], colors, colors2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(colors);
        expect(expectedResult).toContain(colors2);
      });

      it('should accept null and undefined values', () => {
        const colors: IColors = sampleWithRequiredData;
        expectedResult = service.addColorsToCollectionIfMissing([], null, colors, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(colors);
      });

      it('should return initial array if no Colors is added', () => {
        const colorsCollection: IColors[] = [sampleWithRequiredData];
        expectedResult = service.addColorsToCollectionIfMissing(colorsCollection, undefined, null);
        expect(expectedResult).toEqual(colorsCollection);
      });
    });

    describe('compareColors', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareColors(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareColors(entity1, entity2);
        const compareResult2 = service.compareColors(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareColors(entity1, entity2);
        const compareResult2 = service.compareColors(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareColors(entity1, entity2);
        const compareResult2 = service.compareColors(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
