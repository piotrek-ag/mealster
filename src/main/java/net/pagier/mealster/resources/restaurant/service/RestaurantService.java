package net.pagier.mealster.resources.restaurant.service;

import net.pagier.mealster.resources.restaurant.dao.RestaurantRepository;
import net.pagier.mealster.resources.restaurant.exception.RestaurantNotFoundException;
import net.pagier.mealster.resources.restaurant.mapper.RestaurantMapper;
import net.pagier.mealster.resources.restaurant.model.dto.RestaurantSearchResultDto;
import net.pagier.mealster.resources.restaurant.model.entity.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.OutputStream;
import java.util.List;

@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper mapper;
    private final RestaurantWorkbookWriter restaurantWorkbookWriter;

    @Autowired
    public RestaurantService(RestaurantRepository restaurantRepository, RestaurantMapper mapper, RestaurantWorkbookWriter restaurantWorkbookWriter) {
        this.restaurantRepository = restaurantRepository;
        this.mapper = mapper;
        this.restaurantWorkbookWriter = restaurantWorkbookWriter;
    }

    @Transactional(readOnly = true)
    public Page<Restaurant> all(Pageable pageable) {
        return restaurantRepository.findAll(pageable);
    }

    @Transactional
    public Restaurant create(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }

    @Transactional(readOnly = true)
    public Restaurant one(Long id) {
        return restaurantRepository
                .findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException(id));
    }

    @Transactional
    public Restaurant update(Long id, Restaurant inputRestaurant) {
        Restaurant restaurant = restaurantRepository.findById(id).orElseGet(Restaurant::new);
        mapper.update(restaurant, inputRestaurant);
        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public void delete(Long id) {
        restaurantRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantSearchResultDto> search(Integer x, Integer y, String foodType, Pageable pageable) {
        return foodType != null
                ? new PageImpl<>(restaurantRepository.searchWithFoodType(x, y, foodType, pageable)) :
                new PageImpl<>(restaurantRepository.searchWoFoodType(x, y, pageable));
    }

    @Transactional(readOnly = true)
    public void export(Integer x, Integer y, String foodType, OutputStream out) {
        List<RestaurantSearchResultDto> results = foodType != null
                ? restaurantRepository.searchWithFoodType(x, y, foodType, Pageable.unpaged()) :
                restaurantRepository.searchWoFoodType(x, y, Pageable.unpaged());
        restaurantWorkbookWriter.write(results, out);
    }
}
