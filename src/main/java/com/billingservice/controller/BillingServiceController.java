package com.billingservice.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.billingservice.dto.BillingResponse;
import com.billingservice.dto.DatabaseActionResponse;
import com.billingservice.mapper.BillingMapper;
import com.billingservice.model.Ecommerce;
import com.billingservice.model.PaymentProcessor;
import com.billingservice.service.BillingService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j

@AllArgsConstructor
@Controller
@RequestMapping
public class BillingServiceController {
	
	private BillingService billingService;
	
	private BillingMapper billingMapper;
	
	@GetMapping("/")
	public String index(Model model) {
		List<PaymentProcessor> paymentProcessors = billingMapper.getAllPaymentProcessors();
		List<Ecommerce> ecommerces = billingMapper.getAllEcommerces();
		model.addAttribute("paymentProcessors", paymentProcessors);
		model.addAttribute("ecommerces", ecommerces);
		return "index";
	}
	
	@PostMapping("/billingInfo")
	public ResponseEntity<Object> ecommerceBillingInfo(@RequestParam("paymentProcessorId") int paymentProcessorId,
			@RequestParam("month") int month, @RequestParam("year") int year) {
		try {
			log.info("Request for Payment Processor {} for {}/{}", paymentProcessorId, month, year);
			BillingResponse billingResponse = billingService.getPaymentProcessorBill(paymentProcessorId, month, year);
			return ResponseEntity.ok(billingResponse);
		} catch(Exception ex) {
			log.error("Exception get", ex);
			return ResponseEntity.internalServerError().build();
		}

	}
	
	@PostMapping("/modifyFee")
	public ResponseEntity<Object> modifyFee(@RequestParam("paymentProcessorId") int paymentProcessorId, 
			@RequestParam("flatFee") BigDecimal flatFee, @RequestParam("acquirerPlusLV") BigDecimal acquirerPlusLV,
			@RequestParam("acquirerPlusHV") BigDecimal acquirerPlusHV, @RequestParam("feeIndicator") int feeIndicator) {	
		try {
			log.info("Request for change the fee received for PP {} ff {} applv {} apphv {} limit {}",
					paymentProcessorId, flatFee, acquirerPlusLV, acquirerPlusHV, feeIndicator );
			DatabaseActionResponse modifyFeeResponse = billingService.modifyFee(paymentProcessorId, flatFee, acquirerPlusLV, acquirerPlusHV, feeIndicator);
			return ResponseEntity.ok(modifyFeeResponse);
		} catch(Exception ex) {
			log.error("Exception get", ex);
			return ResponseEntity.internalServerError().build();
		}
	}
}
