package net.pagier.mealster.resources.restaurant.model.dto;

import java.math.BigInteger;

public record RestaurantSearchResultDto(String name, Integer x, Integer y, String foodType, BigInteger distance) {}
