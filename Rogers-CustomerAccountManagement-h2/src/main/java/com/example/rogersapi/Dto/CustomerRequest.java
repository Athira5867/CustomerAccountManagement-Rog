package com.example.rogersapi.Dto;

import com.example.rogersapi.Model.Country;
import com.example.rogersapi.Model.Status;
import jakarta.validation.constraints.*;

public class CustomerRequest {

	@NotBlank(message = "Name must not be blank")
	@Size(max = 20, message = "Name is required and must not exceed 20 characters")
    private String name;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Country is required")
    private Country country;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

    @NotNull(message = "Status is required")
    private Status status;

    @Min(value = 0, message = "Age must be positive")
    private int age;

    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Country getCountry() { return country; }
    public void setCountry(Country country) { this.country = country; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
