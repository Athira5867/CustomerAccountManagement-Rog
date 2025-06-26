package com.example.rogersapi.Controller;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.rogersapi.Dto.CustomerRequest;
import com.example.rogersapi.Dto.CustomerResponse;
import com.example.rogersapi.Dto.CustomerStatusUpdateRequest;
import com.example.rogersapi.Dto.getCustomerResponse;
import com.example.rogersapi.Model.Country;
import com.example.rogersapi.Model.Customer;
import com.example.rogersapi.Service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;



@Tag(name ="Customer API" , description = "Operations related to customer management")
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

	@Autowired
    private CustomerService customerService;
	
	private final RestTemplate restTemplate = new RestTemplate();

	@Operation(summary = "Create a new customer", description = "Creates a new customer with the given details and returns account ID and PIN.")
    @PostMapping("/createCustomer")
    public ResponseEntity<?> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        try {
            Customer customer = new Customer();
            customer.setName(customerRequest.getName());
            customer.setEmail(customerRequest.getEmail());
            customer.setCountry(customerRequest.getCountry());
            customer.setPostalCode(customerRequest.getPostalCode());
            customer.setStatus(customerRequest.getStatus());
            customer.setAge(customerRequest.getAge());

            Customer saved = customerService.saveCustomer(customer);

            CustomerResponse response = new CustomerResponse(
               saved.getAccountId(),
               saved.getStatus(),
               saved.getPin()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }

    }
    
	@Operation(summary = "Update customer", description = "Updates an existing ACTIVE customer using accountId.")
    @PutMapping("/updateCustomer/{accountId}")
    public ResponseEntity<?> updateCustomer(@PathVariable String accountId, @RequestBody CustomerRequest updates) {
        try {
            Customer updateCustomer = new Customer();
            updateCustomer.setName(updates.getName());
            updateCustomer.setEmail(updates.getEmail());
            updateCustomer.setCountry(updates.getCountry());
            updateCustomer.setPostalCode(updates.getPostalCode());
            updateCustomer.setAge(updates.getAge());
            updateCustomer.setStatus(updates.getStatus());

            Customer updated = customerService.updateCustomer(accountId, updateCustomer);
            return ResponseEntity.ok("Updated successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

	@Operation(summary = "Delete customer", description = "Deletes a customer by accountId and PIN if the status is INACTIVE.")
    @DeleteMapping("/deleteCustomer")
    public ResponseEntity<?> deleteCustomer(@RequestParam String accountId, @RequestParam int pin) {
        try {
            customerService.deleteCustomer(accountId, pin);
            return ResponseEntity.ok("Deleted successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

	@Operation(summary = "Get customer", description = "Retrieve customer by email or accountId.")
    @GetMapping("/getCustomer")
    public ResponseEntity<?> getCustomer(@RequestParam(required = false) String email,
                                         @RequestParam(required = false) String accountId) {
        Optional<getCustomerResponse> response = customerService.getCustomerResponseByEmailOrAccountId(email, accountId);
        return response.<ResponseEntity<?>>map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.badRequest().body("Customer not found"));
    }
    
	@Operation(summary = "Update customer status", description = "Updates the status (e.g. ACTIVE, INACTIVE) of a customer.")
    @PatchMapping("/updateStatus")
    public ResponseEntity<?> updateCustomerStatus(@RequestBody CustomerStatusUpdateRequest request) {
        try {
            customerService.updateStatus(request.getAccountId(), request.getStatus());
            return ResponseEntity.ok("Status updated successfully");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
	
	@Operation(summary = "Get customer counts", description = "Returns count of customers grouped by state and place for a given country.")
    @GetMapping("/counts")
    public ResponseEntity<?> getCountsByCountry(@RequestParam Country country) {
        return ResponseEntity.ok(customerService.getCustomerCountByCountryStatePlace(country));
    }
}
