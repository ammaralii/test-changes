import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { SearchWithPagination } from 'app/core/request/request.model';
import { IColors, NewColors } from '../colors.model';

export type PartialUpdateColors = Partial<IColors> & Pick<IColors, 'id'>;

export type EntityResponseType = HttpResponse<IColors>;
export type EntityArrayResponseType = HttpResponse<IColors[]>;

@Injectable({ providedIn: 'root' })
export class ColorsService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/colors', 'testchanges');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/colors', 'testchanges');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(colors: NewColors): Observable<EntityResponseType> {
    return this.http.post<IColors>(this.resourceUrl, colors, { observe: 'response' });
  }

  update(colors: IColors): Observable<EntityResponseType> {
    return this.http.put<IColors>(`${this.resourceUrl}/${this.getColorsIdentifier(colors)}`, colors, { observe: 'response' });
  }

  partialUpdate(colors: PartialUpdateColors): Observable<EntityResponseType> {
    return this.http.patch<IColors>(`${this.resourceUrl}/${this.getColorsIdentifier(colors)}`, colors, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IColors>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IColors[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: SearchWithPagination): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IColors[]>(this.resourceSearchUrl, { params: options, observe: 'response' });
  }

  getColorsIdentifier(colors: Pick<IColors, 'id'>): number {
    return colors.id;
  }

  compareColors(o1: Pick<IColors, 'id'> | null, o2: Pick<IColors, 'id'> | null): boolean {
    return o1 && o2 ? this.getColorsIdentifier(o1) === this.getColorsIdentifier(o2) : o1 === o2;
  }

  addColorsToCollectionIfMissing<Type extends Pick<IColors, 'id'>>(
    colorsCollection: Type[],
    ...colorsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const colors: Type[] = colorsToCheck.filter(isPresent);
    if (colors.length > 0) {
      const colorsCollectionIdentifiers = colorsCollection.map(colorsItem => this.getColorsIdentifier(colorsItem)!);
      const colorsToAdd = colors.filter(colorsItem => {
        const colorsIdentifier = this.getColorsIdentifier(colorsItem);
        if (colorsCollectionIdentifiers.includes(colorsIdentifier)) {
          return false;
        }
        colorsCollectionIdentifiers.push(colorsIdentifier);
        return true;
      });
      return [...colorsToAdd, ...colorsCollection];
    }
    return colorsCollection;
  }
}
