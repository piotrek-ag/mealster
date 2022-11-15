package net.pagier.mealster.resources.restaurant.dao;

import net.pagier.mealster.resources.restaurant.model.dto.RestaurantSearchResultDto;
import net.pagier.mealster.resources.restaurant.model.entity.Restaurant;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends PagingAndSortingRepository<Restaurant, Long> {

    @Query(nativeQuery = true, name = "searchWithFoodType")
    List<RestaurantSearchResultDto> searchWithFoodType(@Param("x") Integer x, @Param("y") Integer y, @Param("foodType") String foodType, Pageable pageable);

    @Query(nativeQuery = true, name = "searchWoFoodType")
    List<RestaurantSearchResultDto> searchWoFoodType(@Param("x") Integer x, @Param("y") Integer y, Pageable pageable);
}
