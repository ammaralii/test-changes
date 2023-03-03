import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProductDetailsDetailComponent } from './product-details-detail.component';

describe('ProductDetails Management Detail Component', () => {
  let comp: ProductDetailsDetailComponent;
  let fixture: ComponentFixture<ProductDetailsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProductDetailsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ productDetails: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProductDetailsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProductDetailsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load productDetails on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.productDetails).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
