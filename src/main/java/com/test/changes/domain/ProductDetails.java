package com.test.changes.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A ProductDetails.
 */
@Entity
@Table(name = "product_details")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "productdetails")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductDetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 65535)
    @Column(name = "description", length = 65535, nullable = false)
    private String description;

    @NotNull
    @Size(max = 32)
    @Column(name = "image_url", length = 32, nullable = false)
    private String imageUrl;

    @NotNull
    @Column(name = "isavailable", nullable = false)
    private Boolean isavailable;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties(value = { "category", "orderdetailsProducts", "productdetailsProducts" }, allowSetters = true)
    private Products product;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductDetails id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public ProductDetails description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public ProductDetails imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getIsavailable() {
        return this.isavailable;
    }

    public ProductDetails isavailable(Boolean isavailable) {
        this.setIsavailable(isavailable);
        return this;
    }

    public void setIsavailable(Boolean isavailable) {
        this.isavailable = isavailable;
    }

    public Products getProduct() {
        return this.product;
    }

    public void setProduct(Products products) {
        this.product = products;
    }

    public ProductDetails product(Products products) {
        this.setProduct(products);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductDetails)) {
            return false;
        }
        return id != null && id.equals(((ProductDetails) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductDetails{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", isavailable='" + getIsavailable() + "'" +
            "}";
    }
}
