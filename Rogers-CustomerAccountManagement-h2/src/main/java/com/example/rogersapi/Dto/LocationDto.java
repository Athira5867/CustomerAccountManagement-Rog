package com.example.rogersapi.Dto;

import com.example.rogersapi.Model.Country;

public class LocationDto {
	
	private String place;
    private String state;
    private Country country;
    
    public LocationDto(String place, String state, Country country, String postalCode, String longitude, String latitude) {
        this.place = place;
        this.state = state;
        this.country = country;
        this.postalCode = postalCode;
        this.longitude = longitude;
        this.latitude = latitude;
    }
    public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Country getCountry() {
		return country;
	}
	public void setCountry(Country country) {
		this.country = country;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	private String postalCode;
    private String longitude;
    private String latitude;

}
