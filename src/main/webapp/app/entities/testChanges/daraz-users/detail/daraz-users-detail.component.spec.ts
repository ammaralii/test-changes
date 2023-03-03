import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { DarazUsersDetailComponent } from './daraz-users-detail.component';

describe('DarazUsers Management Detail Component', () => {
  let comp: DarazUsersDetailComponent;
  let fixture: ComponentFixture<DarazUsersDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [DarazUsersDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ darazUsers: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(DarazUsersDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(DarazUsersDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load darazUsers on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.darazUsers).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
