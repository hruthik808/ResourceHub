package com.reuse.reuse.repository;

import com.reuse.reuse.model.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Page<Resource> findByCategory(String category, Pageable pageable);
    Page<Resource> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}

