package com.billingservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "ecommerce")
@Data
public class Ecommerce {
	
	@Id
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "pp_id")
	private int paymentProcessorId;

}
