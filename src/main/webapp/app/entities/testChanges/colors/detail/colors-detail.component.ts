import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IColors } from '../colors.model';

@Component({
  selector: 'jhi-colors-detail',
  templateUrl: './colors-detail.component.html',
})
export class ColorsDetailComponent implements OnInit {
  colors: IColors | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ colors }) => {
      this.colors = colors;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
