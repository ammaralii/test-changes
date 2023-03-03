import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICategories } from '../categories.model';

@Component({
  selector: 'jhi-categories-detail',
  templateUrl: './categories-detail.component.html',
})
export class CategoriesDetailComponent implements OnInit {
  categories: ICategories | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ categories }) => {
      this.categories = categories;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
