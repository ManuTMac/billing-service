package com.billingservice.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.billingservice.model.Ecommerce;
import com.billingservice.model.PaymentProcessor;
import com.billingservice.model.Transaction;
import com.billingservice.repository.EcommerceRepo;
import com.billingservice.repository.PaymentProcessorRepo;
import com.billingservice.repository.TransactionRepo;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BillingMapper {


	@Autowired
	private PaymentProcessorRepo paymentProcessorRepo;
	
	@Autowired
	private EcommerceRepo ecommerceRepo;
	
	@Autowired
	private TransactionRepo transactionRepo;
	

	public List<Ecommerce> getEcommercesOfPaymentProcessor(int paymentProcessorId) {
		return ecommerceRepo.findByPaymentProcessorId(paymentProcessorId);
	}

	public int getMonthVolume(int ecommerceId, int month, int year) {
		return transactionRepo.getMonthVolume(ecommerceId, month, year);
	}

	public List<Transaction> getMonthlyTransactions(int ecommerceId, int month, int year) {
		return transactionRepo.findByEcommerceIdAndMonthAndYear(ecommerceId, month, year);
	}

	public List<PaymentProcessor> getAllPaymentProcessors() {
		return paymentProcessorRepo.findAll();
	}

	public List<Ecommerce> getAllEcommerces() {
		return ecommerceRepo.findAll();
	}

	public Optional<PaymentProcessor> getPaymentProcessor(int paymentProcessorId) {
		return paymentProcessorRepo.findById(paymentProcessorId);
	}

	public void updatePricing(PaymentProcessor paymentProcessor, BigDecimal flatFee, BigDecimal acquirerPlusLV,
			BigDecimal acquirerPlusHV, int feeIndicator) {
		paymentProcessor.setFlatFee(flatFee);
		paymentProcessor.setAcquirerPlusLV(acquirerPlusLV);
		paymentProcessor.setAcquirerPlusHV(acquirerPlusHV);
		paymentProcessor.setFeeIndicator(feeIndicator);
		paymentProcessorRepo.save(paymentProcessor);	
	}

	public Boolean existsPaymentProcessor(String paymentProcessorName) {
		return paymentProcessorRepo.existsByName(paymentProcessorName);
	}

	public int createPaymentProcessor(String paymentProcessorName, BigDecimal flatFee, BigDecimal acquirerPlusLV,
			BigDecimal acquirerPlusHV, int feeIndicator) {
		PaymentProcessor paymentProcessor = PaymentProcessor.builder()
				.name(paymentProcessorName)
				.flatFee(flatFee)
				.acquirerPlusLV(acquirerPlusLV)
				.acquirerPlusHV(acquirerPlusHV)
				.feeIndicator(feeIndicator).build();
		return paymentProcessorRepo.save(paymentProcessor).getId();
	}

	public Boolean existsEcommerce(String ecommerceName, int paymentprocessorId) {
		return ecommerceRepo.existsByNameAndPaymentProcessorId(ecommerceName, paymentprocessorId);
	}

	public int createEcommerce(String ecommerceName, int paymentProcessorId) {
		Ecommerce ecommerce = Ecommerce.builder()
				.name(ecommerceName)
				.paymentProcessorId(paymentProcessorId).build();
		return ecommerceRepo.save(ecommerce).getId();
	}

}
