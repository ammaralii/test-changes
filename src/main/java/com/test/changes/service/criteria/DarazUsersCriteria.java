package com.test.changes.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.test.changes.domain.DarazUsers} entity. This class is used
 * in {@link com.test.changes.web.rest.DarazUsersResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /daraz-users?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class DarazUsersCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter fullName;

    private StringFilter email;

    private StringFilter phone;

    private LongFilter managerId;

    private LongFilter roleId;

    private LongFilter addressesUserId;

    private LongFilter darazusersManagerId;

    private LongFilter orderdeliveryDeliverymanagerId;

    private LongFilter orderdeliveryDeliveryboyId;

    private Boolean distinct;

    public DarazUsersCriteria() {}

    public DarazUsersCriteria(DarazUsersCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.fullName = other.fullName == null ? null : other.fullName.copy();
        this.email = other.email == null ? null : other.email.copy();
        this.phone = other.phone == null ? null : other.phone.copy();
        this.managerId = other.managerId == null ? null : other.managerId.copy();
        this.roleId = other.roleId == null ? null : other.roleId.copy();
        this.addressesUserId = other.addressesUserId == null ? null : other.addressesUserId.copy();
        this.darazusersManagerId = other.darazusersManagerId == null ? null : other.darazusersManagerId.copy();
        this.orderdeliveryDeliverymanagerId =
            other.orderdeliveryDeliverymanagerId == null ? null : other.orderdeliveryDeliverymanagerId.copy();
        this.orderdeliveryDeliveryboyId = other.orderdeliveryDeliveryboyId == null ? null : other.orderdeliveryDeliveryboyId.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DarazUsersCriteria copy() {
        return new DarazUsersCriteria(this);
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

    public StringFilter getFullName() {
        return fullName;
    }

    public StringFilter fullName() {
        if (fullName == null) {
            fullName = new StringFilter();
        }
        return fullName;
    }

    public void setFullName(StringFilter fullName) {
        this.fullName = fullName;
    }

    public StringFilter getEmail() {
        return email;
    }

    public StringFilter email() {
        if (email == null) {
            email = new StringFilter();
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public StringFilter phone() {
        if (phone == null) {
            phone = new StringFilter();
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public LongFilter getManagerId() {
        return managerId;
    }

    public LongFilter managerId() {
        if (managerId == null) {
            managerId = new LongFilter();
        }
        return managerId;
    }

    public void setManagerId(LongFilter managerId) {
        this.managerId = managerId;
    }

    public LongFilter getRoleId() {
        return roleId;
    }

    public LongFilter roleId() {
        if (roleId == null) {
            roleId = new LongFilter();
        }
        return roleId;
    }

    public void setRoleId(LongFilter roleId) {
        this.roleId = roleId;
    }

    public LongFilter getAddressesUserId() {
        return addressesUserId;
    }

    public LongFilter addressesUserId() {
        if (addressesUserId == null) {
            addressesUserId = new LongFilter();
        }
        return addressesUserId;
    }

    public void setAddressesUserId(LongFilter addressesUserId) {
        this.addressesUserId = addressesUserId;
    }

    public LongFilter getDarazusersManagerId() {
        return darazusersManagerId;
    }

    public LongFilter darazusersManagerId() {
        if (darazusersManagerId == null) {
            darazusersManagerId = new LongFilter();
        }
        return darazusersManagerId;
    }

    public void setDarazusersManagerId(LongFilter darazusersManagerId) {
        this.darazusersManagerId = darazusersManagerId;
    }

    public LongFilter getOrderdeliveryDeliverymanagerId() {
        return orderdeliveryDeliverymanagerId;
    }

    public LongFilter orderdeliveryDeliverymanagerId() {
        if (orderdeliveryDeliverymanagerId == null) {
            orderdeliveryDeliverymanagerId = new LongFilter();
        }
        return orderdeliveryDeliverymanagerId;
    }

    public void setOrderdeliveryDeliverymanagerId(LongFilter orderdeliveryDeliverymanagerId) {
        this.orderdeliveryDeliverymanagerId = orderdeliveryDeliverymanagerId;
    }

    public LongFilter getOrderdeliveryDeliveryboyId() {
        return orderdeliveryDeliveryboyId;
    }

    public LongFilter orderdeliveryDeliveryboyId() {
        if (orderdeliveryDeliveryboyId == null) {
            orderdeliveryDeliveryboyId = new LongFilter();
        }
        return orderdeliveryDeliveryboyId;
    }

    public void setOrderdeliveryDeliveryboyId(LongFilter orderdeliveryDeliveryboyId) {
        this.orderdeliveryDeliveryboyId = orderdeliveryDeliveryboyId;
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
        final DarazUsersCriteria that = (DarazUsersCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(fullName, that.fullName) &&
            Objects.equals(email, that.email) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(managerId, that.managerId) &&
            Objects.equals(roleId, that.roleId) &&
            Objects.equals(addressesUserId, that.addressesUserId) &&
            Objects.equals(darazusersManagerId, that.darazusersManagerId) &&
            Objects.equals(orderdeliveryDeliverymanagerId, that.orderdeliveryDeliverymanagerId) &&
            Objects.equals(orderdeliveryDeliveryboyId, that.orderdeliveryDeliveryboyId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            fullName,
            email,
            phone,
            managerId,
            roleId,
            addressesUserId,
            darazusersManagerId,
            orderdeliveryDeliverymanagerId,
            orderdeliveryDeliveryboyId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DarazUsersCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (fullName != null ? "fullName=" + fullName + ", " : "") +
            (email != null ? "email=" + email + ", " : "") +
            (phone != null ? "phone=" + phone + ", " : "") +
            (managerId != null ? "managerId=" + managerId + ", " : "") +
            (roleId != null ? "roleId=" + roleId + ", " : "") +
            (addressesUserId != null ? "addressesUserId=" + addressesUserId + ", " : "") +
            (darazusersManagerId != null ? "darazusersManagerId=" + darazusersManagerId + ", " : "") +
            (orderdeliveryDeliverymanagerId != null ? "orderdeliveryDeliverymanagerId=" + orderdeliveryDeliverymanagerId + ", " : "") +
            (orderdeliveryDeliveryboyId != null ? "orderdeliveryDeliveryboyId=" + orderdeliveryDeliveryboyId + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
