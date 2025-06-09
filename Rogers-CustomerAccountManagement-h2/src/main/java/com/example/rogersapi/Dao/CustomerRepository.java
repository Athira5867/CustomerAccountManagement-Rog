package com.example.rogersapi.Dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.rogersapi.Model.Country;
import com.example.rogersapi.Model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
	
	
	
	 Optional<Customer> findByEmail(String email);
	 Optional<Customer> findByAccountId(String accountId);
	 
	 @Query("SELECT c FROM Customer c WHERE c.country = :country")
	    List<Customer> findByCountry(@Param("country") Country country);

	    @Query("SELECT c.stateAbbreviation as state, c.placeName as place, COUNT(c) as count " +
	           "FROM Customer c WHERE c.country = :country " +
	           "GROUP BY c.stateAbbreviation, c.placeName ORDER BY c.stateAbbreviation, c.placeName")
	    List<Object[]> countByCountryGroupedByStateAndPlace(@Param("country") Country country);

}
