import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { RolesDetailComponent } from './roles-detail.component';

describe('Roles Management Detail Component', () => {
  let comp: RolesDetailComponent;
  let fixture: ComponentFixture<RolesDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RolesDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ roles: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(RolesDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(RolesDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load roles on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.roles).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
