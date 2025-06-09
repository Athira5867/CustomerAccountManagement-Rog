package com.example.rogersapi.Controller;

import com.example.rogersapi.Model.Country;
import com.example.rogersapi.Model.Status;
import com.example.rogersapi.Dto.CustomerRequest;
import com.example.rogersapi.Dto.CustomerResponse;
import com.example.rogersapi.Dto.CustomerStatusUpdateRequest;
import com.example.rogersapi.Dto.LocationDto;
import com.example.rogersapi.Dto.getCustomerResponse;
import com.example.rogersapi.Model.Customer;
import com.example.rogersapi.Service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.containsString;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateCustomer_success() throws Exception {
        CustomerRequest request = new CustomerRequest();
        request.setName("John");
        request.setEmail("john@example.com");
        request.setCountry(Country.US);
        request.setPostalCode("90210");
        request.setStatus(Status.REQUESTED);
        request.setAge(30);

        Customer savedCustomer = new Customer();
        savedCustomer.setAccountId("ABC123");
        savedCustomer.setStatus(Status.ACTIVE);
        savedCustomer.setPin(1234);

        when(customerService.saveCustomer(any(Customer.class))).thenReturn(savedCustomer);

        mockMvc.perform(post("/api/customers/createCustomer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("ABC123"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.pin").value(1234));
    }

    @Test
    void testUpdateCustomer_success() throws Exception {
        CustomerRequest request = new CustomerRequest();
        request.setName("Updated Name");
        request.setEmail("updated@example.com");
        request.setCountry(Country.US);
        request.setPostalCode("12345");
        request.setStatus(Status.ACTIVE);
        request.setAge(40);

        Customer updatedCustomer = new Customer();
        updatedCustomer.setAccountId("ABC123");
        updatedCustomer.setStatus(Status.ACTIVE);

        when(customerService.updateCustomer(eq("ABC123"), any(Customer.class))).thenReturn(updatedCustomer);

        mockMvc.perform(put("/api/customers/updateCustomer/ABC123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Updated successfully"));
    }

    @Test
    void testDeleteCustomer_success() throws Exception {
        doNothing().when(customerService).deleteCustomer("ABC123", 1234);

        mockMvc.perform(delete("/api/customers/deleteCustomer")
                .param("accountId", "ABC123")
                .param("pin", "1234"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted successfully"));
    }

    @Test
    void testGetCustomer_success() throws Exception {
        getCustomerResponse response = new getCustomerResponse(
                "ABC123",
                "john@example.com",
                Status.ACTIVE,
                30,
                new LocationDto("City", "State", Country.US, "90210", "10.0", "20.0")
        );

        when(customerService.getCustomerResponseByEmailOrAccountId(eq("john@example.com"), isNull()))
                .thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/customers/getCustomer")
                .param("email", "john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value("ABC123"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void testUpdateStatus_success() throws Exception {
        CustomerStatusUpdateRequest request = new CustomerStatusUpdateRequest();
        request.setAccountId("ABC123");
        request.setStatus(Status.INACTIVE);

        doNothing().when(customerService).updateStatus("ABC123", Status.INACTIVE);

        mockMvc.perform(patch("/api/customers/updateStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Status updated successfully"));
    }

    @Test
    void testGetCountsByCountry_success() throws Exception {
        Map<String, Object> mockResponse = new LinkedHashMap<>();
        mockResponse.put("country", "US");
        mockResponse.put("count", 10);
        mockResponse.put("states", List.of(
                Map.of("state", "NY", "count", 5, "places", List.of(
                        Map.of("place", "New York", "count", 3),
                        Map.of("place", "Brooklyn", "count", 2)
                )),
                Map.of("state", "CA", "count", 5, "places", List.of(
                        Map.of("place", "Los Angeles", "count", 5)
                ))
        ));

        when(customerService.getCustomerCountByCountryStatePlace(Country.US)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/customers/counts")
                .param("country", "US"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.country").value("US"))
                .andExpect(jsonPath("$.count").value(10))
                .andExpect(jsonPath("$.states").isArray());
    }
    
    //AM going to handle some failure scenarios
    @Test
    public void createCustomer_InvalidEmail_ShouldReturnBadRequest() throws Exception {
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("John Doe");
        invalidRequest.setEmail("john.example");
        invalidRequest.setCountry(Country.US);
        invalidRequest.setPostalCode("12345");
        invalidRequest.setStatus(Status.REQUESTED);
        invalidRequest.setAge(30);

        mockMvc.perform(post("/api/customers/createCustomer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email should be valid")));
    }

    @Test
    public void createCustomer_NameTooLong_ShouldReturnBadRequest() throws Exception {
        CustomerRequest invalidRequest = new CustomerRequest();
        invalidRequest.setName("ABCDEFGHIJKLMNOPQRSTUVWXYZ"); 
        invalidRequest.setEmail("john@example.com");
        invalidRequest.setCountry(Country.US);
        invalidRequest.setPostalCode("12345");
        invalidRequest.setStatus(Status.REQUESTED);
        invalidRequest.setAge(30);

        mockMvc.perform(post("/api/customers/createCustomer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Name is required and must not exceed 20 characters")));
    }

    // Customer not found,passing nonexisting AccountId
    @Test
    public void updateCustomer_NonExistentAccountId_ShouldReturnBadRequest() throws Exception {
        CustomerRequest updateRequest = new CustomerRequest();
        updateRequest.setName("John");
        updateRequest.setEmail("john@example.com");
        updateRequest.setCountry(Country.US);
        updateRequest.setPostalCode("12345");
        updateRequest.setStatus(Status.ACTIVE);
        updateRequest.setAge(40);

        // Mock service to throw exception for invalid accountId
        when(customerService.updateCustomer(eq("AAGGHH"), any(Customer.class)))
            .thenThrow(new IllegalArgumentException("Customer not found for update"));

        mockMvc.perform(put("/api/customers/updateCustomer/AAGGHH")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Customer not found for update")));
    }

    // Invalid PIN
    @Test
    public void deleteCustomer_InvalidPin_ShouldReturnBadRequest() throws Exception {
        String accountId = "someAccountId";
        int wrongPin = 1111;

        // Mock service to throw exception for invalid PIN
        doThrow(new IllegalArgumentException("Invalid PIN"))
            .when(customerService).deleteCustomer(accountId, wrongPin);

        mockMvc.perform(delete("/api/customers/deleteCustomer")
                .param("accountId", accountId)
                .param("pin", String.valueOf(wrongPin)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid PIN")));
    }

    // not found by email or accountId
    @Test
    public void getCustomer_NotFound_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(get("/api/customers/getCustomer")
                .param("email", "mockdata@example.com"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Customer not found")));
    }

    //  customer not found
    @Test
    public void updateStatus_NonExistentCustomer_ShouldReturnBadRequest() throws Exception {
        CustomerStatusUpdateRequest request = new CustomerStatusUpdateRequest();
        request.setAccountId("AABBHH");
        request.setStatus(Status.ACTIVE);

        // Mock service to throw exception when updating non-existent customer
        doThrow(new IllegalArgumentException("Customer not found with accountId"))
            .when(customerService).updateStatus("AABBHH", Status.ACTIVE);

        mockMvc.perform(patch("/api/customers/updateStatus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Customer not found with accountId")));
    }

}
    
