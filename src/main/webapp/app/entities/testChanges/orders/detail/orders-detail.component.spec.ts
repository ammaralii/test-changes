import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { OrdersDetailComponent } from './orders-detail.component';

describe('Orders Management Detail Component', () => {
  let comp: OrdersDetailComponent;
  let fixture: ComponentFixture<OrdersDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrdersDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ orders: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(OrdersDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(OrdersDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load orders on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.orders).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
