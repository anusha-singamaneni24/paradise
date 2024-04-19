package com.anusha.paradise.repository;

import com.anusha.paradise.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PropertyRepository extends JpaRepository<Property, UUID> {
    List<Property> findByStatus(String status);

    List<Property> findByStatusAndPostedBy(String status, String postedBy);
}
