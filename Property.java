package com.RealState.model;

import java.util.ArrayList;
import java.util.List;

public class Property {
    public String propertyId;
    public String agentId;
    public String title;
    public String description;
    public String type;
    public double price;
    public String location;
    public int bedrooms;
    public int bathrooms;
    public double squareFeet;
    public String[] amenities;
    public String status;
    public String createdDate;
    public String updatedDate;
    public String[] images;
    public boolean featured;
}

// Make PropertyData public and separate class
class PropertyData {
    public List<Property> properties;

    public PropertyData() {
        properties = new ArrayList<>();
    }
}