package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Products.
 */
@Entity
@Table(name = "products")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "products")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Products implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "productsCategories" }, allowSetters = true)
    private Categories category;

    @OneToMany(mappedBy = "product")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "order", "product" }, allowSetters = true)
    private Set<OrderDetails> orderdetailsProducts = new HashSet<>();

    @OneToMany(mappedBy = "product")
    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "product" }, allowSetters = true)
    private Set<ProductDetails> productdetailsProducts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Products id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Products name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Categories getCategory() {
        return this.category;
    }

    public void setCategory(Categories categories) {
        this.category = categories;
    }

    public Products category(Categories categories) {
        this.setCategory(categories);
        return this;
    }

    public Set<OrderDetails> getOrderdetailsProducts() {
        return this.orderdetailsProducts;
    }

    public void setOrderdetailsProducts(Set<OrderDetails> orderDetails) {
        if (this.orderdetailsProducts != null) {
            this.orderdetailsProducts.forEach(i -> i.setProduct(null));
        }
        if (orderDetails != null) {
            orderDetails.forEach(i -> i.setProduct(this));
        }
        this.orderdetailsProducts = orderDetails;
    }

    public Products orderdetailsProducts(Set<OrderDetails> orderDetails) {
        this.setOrderdetailsProducts(orderDetails);
        return this;
    }

    public Products addOrderdetailsProduct(OrderDetails orderDetails) {
        this.orderdetailsProducts.add(orderDetails);
        orderDetails.setProduct(this);
        return this;
    }

    public Products removeOrderdetailsProduct(OrderDetails orderDetails) {
        this.orderdetailsProducts.remove(orderDetails);
        orderDetails.setProduct(null);
        return this;
    }

    public Set<ProductDetails> getProductdetailsProducts() {
        return this.productdetailsProducts;
    }

    public void setProductdetailsProducts(Set<ProductDetails> productDetails) {
        if (this.productdetailsProducts != null) {
            this.productdetailsProducts.forEach(i -> i.setProduct(null));
        }
        if (productDetails != null) {
            productDetails.forEach(i -> i.setProduct(this));
        }
        this.productdetailsProducts = productDetails;
    }

    public Products productdetailsProducts(Set<ProductDetails> productDetails) {
        this.setProductdetailsProducts(productDetails);
        return this;
    }

    public Products addProductdetailsProduct(ProductDetails productDetails) {
        this.productdetailsProducts.add(productDetails);
        productDetails.setProduct(this);
        return this;
    }

    public Products removeProductdetailsProduct(ProductDetails productDetails) {
        this.productdetailsProducts.remove(productDetails);
        productDetails.setProduct(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Products)) {
            return false;
        }
        return id != null && id.equals(((Products) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Products{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
