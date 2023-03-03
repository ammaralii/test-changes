import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IOrders } from '../orders.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../orders.test-samples';

import { OrdersService, RestOrders } from './orders.service';

const requireRestSample: RestOrders = {
  ...sampleWithRequiredData,
  orderDate: sampleWithRequiredData.orderDate?.format(DATE_FORMAT),
};

describe('Orders Service', () => {
  let service: OrdersService;
  let httpMock: HttpTestingController;
  let expectedResult: IOrders | IOrders[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OrdersService);
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

    it('should create a Orders', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const orders = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(orders).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Orders', () => {
      const orders = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(orders).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Orders', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Orders', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Orders', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOrdersToCollectionIfMissing', () => {
      it('should add a Orders to an empty array', () => {
        const orders: IOrders = sampleWithRequiredData;
        expectedResult = service.addOrdersToCollectionIfMissing([], orders);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orders);
      });

      it('should not add a Orders to an array that contains it', () => {
        const orders: IOrders = sampleWithRequiredData;
        const ordersCollection: IOrders[] = [
          {
            ...orders,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOrdersToCollectionIfMissing(ordersCollection, orders);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Orders to an array that doesn't contain it", () => {
        const orders: IOrders = sampleWithRequiredData;
        const ordersCollection: IOrders[] = [sampleWithPartialData];
        expectedResult = service.addOrdersToCollectionIfMissing(ordersCollection, orders);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orders);
      });

      it('should add only unique Orders to an array', () => {
        const ordersArray: IOrders[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ordersCollection: IOrders[] = [sampleWithRequiredData];
        expectedResult = service.addOrdersToCollectionIfMissing(ordersCollection, ...ordersArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const orders: IOrders = sampleWithRequiredData;
        const orders2: IOrders = sampleWithPartialData;
        expectedResult = service.addOrdersToCollectionIfMissing([], orders, orders2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orders);
        expect(expectedResult).toContain(orders2);
      });

      it('should accept null and undefined values', () => {
        const orders: IOrders = sampleWithRequiredData;
        expectedResult = service.addOrdersToCollectionIfMissing([], null, orders, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orders);
      });

      it('should return initial array if no Orders is added', () => {
        const ordersCollection: IOrders[] = [sampleWithRequiredData];
        expectedResult = service.addOrdersToCollectionIfMissing(ordersCollection, undefined, null);
        expect(expectedResult).toEqual(ordersCollection);
      });
    });

    describe('compareOrders', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOrders(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareOrders(entity1, entity2);
        const compareResult2 = service.compareOrders(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareOrders(entity1, entity2);
        const compareResult2 = service.compareOrders(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareOrders(entity1, entity2);
        const compareResult2 = service.compareOrders(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
