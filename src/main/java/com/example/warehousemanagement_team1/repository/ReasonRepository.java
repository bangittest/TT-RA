package com.example.warehousemanagement_team1.repository;

import com.example.warehousemanagement_team1.model.Reason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReasonRepository extends JpaRepository<Reason,Long> {
    Reason findByDescription(String description);
}
