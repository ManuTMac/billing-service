package com.billingservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import com.billingservice.dto.Bill;
import com.billingservice.dto.BillingResponse;
import com.billingservice.dto.DatabaseActionResponse;
import com.billingservice.mapper.BillingMapper;
import com.billingservice.model.Ecommerce;
import com.billingservice.model.PaymentProcessor;
import com.billingservice.model.Transaction;

public class BillingServiceTest {
	
	private BillingService billingService;
	private BillingMapper billingMapper;
	private PaymentProcessor testPaymentProcessor;
	private Ecommerce testEcommerce;
	private Transaction testTransaction;
	private static final BigDecimal ZERO = new BigDecimal("0.00");
	private static final String NO_PAYMENT_PROCESSOR_FOUND = "The payment processor requested doesn't exist or has not any ecommerce linked";
	private static final String NO_TRANSACTION_FOUND = "No transactions found for the ecommerce EC1 and period requested | ";
	private static final String MODIFIED = "Changes applied successfully";
	private static final String BAD_FEE_VALUE = "The value of the fees must be between 0 and 1 (Ex. 1% = 0.01)";

	@BeforeEach
	void setUp() {
		billingMapper = mock(BillingMapper.class);
		billingService = new BillingService(billingMapper, "");
		testPaymentProcessor = PaymentProcessor.builder().id(1).name("PP1").flatFee(new BigDecimal("0.05"))
				.acquirerPlusLV(new BigDecimal("0.05")).acquirerPlusHV(new BigDecimal("0.02")).feeIndicator(50).build();
		testEcommerce = Ecommerce.builder().id(1).name("EC1").paymentProcessorId(1).build();
		testTransaction = Transaction.builder().id(1).ecommerceId(1).volume(100).amount(BigDecimal.ONE).month(1).year(1).build();
		when(billingMapper.getPaymentProcessor(1)).thenReturn(Optional.of(testPaymentProcessor));	
		when(billingMapper.getEcommercesOfPaymentProcessor(1)).thenReturn(Arrays.asList(testEcommerce));
	}

	@Test
	public void testOkBilling() throws Exception {
		when(billingMapper.getMonthVolume(1, 1, 2022)).thenReturn(100);		
		when(billingMapper.getMonthlyTransactions(1, 1, 2022)).thenReturn(Arrays.asList(testTransaction));
		Bill okBill = Bill.builder().ecommerce("EC1").amount(new BigDecimal("7.00")).build();
		BillingResponse okBillResponse = BillingResponse.builder().billList(Arrays.asList(okBill)).result("ok").build();
		assertEquals(okBillResponse, billingService.getPaymentProcessorBill(1, 1, 2022));
	}
	
	@Test
	public void testNoEcommercesBilling() throws Exception{
		when(billingMapper.getEcommercesOfPaymentProcessor(2)).thenReturn(Collections.emptyList());
		BillingResponse noEcBillResponse = BillingResponse.builder().billList(Collections.emptyList()).result(NO_PAYMENT_PROCESSOR_FOUND).build();
		assertEquals(noEcBillResponse, billingService.getPaymentProcessorBill(2, 1, 2022));
	}
	
	@Test
	public void testNoTransactionBilling() throws Exception{
		when(billingMapper.getMonthlyTransactions(1, 2, 2022)).thenReturn(Collections.emptyList());
		Bill zeroBill = Bill.builder().ecommerce("EC1").amount(new BigDecimal("0.00")).build();
		BillingResponse noTransactionBillResponse = BillingResponse.builder().billList(Arrays.asList(zeroBill)).result(NO_TRANSACTION_FOUND).build();
		assertEquals(noTransactionBillResponse, billingService.getPaymentProcessorBill(1, 2, 2022));
	}
	
	@Test
	public void testOkModifyFee() throws Exception{
		doNothing().when(billingMapper).updatePricing(testPaymentProcessor, ZERO, ZERO, ZERO, 20);
		DatabaseActionResponse okResponse = DatabaseActionResponse.builder().result(MODIFIED).build();
		assertEquals(okResponse, billingService.modifyFee(1, ZERO, ZERO, ZERO, 20));
	}

	@Test
	public void testNoPpModifyFee() throws Exception{
		when(billingMapper.getPaymentProcessor(2)).thenReturn(Optional.empty());
		DatabaseActionResponse noPpResponse = DatabaseActionResponse.builder().result(NO_PAYMENT_PROCESSOR_FOUND).build();
		assertEquals(noPpResponse, billingService.modifyFee(2, ZERO, ZERO, ZERO, 20));
	}
	
	@Test
	public void testBadFeeValueModifyFee() throws Exception{
		doThrow(new DataIntegrityViolationException("Ex")).when(billingMapper).updatePricing(testPaymentProcessor, BigDecimal.ONE, ZERO, ZERO, 20);
		DatabaseActionResponse badFeeResponse = DatabaseActionResponse.builder().result(BAD_FEE_VALUE).build();
		assertEquals(badFeeResponse, billingService.modifyFee(1, BigDecimal.ONE, ZERO, ZERO, 20));
	}
	
}
