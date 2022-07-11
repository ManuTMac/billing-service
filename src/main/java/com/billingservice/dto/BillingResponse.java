package com.billingservice.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillingResponse {
	
	List<Bill> billList;
	String result;

}
