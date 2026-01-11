package com.dev.monkey_dev.domain.respository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.dev.monkey_dev.domain.entity.Products;
public interface ProductRepository extends JpaRepository<Products, Long> {
    // Define repository methods here
    
}
