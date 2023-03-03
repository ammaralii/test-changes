import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IOrderDelivery } from '../order-delivery.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../order-delivery.test-samples';

import { OrderDeliveryService, RestOrderDelivery } from './order-delivery.service';

const requireRestSample: RestOrderDelivery = {
  ...sampleWithRequiredData,
  deliveryDate: sampleWithRequiredData.deliveryDate?.format(DATE_FORMAT),
};

describe('OrderDelivery Service', () => {
  let service: OrderDeliveryService;
  let httpMock: HttpTestingController;
  let expectedResult: IOrderDelivery | IOrderDelivery[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(OrderDeliveryService);
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

    it('should create a OrderDelivery', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const orderDelivery = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(orderDelivery).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a OrderDelivery', () => {
      const orderDelivery = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(orderDelivery).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a OrderDelivery', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of OrderDelivery', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a OrderDelivery', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addOrderDeliveryToCollectionIfMissing', () => {
      it('should add a OrderDelivery to an empty array', () => {
        const orderDelivery: IOrderDelivery = sampleWithRequiredData;
        expectedResult = service.addOrderDeliveryToCollectionIfMissing([], orderDelivery);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderDelivery);
      });

      it('should not add a OrderDelivery to an array that contains it', () => {
        const orderDelivery: IOrderDelivery = sampleWithRequiredData;
        const orderDeliveryCollection: IOrderDelivery[] = [
          {
            ...orderDelivery,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addOrderDeliveryToCollectionIfMissing(orderDeliveryCollection, orderDelivery);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a OrderDelivery to an array that doesn't contain it", () => {
        const orderDelivery: IOrderDelivery = sampleWithRequiredData;
        const orderDeliveryCollection: IOrderDelivery[] = [sampleWithPartialData];
        expectedResult = service.addOrderDeliveryToCollectionIfMissing(orderDeliveryCollection, orderDelivery);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderDelivery);
      });

      it('should add only unique OrderDelivery to an array', () => {
        const orderDeliveryArray: IOrderDelivery[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const orderDeliveryCollection: IOrderDelivery[] = [sampleWithRequiredData];
        expectedResult = service.addOrderDeliveryToCollectionIfMissing(orderDeliveryCollection, ...orderDeliveryArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const orderDelivery: IOrderDelivery = sampleWithRequiredData;
        const orderDelivery2: IOrderDelivery = sampleWithPartialData;
        expectedResult = service.addOrderDeliveryToCollectionIfMissing([], orderDelivery, orderDelivery2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(orderDelivery);
        expect(expectedResult).toContain(orderDelivery2);
      });

      it('should accept null and undefined values', () => {
        const orderDelivery: IOrderDelivery = sampleWithRequiredData;
        expectedResult = service.addOrderDeliveryToCollectionIfMissing([], null, orderDelivery, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(orderDelivery);
      });

      it('should return initial array if no OrderDelivery is added', () => {
        const orderDeliveryCollection: IOrderDelivery[] = [sampleWithRequiredData];
        expectedResult = service.addOrderDeliveryToCollectionIfMissing(orderDeliveryCollection, undefined, null);
        expect(expectedResult).toEqual(orderDeliveryCollection);
      });
    });

    describe('compareOrderDelivery', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareOrderDelivery(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareOrderDelivery(entity1, entity2);
        const compareResult2 = service.compareOrderDelivery(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareOrderDelivery(entity1, entity2);
        const compareResult2 = service.compareOrderDelivery(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareOrderDelivery(entity1, entity2);
        const compareResult2 = service.compareOrderDelivery(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
