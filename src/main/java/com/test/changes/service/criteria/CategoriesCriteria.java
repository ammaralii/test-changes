package com.test.changes.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.test.changes.domain.Categories} entity. This class is used
 * in {@link com.test.changes.web.rest.CategoriesResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /categories?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CategoriesCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter detail;

    private LongFilter productsCategoryId;

    private Boolean distinct;

    public CategoriesCriteria() {}

    public CategoriesCriteria(CategoriesCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.detail = other.detail == null ? null : other.detail.copy();
        this.productsCategoryId = other.productsCategoryId == null ? null : other.productsCategoryId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CategoriesCriteria copy() {
        return new CategoriesCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public StringFilter name() {
        if (name == null) {
            name = new StringFilter();
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public StringFilter getDetail() {
        return detail;
    }

    public StringFilter detail() {
        if (detail == null) {
            detail = new StringFilter();
        }
        return detail;
    }

    public void setDetail(StringFilter detail) {
        this.detail = detail;
    }

    public LongFilter getProductsCategoryId() {
        return productsCategoryId;
    }

    public LongFilter productsCategoryId() {
        if (productsCategoryId == null) {
            productsCategoryId = new LongFilter();
        }
        return productsCategoryId;
    }

    public void setProductsCategoryId(LongFilter productsCategoryId) {
        this.productsCategoryId = productsCategoryId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CategoriesCriteria that = (CategoriesCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(detail, that.detail) &&
            Objects.equals(productsCategoryId, that.productsCategoryId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, detail, productsCategoryId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CategoriesCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (detail != null ? "detail=" + detail + ", " : "") +
            (productsCategoryId != null ? "productsCategoryId=" + productsCategoryId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
