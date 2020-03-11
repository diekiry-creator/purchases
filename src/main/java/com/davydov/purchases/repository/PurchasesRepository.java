package com.davydov.purchases.repository;

import com.davydov.purchases.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchasesRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByUserId(long userId);
    List<Purchase> findAll();
}
