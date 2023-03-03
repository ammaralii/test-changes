import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ColorsDetailComponent } from './colors-detail.component';

describe('Colors Management Detail Component', () => {
  let comp: ColorsDetailComponent;
  let fixture: ComponentFixture<ColorsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ColorsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ colors: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ColorsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ColorsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load colors on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.colors).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
