package com.ezcart.api.domain.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ezcart.api.domain.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}

