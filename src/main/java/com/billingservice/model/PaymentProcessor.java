package com.billingservice.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Builder;
import lombok.Data;

@Entity
@Table(name="payment_processor")
@Data
@Builder
public class PaymentProcessor {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "flat_fee")
	private BigDecimal flatFee;
	
	@Column(name = "app_lv")
	private BigDecimal acquirerPlusLV;
	
	@Column(name = "app_hv")
	private BigDecimal acquirerPlusHV;
	
	@Column(name = "fee_indicator")
	private int feeIndicator;

}
