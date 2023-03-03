import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CategoriesDetailComponent } from './categories-detail.component';

describe('Categories Management Detail Component', () => {
  let comp: CategoriesDetailComponent;
  let fixture: ComponentFixture<CategoriesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CategoriesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ categories: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CategoriesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CategoriesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load categories on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.categories).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
