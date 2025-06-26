package com.example.rogersapi.Dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZippopotamResponse {
    private String country;

    @JsonProperty("places")
    private List<Place> places;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<Place> places) {
        this.places = places;
    }

    public static class Place {
        @JsonProperty("place name")
        private String placeName;

        @JsonProperty("state abbreviation")
        private String stateAbbreviation;

        private String latitude;
        private String longitude;

        public String getPlaceName() {
            return placeName;
        }

        public void setPlaceName(String placeName) {
            this.placeName = placeName;
        }

        public String getStateAbbreviation() {
            return stateAbbreviation;
        }

        public void setStateAbbreviation(String stateAbbreviation) {
            this.stateAbbreviation = stateAbbreviation;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }
}
