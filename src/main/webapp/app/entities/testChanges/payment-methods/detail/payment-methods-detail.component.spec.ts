import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PaymentMethodsDetailComponent } from './payment-methods-detail.component';

describe('PaymentMethods Management Detail Component', () => {
  let comp: PaymentMethodsDetailComponent;
  let fixture: ComponentFixture<PaymentMethodsDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentMethodsDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ paymentMethods: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PaymentMethodsDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PaymentMethodsDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load paymentMethods on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.paymentMethods).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
