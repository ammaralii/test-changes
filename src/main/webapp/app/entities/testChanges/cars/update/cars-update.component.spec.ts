import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CarsFormService } from './cars-form.service';
import { CarsService } from '../service/cars.service';
import { ICars } from '../cars.model';
import { IColors } from 'app/entities/testChanges/colors/colors.model';
import { ColorsService } from 'app/entities/testChanges/colors/service/colors.service';

import { CarsUpdateComponent } from './cars-update.component';

describe('Cars Management Update Component', () => {
  let comp: CarsUpdateComponent;
  let fixture: ComponentFixture<CarsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let carsFormService: CarsFormService;
  let carsService: CarsService;
  let colorsService: ColorsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CarsUpdateComponent],
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
      .overrideTemplate(CarsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CarsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    carsFormService = TestBed.inject(CarsFormService);
    carsService = TestBed.inject(CarsService);
    colorsService = TestBed.inject(ColorsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Colors query and add missing value', () => {
      const cars: ICars = { id: 456 };
      const colors: IColors[] = [{ id: 49576 }];
      cars.colors = colors;

      const colorsCollection: IColors[] = [{ id: 79310 }];
      jest.spyOn(colorsService, 'query').mockReturnValue(of(new HttpResponse({ body: colorsCollection })));
      const additionalColors = [...colors];
      const expectedCollection: IColors[] = [...additionalColors, ...colorsCollection];
      jest.spyOn(colorsService, 'addColorsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ cars });
      comp.ngOnInit();

      expect(colorsService.query).toHaveBeenCalled();
      expect(colorsService.addColorsToCollectionIfMissing).toHaveBeenCalledWith(
        colorsCollection,
        ...additionalColors.map(expect.objectContaining)
      );
      expect(comp.colorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const cars: ICars = { id: 456 };
      const color: IColors = { id: 81502 };
      cars.colors = [color];

      activatedRoute.data = of({ cars });
      comp.ngOnInit();

      expect(comp.colorsSharedCollection).toContain(color);
      expect(comp.cars).toEqual(cars);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICars>>();
      const cars = { id: 123 };
      jest.spyOn(carsFormService, 'getCars').mockReturnValue(cars);
      jest.spyOn(carsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cars });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cars }));
      saveSubject.complete();

      // THEN
      expect(carsFormService.getCars).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(carsService.update).toHaveBeenCalledWith(expect.objectContaining(cars));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICars>>();
      const cars = { id: 123 };
      jest.spyOn(carsFormService, 'getCars').mockReturnValue({ id: null });
      jest.spyOn(carsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cars: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: cars }));
      saveSubject.complete();

      // THEN
      expect(carsFormService.getCars).toHaveBeenCalled();
      expect(carsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICars>>();
      const cars = { id: 123 };
      jest.spyOn(carsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ cars });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(carsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareColors', () => {
      it('Should forward to colorsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(colorsService, 'compareColors');
        comp.compareColors(entity, entity2);
        expect(colorsService.compareColors).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
