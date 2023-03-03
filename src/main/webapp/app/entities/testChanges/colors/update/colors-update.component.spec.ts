import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ColorsFormService } from './colors-form.service';
import { ColorsService } from '../service/colors.service';
import { IColors } from '../colors.model';

import { ColorsUpdateComponent } from './colors-update.component';

describe('Colors Management Update Component', () => {
  let comp: ColorsUpdateComponent;
  let fixture: ComponentFixture<ColorsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let colorsFormService: ColorsFormService;
  let colorsService: ColorsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ColorsUpdateComponent],
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
      .overrideTemplate(ColorsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ColorsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    colorsFormService = TestBed.inject(ColorsFormService);
    colorsService = TestBed.inject(ColorsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const colors: IColors = { id: 456 };

      activatedRoute.data = of({ colors });
      comp.ngOnInit();

      expect(comp.colors).toEqual(colors);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IColors>>();
      const colors = { id: 123 };
      jest.spyOn(colorsFormService, 'getColors').mockReturnValue(colors);
      jest.spyOn(colorsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ colors });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: colors }));
      saveSubject.complete();

      // THEN
      expect(colorsFormService.getColors).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(colorsService.update).toHaveBeenCalledWith(expect.objectContaining(colors));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IColors>>();
      const colors = { id: 123 };
      jest.spyOn(colorsFormService, 'getColors').mockReturnValue({ id: null });
      jest.spyOn(colorsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ colors: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: colors }));
      saveSubject.complete();

      // THEN
      expect(colorsFormService.getColors).toHaveBeenCalled();
      expect(colorsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IColors>>();
      const colors = { id: 123 };
      jest.spyOn(colorsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ colors });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(colorsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
