package com.example.rogersapi.Model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
	    REQUESTED,
	    ACTIVE,
	    INACTIVE;
	
	@JsonCreator
    public static Status fromString(String key) {
        if (key == null) return null;
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(key)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Invalid status. Allowed values: REQUESTED, ACTIVE, INACTIVE");
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
