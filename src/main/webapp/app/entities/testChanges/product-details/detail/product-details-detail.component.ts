import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProductDetails } from '../product-details.model';

@Component({
  selector: 'jhi-product-details-detail',
  templateUrl: './product-details-detail.component.html',
})
export class ProductDetailsDetailComponent implements OnInit {
  productDetails: IProductDetails | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ productDetails }) => {
      this.productDetails = productDetails;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
