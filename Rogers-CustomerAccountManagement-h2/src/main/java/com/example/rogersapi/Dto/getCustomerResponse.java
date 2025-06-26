package com.example.rogersapi.Dto;

import com.example.rogersapi.Model.Status;

public class getCustomerResponse {

	private String accountId;
    private String email;
    private Status status;   
    private int age;
    private LocationDto location;
    
    public getCustomerResponse(String accountId, String email, Status status, int age, LocationDto location) {
        this.accountId = accountId;
        this.email = email;
        this.status = status;
        this.age = age;
        this.location = location;
    }
    
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public LocationDto getLocation() {
		return location;
	}
	public void setLocation(LocationDto location) {
		this.location = location;
	}
    
    
}

