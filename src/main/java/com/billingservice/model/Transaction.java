package com.billingservice.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="transactions")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Transaction {
	
	@Id
	@GeneratedValue
	private int id;
	
	@Column(name = "ec_id")
	private int ecommerceId;
		
	@Column(name = "month")
	private int month;
	
	@Column(name = "year")
	private int year;
	
	@Column(name = "volume")
	private int volume;
	
	@Column(name = "amount")
	private BigDecimal amount;
	
	public BigDecimal rowAmount() {
		return amount.multiply(new BigDecimal(volume));
	}
	
}
