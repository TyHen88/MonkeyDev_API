package com.dev.monkey_dev.domain.respository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.monkey_dev.domain.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
