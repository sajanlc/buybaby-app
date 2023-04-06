package com.example.babybuy.models;

public class ItemModel {
    public String id, name , qty , location ,createdDate , price   , description , locationName , locationLongitude , locationLatitude  ;
    public boolean isPurchased;
    public ItemModel(){

    }
    public ItemModel(String id ,String name, String qty, String createdDate, String price, String description , boolean isPurchased  , String locationName , String locationLongitude ,String  locationLatitude  ) {
        this.name = name;
        this.qty = qty;
        this.createdDate = createdDate;
        this.price = price;
        this.isPurchased = isPurchased;
        this.description = description;
        this.id = id;
        this.locationLongitude = locationLongitude;
        this.locationLatitude = locationLatitude;
        this.locationName = locationName;
    }
}
