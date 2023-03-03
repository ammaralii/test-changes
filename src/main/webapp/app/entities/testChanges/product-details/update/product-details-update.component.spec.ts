import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductDetailsFormService } from './product-details-form.service';
import { ProductDetailsService } from '../service/product-details.service';
import { IProductDetails } from '../product-details.model';
import { IProducts } from 'app/entities/testChanges/products/products.model';
import { ProductsService } from 'app/entities/testChanges/products/service/products.service';

import { ProductDetailsUpdateComponent } from './product-details-update.component';

describe('ProductDetails Management Update Component', () => {
  let comp: ProductDetailsUpdateComponent;
  let fixture: ComponentFixture<ProductDetailsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productDetailsFormService: ProductDetailsFormService;
  let productDetailsService: ProductDetailsService;
  let productsService: ProductsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductDetailsUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ProductDetailsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductDetailsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productDetailsFormService = TestBed.inject(ProductDetailsFormService);
    productDetailsService = TestBed.inject(ProductDetailsService);
    productsService = TestBed.inject(ProductsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Products query and add missing value', () => {
      const productDetails: IProductDetails = { id: 456 };
      const product: IProducts = { id: 95323 };
      productDetails.product = product;

      const productsCollection: IProducts[] = [{ id: 1902 }];
      jest.spyOn(productsService, 'query').mockReturnValue(of(new HttpResponse({ body: productsCollection })));
      const additionalProducts = [product];
      const expectedCollection: IProducts[] = [...additionalProducts, ...productsCollection];
      jest.spyOn(productsService, 'addProductsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ productDetails });
      comp.ngOnInit();

      expect(productsService.query).toHaveBeenCalled();
      expect(productsService.addProductsToCollectionIfMissing).toHaveBeenCalledWith(
        productsCollection,
        ...additionalProducts.map(expect.objectContaining)
      );
      expect(comp.productsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const productDetails: IProductDetails = { id: 456 };
      const product: IProducts = { id: 90954 };
      productDetails.product = product;

      activatedRoute.data = of({ productDetails });
      comp.ngOnInit();

      expect(comp.productsSharedCollection).toContain(product);
      expect(comp.productDetails).toEqual(productDetails);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductDetails>>();
      const productDetails = { id: 123 };
      jest.spyOn(productDetailsFormService, 'getProductDetails').mockReturnValue(productDetails);
      jest.spyOn(productDetailsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productDetails }));
      saveSubject.complete();

      // THEN
      expect(productDetailsFormService.getProductDetails).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productDetailsService.update).toHaveBeenCalledWith(expect.objectContaining(productDetails));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductDetails>>();
      const productDetails = { id: 123 };
      jest.spyOn(productDetailsFormService, 'getProductDetails').mockReturnValue({ id: null });
      jest.spyOn(productDetailsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productDetails: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: productDetails }));
      saveSubject.complete();

      // THEN
      expect(productDetailsFormService.getProductDetails).toHaveBeenCalled();
      expect(productDetailsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProductDetails>>();
      const productDetails = { id: 123 };
      jest.spyOn(productDetailsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ productDetails });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productDetailsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProducts', () => {
      it('Should forward to productsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(productsService, 'compareProducts');
        comp.compareProducts(entity, entity2);
        expect(productsService.compareProducts).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
