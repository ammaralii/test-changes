package com.test.changes.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.test.changes.domain.Colors} entity. This class is used
 * in {@link com.test.changes.web.rest.ColorsResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /colors?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ColorsCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter coloruid;

    private StringFilter name;

    private LongFilter carId;

    private Boolean distinct;

    public ColorsCriteria() {}

    public ColorsCriteria(ColorsCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.coloruid = other.coloruid == null ? null : other.coloruid.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.carId = other.carId == null ? null : other.carId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ColorsCriteria copy() {
        return new ColorsCriteria(this);
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

    public IntegerFilter getColoruid() {
        return coloruid;
    }

    public IntegerFilter coloruid() {
        if (coloruid == null) {
            coloruid = new IntegerFilter();
        }
        return coloruid;
    }

    public void setColoruid(IntegerFilter coloruid) {
        this.coloruid = coloruid;
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

    public LongFilter getCarId() {
        return carId;
    }

    public LongFilter carId() {
        if (carId == null) {
            carId = new LongFilter();
        }
        return carId;
    }

    public void setCarId(LongFilter carId) {
        this.carId = carId;
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
        final ColorsCriteria that = (ColorsCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(coloruid, that.coloruid) &&
            Objects.equals(name, that.name) &&
            Objects.equals(carId, that.carId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, coloruid, name, carId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ColorsCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (coloruid != null ? "coloruid=" + coloruid + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (carId != null ? "carId=" + carId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
