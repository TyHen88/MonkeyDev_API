package com.dev.monkey_dev.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "seller_profiles", uniqueConstraints = {
        @UniqueConstraint(name = "uk_seller_profile_user", columnNames = "user_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = "user")
@EqualsAndHashCode(callSuper = true, exclude = "user")
public class SellerProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private Users user;

    @NotBlank
    @Size(max = 200)
    @Column(name = "store_name", length = 200, nullable = false)
    private String storeName;

    @Size(max = 1000)
    @Column(name = "store_description", length = 1000)
    private String storeDescription;

    @Column(name = "store_logo_url", length = 500)
    private String storeLogoUrl;

    @Column(name = "store_banner_url", length = 500)
    private String storeBannerUrl;

    @Column(name = "seller_rating", precision = 3, scale = 2)
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal sellerRating;

    @Column(name = "total_reviews")
    private Long totalReviews;

    @Column(name = "total_sales")
    private Long totalSales;

    @Builder.Default
    @Column(name = "is_verified_seller", nullable = false)
    private boolean verifiedSeller = false;

    @Size(max = 100)
    @Column(name = "business_registration_number", length = 100)
    private String businessRegistrationNumber;

    @Size(max = 500)
    @Column(name = "business_address", length = 500)
    private String businessAddress;

    @Size(max = 100)
    @Column(name = "contact_phone", length = 100)
    private String contactPhone;

    @Size(max = 200)
    @Column(name = "contact_email", length = 200)
    private String contactEmail;

    @Column(name = "established_date")
    private LocalDateTime establishedDate;

    @Size(max = 1000)
    @Column(name = "return_policy", length = 1000)
    private String returnPolicy;

    @Size(max = 1000)
    @Column(name = "shipping_policy", length = 1000)
    private String shippingPolicy;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "verification_date")
    private LocalDateTime verificationDate;

    // Convenience methods
    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void verifySeller() {
        this.verifiedSeller = true;
        this.verificationDate = LocalDateTime.now();
    }

    public void unverifySeller() {
        this.verifiedSeller = false;
        this.verificationDate = null;
    }

    public void updateRating(BigDecimal newRating, Long totalReviews) {
        this.sellerRating = newRating;
        this.totalReviews = totalReviews;
    }

    public void incrementSales() {
        this.totalSales = (this.totalSales == null) ? 1 : this.totalSales + 1;
    }
}
