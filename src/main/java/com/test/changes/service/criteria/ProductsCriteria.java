package com.test.changes.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.test.changes.domain.Products} entity. This class is used
 * in {@link com.test.changes.web.rest.ProductsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /products?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LongFilter categoryId;

    private LongFilter orderdetailsProductId;

    private LongFilter productdetailsProductId;

    private Boolean distinct;

    public ProductsCriteria() {}

    public ProductsCriteria(ProductsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.categoryId = other.categoryId == null ? null : other.categoryId.copy();
        this.orderdetailsProductId = other.orderdetailsProductId == null ? null : other.orderdetailsProductId.copy();
        this.productdetailsProductId = other.productdetailsProductId == null ? null : other.productdetailsProductId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ProductsCriteria copy() {
        return new ProductsCriteria(this);
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

    public LongFilter getCategoryId() {
        return categoryId;
    }

    public LongFilter categoryId() {
        if (categoryId == null) {
            categoryId = new LongFilter();
        }
        return categoryId;
    }

    public void setCategoryId(LongFilter categoryId) {
        this.categoryId = categoryId;
    }

    public LongFilter getOrderdetailsProductId() {
        return orderdetailsProductId;
    }

    public LongFilter orderdetailsProductId() {
        if (orderdetailsProductId == null) {
            orderdetailsProductId = new LongFilter();
        }
        return orderdetailsProductId;
    }

    public void setOrderdetailsProductId(LongFilter orderdetailsProductId) {
        this.orderdetailsProductId = orderdetailsProductId;
    }

    public LongFilter getProductdetailsProductId() {
        return productdetailsProductId;
    }

    public LongFilter productdetailsProductId() {
        if (productdetailsProductId == null) {
            productdetailsProductId = new LongFilter();
        }
        return productdetailsProductId;
    }

    public void setProductdetailsProductId(LongFilter productdetailsProductId) {
        this.productdetailsProductId = productdetailsProductId;
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
        final ProductsCriteria that = (ProductsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(categoryId, that.categoryId) &&
            Objects.equals(orderdetailsProductId, that.orderdetailsProductId) &&
            Objects.equals(productdetailsProductId, that.productdetailsProductId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, categoryId, orderdetailsProductId, productdetailsProductId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (categoryId != null ? "categoryId=" + categoryId + ", " : "") +
            (orderdetailsProductId != null ? "orderdetailsProductId=" + orderdetailsProductId + ", " : "") +
            (productdetailsProductId != null ? "productdetailsProductId=" + productdetailsProductId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
