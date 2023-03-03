import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CarsDetailComponent } from './cars-detail.component';

describe('Cars Management Detail Component', () => {
  let comp: CarsDetailComponent;
  let fixture: ComponentFixture<CarsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CarsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ cars: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CarsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CarsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load cars on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.cars).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
