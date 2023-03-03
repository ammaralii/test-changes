import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProductDetails } from '../product-details.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../product-details.test-samples';

import { ProductDetailsService } from './product-details.service';

const requireRestSample: IProductDetails = {
  ...sampleWithRequiredData,
};

describe('ProductDetails Service', () => {
  let service: ProductDetailsService;
  let httpMock: HttpTestingController;
  let expectedResult: IProductDetails | IProductDetails[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProductDetailsService);
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

    it('should create a ProductDetails', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const productDetails = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(productDetails).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ProductDetails', () => {
      const productDetails = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(productDetails).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ProductDetails', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ProductDetails', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ProductDetails', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addProductDetailsToCollectionIfMissing', () => {
      it('should add a ProductDetails to an empty array', () => {
        const productDetails: IProductDetails = sampleWithRequiredData;
        expectedResult = service.addProductDetailsToCollectionIfMissing([], productDetails);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productDetails);
      });

      it('should not add a ProductDetails to an array that contains it', () => {
        const productDetails: IProductDetails = sampleWithRequiredData;
        const productDetailsCollection: IProductDetails[] = [
          {
            ...productDetails,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addProductDetailsToCollectionIfMissing(productDetailsCollection, productDetails);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ProductDetails to an array that doesn't contain it", () => {
        const productDetails: IProductDetails = sampleWithRequiredData;
        const productDetailsCollection: IProductDetails[] = [sampleWithPartialData];
        expectedResult = service.addProductDetailsToCollectionIfMissing(productDetailsCollection, productDetails);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productDetails);
      });

      it('should add only unique ProductDetails to an array', () => {
        const productDetailsArray: IProductDetails[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const productDetailsCollection: IProductDetails[] = [sampleWithRequiredData];
        expectedResult = service.addProductDetailsToCollectionIfMissing(productDetailsCollection, ...productDetailsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const productDetails: IProductDetails = sampleWithRequiredData;
        const productDetails2: IProductDetails = sampleWithPartialData;
        expectedResult = service.addProductDetailsToCollectionIfMissing([], productDetails, productDetails2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(productDetails);
        expect(expectedResult).toContain(productDetails2);
      });

      it('should accept null and undefined values', () => {
        const productDetails: IProductDetails = sampleWithRequiredData;
        expectedResult = service.addProductDetailsToCollectionIfMissing([], null, productDetails, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(productDetails);
      });

      it('should return initial array if no ProductDetails is added', () => {
        const productDetailsCollection: IProductDetails[] = [sampleWithRequiredData];
        expectedResult = service.addProductDetailsToCollectionIfMissing(productDetailsCollection, undefined, null);
        expect(expectedResult).toEqual(productDetailsCollection);
      });
    });

    describe('compareProductDetails', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareProductDetails(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareProductDetails(entity1, entity2);
        const compareResult2 = service.compareProductDetails(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareProductDetails(entity1, entity2);
        const compareResult2 = service.compareProductDetails(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareProductDetails(entity1, entity2);
        const compareResult2 = service.compareProductDetails(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
