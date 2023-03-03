import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CategoriesFormService } from './categories-form.service';
import { CategoriesService } from '../service/categories.service';
import { ICategories } from '../categories.model';

import { CategoriesUpdateComponent } from './categories-update.component';

describe('Categories Management Update Component', () => {
  let comp: CategoriesUpdateComponent;
  let fixture: ComponentFixture<CategoriesUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let categoriesFormService: CategoriesFormService;
  let categoriesService: CategoriesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CategoriesUpdateComponent],
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
      .overrideTemplate(CategoriesUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CategoriesUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    categoriesFormService = TestBed.inject(CategoriesFormService);
    categoriesService = TestBed.inject(CategoriesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const categories: ICategories = { id: 456 };

      activatedRoute.data = of({ categories });
      comp.ngOnInit();

      expect(comp.categories).toEqual(categories);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICategories>>();
      const categories = { id: 123 };
      jest.spyOn(categoriesFormService, 'getCategories').mockReturnValue(categories);
      jest.spyOn(categoriesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ categories });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: categories }));
      saveSubject.complete();

      // THEN
      expect(categoriesFormService.getCategories).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(categoriesService.update).toHaveBeenCalledWith(expect.objectContaining(categories));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICategories>>();
      const categories = { id: 123 };
      jest.spyOn(categoriesFormService, 'getCategories').mockReturnValue({ id: null });
      jest.spyOn(categoriesService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ categories: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: categories }));
      saveSubject.complete();

      // THEN
      expect(categoriesFormService.getCategories).toHaveBeenCalled();
      expect(categoriesService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICategories>>();
      const categories = { id: 123 };
      jest.spyOn(categoriesService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ categories });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(categoriesService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
