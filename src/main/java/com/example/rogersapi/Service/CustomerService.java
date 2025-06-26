package com.example.rogersapi.Service;
import com.example.rogersapi.RogersCustomerAccountManagementH2Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.rogersapi.Dao.CustomerRepository;
import com.example.rogersapi.Dto.LocationDto;
import com.example.rogersapi.Dto.ZippopotamResponse;
import com.example.rogersapi.Dto.getCustomerResponse;
import com.example.rogersapi.Model.Country;
import com.example.rogersapi.Model.Customer;
import com.example.rogersapi.Model.Status;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service

public class CustomerService {

    private final RogersCustomerAccountManagementH2Application rogersCustomerAccountManagementH2Application;

    
	
	@Autowired
	private CustomerRepository repository;
	
	private static final String AlphaNumeric="ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
	private static final SecureRandom randomNumber = new SecureRandom();


    CustomerService(RogersCustomerAccountManagementH2Application rogersCustomerAccountManagementH2Application) {
        this.rogersCustomerAccountManagementH2Application = rogersCustomerAccountManagementH2Application;
    }

   

	public Customer saveCustomer(Customer customer) {
		validateInputsWhileSave(customer);
		populatePlaceDetails(customer);
		customerNameValidation(customer.getName());
		customer.setAccountId(customerAccountIdGeneration());
		customer.setPin(customerAccountPinGeneration());
		customer.setStatus(Status.ACTIVE);

				
		return repository.save(customer);
	}
	
	//Customer Name Validation like Null check and length check
		public void customerNameValidation(String name) {
			if(name == null || name.length() > 20) {
				throw new IllegalArgumentException("Name is required and must not exceed 20 charcters");
			}
			
		}
	private void validateInputsWhileSave(Customer customer) {
			customerNameValidation(customer.getName());
		 	if (customer.getEmail() == null || !customer.getEmail().matches("^.+@.+\\..+$"))
	            throw new IllegalArgumentException("A valid email is required");

		 	Optional<Customer> existing = repository.findByEmail(customer.getEmail());
	        if (existing.isPresent())
	            throw new IllegalArgumentException("Email already exists");

	        if (customer.getCountry() == null)
	            throw new IllegalArgumentException("Country is required and must be one of US, DE, ES, FR");

	        if (customer.getPostalCode() == null || !customer.getPostalCode().matches("\\d{5}"))
	            throw new IllegalArgumentException("Postal code must be 5 digits");

	        if (customer.getStatus() != Status.REQUESTED)
	            throw new IllegalArgumentException("Only 'Requested' status is allowed on create");

	}
	private void populatePlaceDetails(Customer customer) {
		 String url = String.format("https://api.zippopotam.us/%s/%s", customer.getCountry().name().toLowerCase(Locale.ROOT), customer.getPostalCode());
		 RestTemplate rt = new RestTemplate();
		 ZippopotamResponse resp = rt.getForObject(url, ZippopotamResponse.class);
		 
		 if (resp == null || resp.getPlaces().isEmpty()) {
			 throw new IllegalArgumentException("Invalid postal code or country for location lookup");
		 }
		 
		 ZippopotamResponse.Place place = resp.getPlaces().get(0);
		 customer.setPlaceName(place.getPlaceName());
		 customer.setStateAbbreviation(place.getStateAbbreviation());
		 customer.setLatitude(place.getLatitude());
		 customer.setLongitude(place.getLongitude());
	}

	//Going to create an random AccountId which is in Alphanumeric form
	public String customerAccountIdGeneration() {
		StringBuilder sb= new StringBuilder(6);
		for(int i=0;i<6;i++)
		{
			sb.append(AlphaNumeric.charAt(randomNumber.nextInt(AlphaNumeric.length())));
		}
		return sb.toString();
	}
  //Going to create a random 4 digit pin fro customer
	public int customerAccountPinGeneration() {
	    return randomNumber.nextInt(10000);
	}
	
//	public Optional<Customer> getByEmailOrAccountId(String email, String accountId) {
//        if (email != null) return repository.findByEmail(email);
//        if (accountId != null) return repository.findByAccountId(accountId);
//        return Optional.empty();
//    }

	//Input Validation for update operation
	private void validateUpdateFields(Customer existing, Customer updates) {
	    if (updates.getName() == null || updates.getName().isBlank() || updates.getName().length() > 20) {
	        throw new IllegalArgumentException("Name is required and must be max 20 characters");
	    }

	    if (updates.getEmail() == null || !updates.getEmail().matches("^.+@.+\\..+$")) {
	        throw new IllegalArgumentException("A valid email is required");
	    }

	    // If email is changed, ensure it's unique
	    if (!updates.getEmail().equalsIgnoreCase(existing.getEmail())) {
	        Optional<Customer> emailCheck = repository.findByEmail(updates.getEmail());
	        if (emailCheck.isPresent()) {
	            throw new IllegalArgumentException("Email already exists");
	        }
	    }

	    if (updates.getCountry() == null || !EnumSet.of(Country.US, Country.DE, Country.ES, Country.FR).contains(updates.getCountry())) {
	        throw new IllegalArgumentException("Country must be one of US, DE, ES, FR");
	    }

	    if (updates.getPostalCode() == null || !updates.getPostalCode().matches("\\d{5}")) {
	        throw new IllegalArgumentException("Postal code must be exactly 5 digits");
	    }

	    if (updates.getStatus() == null || updates.getStatus() != Status.ACTIVE) {
	        throw new IllegalArgumentException("Only ACTIVE status is allowed for updates");
	    }
	}
		
	public Customer updateCustomer(String accountId, Customer updates) {
	    
	    Customer existing = repository.findByAccountId(accountId).orElseThrow(() ->
	        new IllegalArgumentException("Customer not found for update"));

	    
	    if (!Status.ACTIVE.equals(existing.getStatus())) {
	        throw new IllegalArgumentException("Only ACTIVE accounts can be updated");
	    }

	   //Funtion call to validate the update fields
	    validateUpdateFields(existing, updates);

	    
	    boolean locationChanged = false;
	    if (!existing.getCountry().equals(updates.getCountry())) {
	        existing.setCountry(updates.getCountry());
	        locationChanged = true;
	    }
	    if (!existing.getPostalCode().equals(updates.getPostalCode())) {
	        existing.setPostalCode(updates.getPostalCode());
	        locationChanged = true;
	    }
	    if (locationChanged) {
	        populatePlaceDetails(existing); // API call to fetch Place details .
	    }

	    
	    existing.setName(updates.getName());
	    existing.setEmail(updates.getEmail());
	    existing.setAge(updates.getAge());
	    
	    return repository.save(existing);
	}

    
    
    //Update Customer status , its not a put function its a patch function
    
    public void updateStatus(String accountId, Status status) {
        Customer customer = repository.findByAccountId(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with accountId: " + accountId));

        customer.setStatus(status);
        repository.save(customer);
    }
    
    //Delete customer

    public void deleteCustomer(String accountId, int pin) {
        Customer existing = repository.findByAccountId(accountId).orElseThrow(() ->
                new IllegalArgumentException("Customer not found for deletion"));

        if (!Status.INACTIVE.equals(existing.getStatus())) {
            throw new IllegalArgumentException("Only INACTIVE accounts can be deleted");
        }

        if (existing.getPin() != pin) {
            throw new IllegalArgumentException("Invalid PIN");
        }

        repository.delete(existing);
    }
    
    //getcustomer details and also the count by country state and place
    
    public Map<String, Object> getCustomerCountByCountryStatePlace(Country country) {
        List<Object[]> raw = repository.countByCountryGroupedByStateAndPlace(country);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("country", country.name());

        Map<String, Map<String, Integer>> stateToPlaces = new LinkedHashMap<>();
        for (Object[] row : raw) {
            String state = (String) row[0];
            String place = (String) row[1];
            Long count = (Long) row[2];

            stateToPlaces.putIfAbsent(state, new LinkedHashMap<>());
            stateToPlaces.get(state).put(place, count.intValue());
        }

        List<Map<String, Object>> states = new ArrayList<>(); 
        for (Map.Entry<String, Map<String, Integer>> stateEntry : stateToPlaces.entrySet()) {
            Map<String, Object> stateMap = new LinkedHashMap<>(); 
            stateMap.put("state", stateEntry.getKey());
            List<Map<String, Object>> places = new ArrayList<>(); 

            int stateCount = 0;
            for (Map.Entry<String, Integer> placeEntry : stateEntry.getValue().entrySet()) {
                Map<String, Object> placeMap = new LinkedHashMap<>(); 
                placeMap.put("place", placeEntry.getKey());
                placeMap.put("count", placeEntry.getValue());
                places.add(placeMap);
                stateCount += placeEntry.getValue();
            }

            stateMap.put("count", stateCount);
            stateMap.put("places", places);
            states.add(stateMap);
        }

        result.put("count", states.stream().mapToInt(s -> (int) s.get("count")).sum());
        result.put("states", states);
        

        return result;
    }

    
    //get customer details by email or account id
    
    public Optional<getCustomerResponse> getCustomerResponseByEmailOrAccountId(String email, String accountId) {
		 Optional<Customer> optCustomer = Optional.empty();
		    
		    if (email != null) {
		        optCustomer = repository.findByEmail(email);
		    } else if (accountId != null) {
		        optCustomer = repository.findByAccountId(accountId);
		    }

		    return optCustomer.map(c -> new getCustomerResponse(
		        c.getAccountId(),
		        c.getEmail(),
		        c.getStatus(),
		        c.getAge(),
		        new LocationDto(
		            c.getPlaceName(),
		            c.getStateAbbreviation(),
		            c.getCountry(),
		            c.getPostalCode(),
		            c.getLongitude(),
		            c.getLatitude()
		        )
		    ));
	}

    
}


