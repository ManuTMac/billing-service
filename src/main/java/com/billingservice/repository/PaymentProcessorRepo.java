package com.billingservice.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billingservice.model.PaymentProcessor;

@Transactional
public interface PaymentProcessorRepo extends JpaRepository<PaymentProcessor, Integer>{
	
	List<PaymentProcessor> findAll();
	
	Optional<PaymentProcessor> findById(int paymentProcessorId);

}
