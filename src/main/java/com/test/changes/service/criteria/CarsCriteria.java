package com.test.changes.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.test.changes.domain.Cars} entity. This class is used
 * in {@link com.test.changes.web.rest.CarsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cars?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CarsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter caruid;

    private StringFilter name;

    private LongFilter colorId;

    private Boolean distinct;

    public CarsCriteria() {}

    public CarsCriteria(CarsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.caruid = other.caruid == null ? null : other.caruid.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.colorId = other.colorId == null ? null : other.colorId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public CarsCriteria copy() {
        return new CarsCriteria(this);
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

    public IntegerFilter getCaruid() {
        return caruid;
    }

    public IntegerFilter caruid() {
        if (caruid == null) {
            caruid = new IntegerFilter();
        }
        return caruid;
    }

    public void setCaruid(IntegerFilter caruid) {
        this.caruid = caruid;
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

    public LongFilter getColorId() {
        return colorId;
    }

    public LongFilter colorId() {
        if (colorId == null) {
            colorId = new LongFilter();
        }
        return colorId;
    }

    public void setColorId(LongFilter colorId) {
        this.colorId = colorId;
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
        final CarsCriteria that = (CarsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(caruid, that.caruid) &&
            Objects.equals(name, that.name) &&
            Objects.equals(colorId, that.colorId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, caruid, name, colorId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CarsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (caruid != null ? "caruid=" + caruid + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (colorId != null ? "colorId=" + colorId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
