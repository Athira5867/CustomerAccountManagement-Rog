package com.example.rogersapi.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Country {
	
	 US, DE, ES, FR;

	 @JsonCreator
	    public static Country fromString(String key) {
	        if (key == null) return null;
	        for (Country c : Country.values()) {
	            if (c.name().equalsIgnoreCase(key)) {
	                return c;
	            }
	        }
	        throw new IllegalArgumentException("Invalid country code. Allowed values: US, DE, ES, FR");
	    }

	    @JsonValue
	    public String toValue() {
	        return this.name();
	    }
}
