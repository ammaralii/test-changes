import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrderDeliveryDetailComponent } from './order-delivery-detail.component';

describe('OrderDelivery Management Detail Component', () => {
  let comp: OrderDeliveryDetailComponent;
  let fixture: ComponentFixture<OrderDeliveryDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrderDeliveryDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ orderDelivery: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OrderDeliveryDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OrderDeliveryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load orderDelivery on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.orderDelivery).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
