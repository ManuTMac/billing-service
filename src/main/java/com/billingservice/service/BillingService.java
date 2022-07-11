package com.billingservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.billingservice.dto.Bill;
import com.billingservice.dto.BillingResponse;
import com.billingservice.dto.DatabaseActionResponse;
import com.billingservice.mapper.BillingMapper;
import com.billingservice.model.Ecommerce;
import com.billingservice.model.PaymentProcessor;
import com.billingservice.model.Transaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class BillingService {
	
	private static final String OK = "ok";
	private static final String NO_PAYMENT_PROCESSOR_FOUND = "The payment processor requested doesn't exist or has not any ecommerce linked";
	private static final String NO_FEE_FOUND = "No fee is assigned to the ecommerce %s | ";
	private static final String NO_TRANSACTION_FOUND = "No transactions found for the ecommerce %s and period requested | ";
	private static final String MODIFIED = "Changes applied successfully";
	private static final String BAD_FEE_VALUE = "The value of the fees must be between 0 and 1 (Ex. 1% = 0.01)";
	private static final String PP_ALREADY_EXISTS = "There is already a payment processor with this name";
	private static final String EC_ALREADY_EXISTS = "There is already a ecommerce with this name assigned to the payment processor";
	private static final String PP_CREATED = "Payment Processor successfuly created. Id -> %x.";
	private static final String EC_CREATED = "Ecommerce successfuly created. Id -> %x.";
	
	@Autowired
	private BillingMapper billingMapper;
	
	private String result;
	
	public BillingResponse getPaymentProcessorBill(int paymentProcessorId, int month, int year) {
		result = "";
		List <Bill> paymentProcessorBilling = new ArrayList<>();
		List <Ecommerce> paymentProcessorEcommerces = billingMapper.getEcommercesOfPaymentProcessor(paymentProcessorId);
		if (paymentProcessorEcommerces.isEmpty()) {
			log.warn(NO_PAYMENT_PROCESSOR_FOUND);
			return BillingResponse.builder().billList(Collections.emptyList()).result(NO_PAYMENT_PROCESSOR_FOUND).build();
		} else {
			paymentProcessorEcommerces.forEach(e -> paymentProcessorBilling.add(getEcommerceBill(paymentProcessorId, e, month, year)));
		return BillingResponse.builder().billList(paymentProcessorBilling).result(result.isEmpty() ? OK : result).build();
		}
	}
	
	private Bill getEcommerceBill(int paymentProcessorId, Ecommerce ecommerce, int month, int year) {
		BigDecimal billAmount = calculateTotalAmount(ecommerce, month, year)
				.multiply(calculateFee(paymentProcessorId, ecommerce, month, year))
				.setScale(2, RoundingMode.HALF_EVEN);
		return Bill.builder().ecommerce(ecommerce.getName()).amount(billAmount).build();
	}
	
	private BigDecimal calculateTotalAmount(Ecommerce ecommerce, int month, int year) {
		List<Transaction> monthlyTransactions = billingMapper.getMonthlyTransactions(ecommerce.getId(), month, year);
		if (monthlyTransactions.isEmpty()) {
			log.warn(NO_TRANSACTION_FOUND, ecommerce.getName());
			addBillResult(NO_TRANSACTION_FOUND, ecommerce.getName());
			return BigDecimal.ZERO;
		}
		return billingMapper.getMonthlyTransactions(ecommerce.getId(), month, year)
				.stream()
			.map(Transaction::rowAmount)
			.reduce(BigDecimal.ZERO, BigDecimal::add);	
	}

	private BigDecimal calculateFee(int paymentProcessorId, Ecommerce ecommerce, int month, int year) {
		int monthVolume = billingMapper.getMonthVolume(ecommerce.getId(), month, year);
		log.info("The ecommerce {} has {} transactions for {}/{}", ecommerce.getId(), monthVolume, month, year);
		Optional<PaymentProcessor> paymentProcessor = billingMapper.getPaymentProcessor(paymentProcessorId);
		log.info("PP pricing recovered: {}", paymentProcessor);
		if (paymentProcessor.isPresent()) {
			BigDecimal appFee = (monthVolume > paymentProcessor.get().getFeeIndicator()) ? paymentProcessor.get().getAcquirerPlusHV() 
					: paymentProcessor.get().getAcquirerPlusLV();
			log.info("APP fee applied: {}", appFee);
			return appFee.add(paymentProcessor.get().getFlatFee());	
		} else {
			return addBillResult(NO_FEE_FOUND, ecommerce.getName());
		}
	}
	
	private BigDecimal addBillResult(String error, String ecommerceName) {
		result += String.format(error, ecommerceName);
		return BigDecimal.ZERO;
	}

	public DatabaseActionResponse modifyFee(int paymentProcessorId, BigDecimal flatFee, BigDecimal acquirerPlusLV,
			BigDecimal acquirerPlusHV, int feeIndicator) {
		try {
			Optional<PaymentProcessor> paymentProcessor = billingMapper.getPaymentProcessor(paymentProcessorId);
			if (paymentProcessor.isPresent()) {
				billingMapper.updatePricing(paymentProcessor.get(), flatFee, acquirerPlusLV,acquirerPlusHV, feeIndicator);
				return DatabaseActionResponse.builder().result(MODIFIED).build();
			} else {
				return DatabaseActionResponse.builder().result(NO_PAYMENT_PROCESSOR_FOUND).build();
			}
		} catch (DataIntegrityViolationException ex) {
			return DatabaseActionResponse.builder().result(BAD_FEE_VALUE).build();
		}
	}

	public DatabaseActionResponse createPaymentProcessor(String paymentProcessorName, BigDecimal flatFee,
			BigDecimal acquirerPlusLV, BigDecimal acquirerPlusHV, int feeIndicator) {
		try {
			if (Boolean.TRUE.equals(billingMapper.existsPaymentProcessor(paymentProcessorName))) {
				return DatabaseActionResponse.builder().result(PP_ALREADY_EXISTS).build();
			} else {
				int newPpId = billingMapper.createPaymentProcessor(paymentProcessorName, flatFee, acquirerPlusLV, acquirerPlusHV, feeIndicator);
				return DatabaseActionResponse.builder().result(String.format(PP_CREATED, newPpId)).build();
			}
		} catch (DataIntegrityViolationException ex) {
			return DatabaseActionResponse.builder().result(BAD_FEE_VALUE).build();
		}
	}
	
	public DatabaseActionResponse createEcommerce(String ecommerceName, int paymentprocessorId) {
		try {
			if (Boolean.TRUE.equals(billingMapper.existsEcommerce(ecommerceName, paymentprocessorId))) {
				return DatabaseActionResponse.builder().result(EC_ALREADY_EXISTS).build();
			} else {
				int newPpId = billingMapper.createEcommerce(ecommerceName, paymentprocessorId);
				return DatabaseActionResponse.builder().result(String.format(EC_CREATED, newPpId)).build();
			}
		} catch (DataIntegrityViolationException ex) {
			return DatabaseActionResponse.builder().result(BAD_FEE_VALUE).build();
		}
	}

}
