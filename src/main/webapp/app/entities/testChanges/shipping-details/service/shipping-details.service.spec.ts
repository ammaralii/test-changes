import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DATE_FORMAT } from 'app/config/input.constants';
import { IShippingDetails } from '../shipping-details.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../shipping-details.test-samples';

import { ShippingDetailsService, RestShippingDetails } from './shipping-details.service';

const requireRestSample: RestShippingDetails = {
  ...sampleWithRequiredData,
  estimatedDeliveryDate: sampleWithRequiredData.estimatedDeliveryDate?.format(DATE_FORMAT),
};

describe('ShippingDetails Service', () => {
  let service: ShippingDetailsService;
  let httpMock: HttpTestingController;
  let expectedResult: IShippingDetails | IShippingDetails[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ShippingDetailsService);
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

    it('should create a ShippingDetails', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const shippingDetails = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(shippingDetails).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ShippingDetails', () => {
      const shippingDetails = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(shippingDetails).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ShippingDetails', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ShippingDetails', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ShippingDetails', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addShippingDetailsToCollectionIfMissing', () => {
      it('should add a ShippingDetails to an empty array', () => {
        const shippingDetails: IShippingDetails = sampleWithRequiredData;
        expectedResult = service.addShippingDetailsToCollectionIfMissing([], shippingDetails);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(shippingDetails);
      });

      it('should not add a ShippingDetails to an array that contains it', () => {
        const shippingDetails: IShippingDetails = sampleWithRequiredData;
        const shippingDetailsCollection: IShippingDetails[] = [
          {
            ...shippingDetails,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addShippingDetailsToCollectionIfMissing(shippingDetailsCollection, shippingDetails);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ShippingDetails to an array that doesn't contain it", () => {
        const shippingDetails: IShippingDetails = sampleWithRequiredData;
        const shippingDetailsCollection: IShippingDetails[] = [sampleWithPartialData];
        expectedResult = service.addShippingDetailsToCollectionIfMissing(shippingDetailsCollection, shippingDetails);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(shippingDetails);
      });

      it('should add only unique ShippingDetails to an array', () => {
        const shippingDetailsArray: IShippingDetails[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const shippingDetailsCollection: IShippingDetails[] = [sampleWithRequiredData];
        expectedResult = service.addShippingDetailsToCollectionIfMissing(shippingDetailsCollection, ...shippingDetailsArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const shippingDetails: IShippingDetails = sampleWithRequiredData;
        const shippingDetails2: IShippingDetails = sampleWithPartialData;
        expectedResult = service.addShippingDetailsToCollectionIfMissing([], shippingDetails, shippingDetails2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(shippingDetails);
        expect(expectedResult).toContain(shippingDetails2);
      });

      it('should accept null and undefined values', () => {
        const shippingDetails: IShippingDetails = sampleWithRequiredData;
        expectedResult = service.addShippingDetailsToCollectionIfMissing([], null, shippingDetails, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(shippingDetails);
      });

      it('should return initial array if no ShippingDetails is added', () => {
        const shippingDetailsCollection: IShippingDetails[] = [sampleWithRequiredData];
        expectedResult = service.addShippingDetailsToCollectionIfMissing(shippingDetailsCollection, undefined, null);
        expect(expectedResult).toEqual(shippingDetailsCollection);
      });
    });

    describe('compareShippingDetails', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareShippingDetails(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareShippingDetails(entity1, entity2);
        const compareResult2 = service.compareShippingDetails(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareShippingDetails(entity1, entity2);
        const compareResult2 = service.compareShippingDetails(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareShippingDetails(entity1, entity2);
        const compareResult2 = service.compareShippingDetails(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
