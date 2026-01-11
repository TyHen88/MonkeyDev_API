package com.dev.monkey_dev.domain.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dev.monkey_dev.domain.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId")
    List<Address> findByUserId(@Param("userId") Long userId);

    @Query("SELECT a FROM Address a WHERE a.user.id = :userId AND a.isDefault = true")
    List<Address> findDefaultAddressByUserId(@Param("userId") Long userId);

}
