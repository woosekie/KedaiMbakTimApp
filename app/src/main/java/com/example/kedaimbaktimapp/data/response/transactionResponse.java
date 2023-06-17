package com.example.kedaimbaktimapp.data.response;

import com.google.gson.annotations.SerializedName;

public class transactionResponse {

	@SerializedName("payment_type")
	private String paymentType;

	@SerializedName("transaction_status")
	private String transactionStatus;

	@SerializedName("order_id")

	private String orderId;

	public String getPaymentType(){
		return paymentType;
	}

	public String getTransactionStatus(){
		return transactionStatus;
	}

	public String getOrderId(){
		return orderId;
	}
}