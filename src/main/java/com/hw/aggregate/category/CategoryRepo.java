package com.hw.aggregate.category;

import com.hw.aggregate.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {
}
