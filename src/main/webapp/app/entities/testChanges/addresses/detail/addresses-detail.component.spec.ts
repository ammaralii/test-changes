import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AddressesDetailComponent } from './addresses-detail.component';

describe('Addresses Management Detail Component', () => {
  let comp: AddressesDetailComponent;
  let fixture: ComponentFixture<AddressesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AddressesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ addresses: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AddressesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AddressesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load addresses on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.addresses).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
