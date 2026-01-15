package com.dev.monkey_dev.domain.respository;

import com.dev.monkey_dev.domain.entity.SellerProfile;
import com.dev.monkey_dev.domain.entity.Users;
import com.dev.monkey_dev.dto.response.SellerProfileResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // ✅ ADD
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal; // ✅ ADD
import java.util.List;
import java.util.Optional;

@Repository
public interface SellerProfileRepository
                extends JpaRepository<SellerProfile, Long>, JpaSpecificationExecutor<SellerProfile> { // ✅ FIX

        Optional<SellerProfile> findByUser(Users user);

        Optional<SellerProfile> findByUserId(Long userId);

        @Query("SELECT sp FROM SellerProfile sp WHERE sp.id = :id AND sp.active = true")
        Optional<SellerProfile> findActiveSellerProfileById(@Param("id") Long id);

        @Query("SELECT sp FROM SellerProfile sp WHERE sp.user.id = :userId AND sp.active = true")
        Optional<SellerProfile> findActiveSellerProfileByUserId(@Param("userId") Long userId);

        @Query("SELECT sp FROM SellerProfile sp WHERE sp.active = true")
        List<SellerProfile> findAllActiveSellerProfiles();

        @Query("SELECT sp FROM SellerProfile sp WHERE sp.verifiedSeller = true AND sp.active = true")
        List<SellerProfile> findAllVerifiedSellerProfiles();

        @Query("SELECT sp FROM SellerProfile sp WHERE " +
                        "(:isActive IS NULL OR sp.active = :isActive) AND " +
                        "(:isVerified IS NULL OR sp.verifiedSeller = :isVerified) AND " +
                        "(:search IS NULL OR :search = '' OR " +
                        "LOWER(sp.storeName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                        "LOWER(sp.businessRegistrationNumber) LIKE LOWER(CONCAT('%', :search, '%')))")
        Page<SellerProfile> findAllSellerProfilesWithFilters(
                        @Param("isActive") Boolean isActive,
                        @Param("isVerified") Boolean isVerified,
                        @Param("search") String search,
                        Pageable pageable);

        @Query("SELECT COUNT(sp) FROM SellerProfile sp WHERE sp.active = true")
        Long countActiveSellerProfiles();

        @Query("SELECT COUNT(sp) FROM SellerProfile sp WHERE sp.verifiedSeller = true AND sp.active = true")
        Long countVerifiedSellerProfiles();

        // ✅ FIX: minRating should be BigDecimal (if sellerRating is BigDecimal)
        @Query("SELECT sp FROM SellerProfile sp " +
                        "WHERE sp.sellerRating >= :minRating AND sp.active = true " +
                        "ORDER BY sp.sellerRating DESC")
        List<SellerProfile> findTopRatedSellers(@Param("minRating") BigDecimal minRating, Pageable pageable);

        @Query("SELECT sp FROM SellerProfile sp " +
                        "WHERE sp.totalSales >= :minSales AND sp.active = true " +
                        "ORDER BY sp.totalSales DESC")
        List<SellerProfile> findTopSellingSellers(@Param("minSales") Long minSales, Pageable pageable);

        boolean existsByUser(Users user);

        boolean existsByUserId(Long userId);
}
