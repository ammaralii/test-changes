import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAddresses } from '../addresses.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../addresses.test-samples';

import { AddressesService } from './addresses.service';

const requireRestSample: IAddresses = {
  ...sampleWithRequiredData,
};

describe('Addresses Service', () => {
  let service: AddressesService;
  let httpMock: HttpTestingController;
  let expectedResult: IAddresses | IAddresses[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AddressesService);
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

    it('should create a Addresses', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const addresses = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(addresses).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Addresses', () => {
      const addresses = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(addresses).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Addresses', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Addresses', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Addresses', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addAddressesToCollectionIfMissing', () => {
      it('should add a Addresses to an empty array', () => {
        const addresses: IAddresses = sampleWithRequiredData;
        expectedResult = service.addAddressesToCollectionIfMissing([], addresses);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(addresses);
      });

      it('should not add a Addresses to an array that contains it', () => {
        const addresses: IAddresses = sampleWithRequiredData;
        const addressesCollection: IAddresses[] = [
          {
            ...addresses,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAddressesToCollectionIfMissing(addressesCollection, addresses);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Addresses to an array that doesn't contain it", () => {
        const addresses: IAddresses = sampleWithRequiredData;
        const addressesCollection: IAddresses[] = [sampleWithPartialData];
        expectedResult = service.addAddressesToCollectionIfMissing(addressesCollection, addresses);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(addresses);
      });

      it('should add only unique Addresses to an array', () => {
        const addressesArray: IAddresses[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const addressesCollection: IAddresses[] = [sampleWithRequiredData];
        expectedResult = service.addAddressesToCollectionIfMissing(addressesCollection, ...addressesArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const addresses: IAddresses = sampleWithRequiredData;
        const addresses2: IAddresses = sampleWithPartialData;
        expectedResult = service.addAddressesToCollectionIfMissing([], addresses, addresses2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(addresses);
        expect(expectedResult).toContain(addresses2);
      });

      it('should accept null and undefined values', () => {
        const addresses: IAddresses = sampleWithRequiredData;
        expectedResult = service.addAddressesToCollectionIfMissing([], null, addresses, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(addresses);
      });

      it('should return initial array if no Addresses is added', () => {
        const addressesCollection: IAddresses[] = [sampleWithRequiredData];
        expectedResult = service.addAddressesToCollectionIfMissing(addressesCollection, undefined, null);
        expect(expectedResult).toEqual(addressesCollection);
      });
    });

    describe('compareAddresses', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAddresses(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareAddresses(entity1, entity2);
        const compareResult2 = service.compareAddresses(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareAddresses(entity1, entity2);
        const compareResult2 = service.compareAddresses(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareAddresses(entity1, entity2);
        const compareResult2 = service.compareAddresses(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
