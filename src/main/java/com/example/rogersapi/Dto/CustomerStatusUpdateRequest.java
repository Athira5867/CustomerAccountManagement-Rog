package com.example.rogersapi.Dto;

import com.example.rogersapi.Model.Status;

import lombok.Data;

@Data

public class CustomerStatusUpdateRequest {
	
	private Status status;
	private String accountId;
	
	public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
   
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	

}
