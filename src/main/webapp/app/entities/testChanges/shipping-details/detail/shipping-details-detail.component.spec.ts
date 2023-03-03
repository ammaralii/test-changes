import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ShippingDetailsDetailComponent } from './shipping-details-detail.component';

describe('ShippingDetails Management Detail Component', () => {
  let comp: ShippingDetailsDetailComponent;
  let fixture: ComponentFixture<ShippingDetailsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ShippingDetailsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ shippingDetails: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ShippingDetailsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ShippingDetailsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load shippingDetails on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.shippingDetails).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
