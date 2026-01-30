package com.karan.admin_sunset_point.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "dishes")
public class Dish {

    @PrimaryKey(autoGenerate = true)
    public int dish_id;

    @NonNull
    public String dish_name;

    @NonNull
    public String category;

    public int price;
}
