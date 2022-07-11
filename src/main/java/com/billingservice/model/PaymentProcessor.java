package com.billingservice.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name="payment_processor")
@Data
public class PaymentProcessor {
	
	@Id
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
