package com.billingservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.billingservice.model.Transaction;

public interface TransactionRepo extends JpaRepository<Transaction, Integer> {

	List<Transaction> findByEcommerceIdAndMonthAndYear(int ecommerceId, int month, int year);
	
	@Query(value = "SELECT IFNULL(SUM(volume),0) FROM transactions WHERE ec_id = ?1 AND month = ?2 AND year = ?3", nativeQuery = true)
	int getMonthVolume(int ecommerceId, int month, int year);
	
}
