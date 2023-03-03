import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IOrderDetails } from '../order-details.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../order-details.test-samples';

import { OrderDetailsService } from './order-details.service';

const requireRestSample: IOrderDetails = {
  ...sampleWithRequiredData,
};

describe('OrderDetails Service', () => {
  let service: OrderDetailsService;
  let httpMock: HttpTestingController;
  let expectedResult: IOrderDetails | IOrderDetails[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OrderDetailsService);
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

    it('should create a OrderDetails', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const orderDetails = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(orderDetails).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrderDetails', () => {
      const orderDetails = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(orderDetails).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OrderDetails', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OrderDetails', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OrderDetails', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOrderDetailsToCollectionIfMissing', () => {
      it('should add a OrderDetails to an empty array', () => {
        const orderDetails: IOrderDetails = sampleWithRequiredData;
        expectedResult = service.addOrderDetailsToCollectionIfMissing([], orderDetails);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderDetails);
      });

      it('should not add a OrderDetails to an array that contains it', () => {
        const orderDetails: IOrderDetails = sampleWithRequiredData;
        const orderDetailsCollection: IOrderDetails[] = [
          {
            ...orderDetails,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOrderDetailsToCollectionIfMissing(orderDetailsCollection, orderDetails);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrderDetails to an array that doesn't contain it", () => {
        const orderDetails: IOrderDetails = sampleWithRequiredData;
        const orderDetailsCollection: IOrderDetails[] = [sampleWithPartialData];
        expectedResult = service.addOrderDetailsToCollectionIfMissing(orderDetailsCollection, orderDetails);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderDetails);
      });

      it('should add only unique OrderDetails to an array', () => {
        const orderDetailsArray: IOrderDetails[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const orderDetailsCollection: IOrderDetails[] = [sampleWithRequiredData];
        expectedResult = service.addOrderDetailsToCollectionIfMissing(orderDetailsCollection, ...orderDetailsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const orderDetails: IOrderDetails = sampleWithRequiredData;
        const orderDetails2: IOrderDetails = sampleWithPartialData;
        expectedResult = service.addOrderDetailsToCollectionIfMissing([], orderDetails, orderDetails2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderDetails);
        expect(expectedResult).toContain(orderDetails2);
      });

      it('should accept null and undefined values', () => {
        const orderDetails: IOrderDetails = sampleWithRequiredData;
        expectedResult = service.addOrderDetailsToCollectionIfMissing([], null, orderDetails, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderDetails);
      });

      it('should return initial array if no OrderDetails is added', () => {
        const orderDetailsCollection: IOrderDetails[] = [sampleWithRequiredData];
        expectedResult = service.addOrderDetailsToCollectionIfMissing(orderDetailsCollection, undefined, null);
        expect(expectedResult).toEqual(orderDetailsCollection);
      });
    });

    describe('compareOrderDetails', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOrderDetails(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareOrderDetails(entity1, entity2);
        const compareResult2 = service.compareOrderDetails(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareOrderDetails(entity1, entity2);
        const compareResult2 = service.compareOrderDetails(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareOrderDetails(entity1, entity2);
        const compareResult2 = service.compareOrderDetails(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
