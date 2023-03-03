import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICars } from '../cars.model';

@Component({
  selector: 'jhi-cars-detail',
  templateUrl: './cars-detail.component.html',
})
export class CarsDetailComponent implements OnInit {
  cars: ICars | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ cars }) => {
      this.cars = cars;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
