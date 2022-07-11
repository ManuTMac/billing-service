package com.billingservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billingservice.model.Ecommerce;

public interface EcommerceRepo extends JpaRepository<Ecommerce, Integer>{
	
	List<Ecommerce> findByPaymentProcessorId(int paymentProcessorId);

}
