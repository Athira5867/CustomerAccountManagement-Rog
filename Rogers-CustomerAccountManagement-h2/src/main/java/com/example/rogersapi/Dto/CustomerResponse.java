package com.example.rogersapi.Dto;

import com.example.rogersapi.Model.Status;

public class CustomerResponse {

	private String accountId;
    private Status status;
    private int pin;

    public CustomerResponse(String accountId, Status status, int pin) {
        this.accountId = accountId;
        this.status = status;
        this.pin = pin;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {  
        this.accountId = accountId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getPin() {     
        return pin;
    }

    public void setPin(int pin) {  
        this.pin = pin;
    }
}
