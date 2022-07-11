package com.billingservice.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Bill {
	
	String ecommerce;
	
	BigDecimal amount;

}
