import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProductsFormService } from './products-form.service';
import { ProductsService } from '../service/products.service';
import { IProducts } from '../products.model';
import { ICategories } from 'app/entities/testChanges/categories/categories.model';
import { CategoriesService } from 'app/entities/testChanges/categories/service/categories.service';

import { ProductsUpdateComponent } from './products-update.component';

describe('Products Management Update Component', () => {
  let comp: ProductsUpdateComponent;
  let fixture: ComponentFixture<ProductsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let productsFormService: ProductsFormService;
  let productsService: ProductsService;
  let categoriesService: CategoriesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProductsUpdateComponent],
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
      .overrideTemplate(ProductsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProductsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    productsFormService = TestBed.inject(ProductsFormService);
    productsService = TestBed.inject(ProductsService);
    categoriesService = TestBed.inject(CategoriesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Categories query and add missing value', () => {
      const products: IProducts = { id: 456 };
      const category: ICategories = { id: 52988 };
      products.category = category;

      const categoriesCollection: ICategories[] = [{ id: 60386 }];
      jest.spyOn(categoriesService, 'query').mockReturnValue(of(new HttpResponse({ body: categoriesCollection })));
      const additionalCategories = [category];
      const expectedCollection: ICategories[] = [...additionalCategories, ...categoriesCollection];
      jest.spyOn(categoriesService, 'addCategoriesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ products });
      comp.ngOnInit();

      expect(categoriesService.query).toHaveBeenCalled();
      expect(categoriesService.addCategoriesToCollectionIfMissing).toHaveBeenCalledWith(
        categoriesCollection,
        ...additionalCategories.map(expect.objectContaining)
      );
      expect(comp.categoriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const products: IProducts = { id: 456 };
      const category: ICategories = { id: 68822 };
      products.category = category;

      activatedRoute.data = of({ products });
      comp.ngOnInit();

      expect(comp.categoriesSharedCollection).toContain(category);
      expect(comp.products).toEqual(products);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProducts>>();
      const products = { id: 123 };
      jest.spyOn(productsFormService, 'getProducts').mockReturnValue(products);
      jest.spyOn(productsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ products });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: products }));
      saveSubject.complete();

      // THEN
      expect(productsFormService.getProducts).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(productsService.update).toHaveBeenCalledWith(expect.objectContaining(products));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProducts>>();
      const products = { id: 123 };
      jest.spyOn(productsFormService, 'getProducts').mockReturnValue({ id: null });
      jest.spyOn(productsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ products: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: products }));
      saveSubject.complete();

      // THEN
      expect(productsFormService.getProducts).toHaveBeenCalled();
      expect(productsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IProducts>>();
      const products = { id: 123 };
      jest.spyOn(productsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ products });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(productsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCategories', () => {
      it('Should forward to categoriesService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(categoriesService, 'compareCategories');
        comp.compareCategories(entity, entity2);
        expect(categoriesService.compareCategories).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
