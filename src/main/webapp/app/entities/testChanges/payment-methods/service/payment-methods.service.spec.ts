import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IPaymentMethods } from '../payment-methods.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../payment-methods.test-samples';

import { PaymentMethodsService } from './payment-methods.service';

const requireRestSample: IPaymentMethods = {
  ...sampleWithRequiredData,
};

describe('PaymentMethods Service', () => {
  let service: PaymentMethodsService;
  let httpMock: HttpTestingController;
  let expectedResult: IPaymentMethods | IPaymentMethods[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PaymentMethodsService);
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

    it('should create a PaymentMethods', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const paymentMethods = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(paymentMethods).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a PaymentMethods', () => {
      const paymentMethods = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(paymentMethods).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a PaymentMethods', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of PaymentMethods', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a PaymentMethods', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addPaymentMethodsToCollectionIfMissing', () => {
      it('should add a PaymentMethods to an empty array', () => {
        const paymentMethods: IPaymentMethods = sampleWithRequiredData;
        expectedResult = service.addPaymentMethodsToCollectionIfMissing([], paymentMethods);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(paymentMethods);
      });

      it('should not add a PaymentMethods to an array that contains it', () => {
        const paymentMethods: IPaymentMethods = sampleWithRequiredData;
        const paymentMethodsCollection: IPaymentMethods[] = [
          {
            ...paymentMethods,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPaymentMethodsToCollectionIfMissing(paymentMethodsCollection, paymentMethods);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a PaymentMethods to an array that doesn't contain it", () => {
        const paymentMethods: IPaymentMethods = sampleWithRequiredData;
        const paymentMethodsCollection: IPaymentMethods[] = [sampleWithPartialData];
        expectedResult = service.addPaymentMethodsToCollectionIfMissing(paymentMethodsCollection, paymentMethods);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(paymentMethods);
      });

      it('should add only unique PaymentMethods to an array', () => {
        const paymentMethodsArray: IPaymentMethods[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const paymentMethodsCollection: IPaymentMethods[] = [sampleWithRequiredData];
        expectedResult = service.addPaymentMethodsToCollectionIfMissing(paymentMethodsCollection, ...paymentMethodsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const paymentMethods: IPaymentMethods = sampleWithRequiredData;
        const paymentMethods2: IPaymentMethods = sampleWithPartialData;
        expectedResult = service.addPaymentMethodsToCollectionIfMissing([], paymentMethods, paymentMethods2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(paymentMethods);
        expect(expectedResult).toContain(paymentMethods2);
      });

      it('should accept null and undefined values', () => {
        const paymentMethods: IPaymentMethods = sampleWithRequiredData;
        expectedResult = service.addPaymentMethodsToCollectionIfMissing([], null, paymentMethods, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(paymentMethods);
      });

      it('should return initial array if no PaymentMethods is added', () => {
        const paymentMethodsCollection: IPaymentMethods[] = [sampleWithRequiredData];
        expectedResult = service.addPaymentMethodsToCollectionIfMissing(paymentMethodsCollection, undefined, null);
        expect(expectedResult).toEqual(paymentMethodsCollection);
      });
    });

    describe('comparePaymentMethods', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePaymentMethods(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.comparePaymentMethods(entity1, entity2);
        const compareResult2 = service.comparePaymentMethods(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.comparePaymentMethods(entity1, entity2);
        const compareResult2 = service.comparePaymentMethods(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.comparePaymentMethods(entity1, entity2);
        const compareResult2 = service.comparePaymentMethods(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
