package com.affirm.geocoding.model;

import java.util.List;

public class GeocodingResult {

    private String place_id;
    private List<String> types;
    private String formatted_address;
    private List<AddressComponents> address_components;
    private Geometry geometry;

    public static class AddressComponents{
        private String long_name;
        private String short_name;
        private List<String> types;

        public String getLong_name() {
            return long_name;
        }

        public void setLong_name(String long_name) {
            this.long_name = long_name;
        }

        public String getShort_name() {
            return short_name;
        }

        public void setShort_name(String short_name) {
            this.short_name = short_name;
        }

        public List<String> getTypes() {
            return types;
        }

        public void setTypes(List<String> types) {
            this.types = types;
        }
    }

    public static class Geometry{
        private GeoLocation location;
        private String location_type;
        private GeoViewport viewport;

        public GeoLocation getLocation() {
            return location;
        }

        public void setLocation(GeoLocation location) {
            this.location = location;
        }

        public String getLocation_type() {
            return location_type;
        }

        public void setLocation_type(String location_type) {
            this.location_type = location_type;
        }

        public GeoViewport getViewport() {
            return viewport;
        }

        public void setViewport(GeoViewport viewport) {
            this.viewport = viewport;
        }
    }

    public static class GeoLocation{
        private Double lat;
        private Double lng;

        public Double getLat() {
            return lat;
        }

        public void setLat(Double lat) {
            this.lat = lat;
        }

        public Double getLng() {
            return lng;
        }

        public void setLng(Double lng) {
            this.lng = lng;
        }
    }

    public static class GeoViewport{
        private GeoLocation northeast;
        private GeoLocation southwest;

        public GeoLocation getNortheast() {
            return northeast;
        }

        public void setNortheast(GeoLocation northeast) {
            this.northeast = northeast;
        }

        public GeoLocation getSouthwest() {
            return southwest;
        }

        public void setSouthwest(GeoLocation southwest) {
            this.southwest = southwest;
        }
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public List<AddressComponents> getAddress_components() {
        return address_components;
    }

    public void setAddress_components(List<AddressComponents> address_components) {
        this.address_components = address_components;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
