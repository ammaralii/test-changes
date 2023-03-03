package com.test.changes.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.test.changes.domain.Roles} entity. This class is used
 * in {@link com.test.changes.web.rest.RolesResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /roles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class RolesCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter rolePrId;

    private StringFilter name;

    private LongFilter userId;

    private Boolean distinct;

    public RolesCriteria() {}

    public RolesCriteria(RolesCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.rolePrId = other.rolePrId == null ? null : other.rolePrId.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public RolesCriteria copy() {
        return new RolesCriteria(this);
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

    public IntegerFilter getRolePrId() {
        return rolePrId;
    }

    public IntegerFilter rolePrId() {
        if (rolePrId == null) {
            rolePrId = new IntegerFilter();
        }
        return rolePrId;
    }

    public void setRolePrId(IntegerFilter rolePrId) {
        this.rolePrId = rolePrId;
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

    public LongFilter getUserId() {
        return userId;
    }

    public LongFilter userId() {
        if (userId == null) {
            userId = new LongFilter();
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
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
        final RolesCriteria that = (RolesCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(rolePrId, that.rolePrId) &&
            Objects.equals(name, that.name) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rolePrId, name, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RolesCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (rolePrId != null ? "rolePrId=" + rolePrId + ", " : "") +
            (name != null ? "name=" + name + ", " : "") +
            (userId != null ? "userId=" + userId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
