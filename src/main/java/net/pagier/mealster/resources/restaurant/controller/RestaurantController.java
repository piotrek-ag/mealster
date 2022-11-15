package net.pagier.mealster.resources.restaurant.controller;

import lombok.extern.log4j.Log4j2;
import net.pagier.mealster.resources.restaurant.model.dto.RestaurantSearchResultDto;
import net.pagier.mealster.resources.restaurant.model.entity.Restaurant;
import net.pagier.mealster.resources.restaurant.service.RestaurantService;
import net.pagier.mealster.util.ResponseFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log4j2
@RestController
@RequestMapping("/restaurants")
public class RestaurantController {
    private static final String ID = "/{id:[0-9]+}";
    private static final String EXPORT = "/export";
    private static final String SEARCH = "/search";
    private static final String EXPORT_FILENAME = "exported_restaurants";

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    Page<Restaurant> all(final @PageableDefault(size = 5) Pageable pageable) {
        return restaurantService.all(pageable);
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Restaurant create(@RequestBody @Valid Restaurant restaurant) {
        return restaurantService.create(restaurant);
    }

    @GetMapping(value = ID, produces = APPLICATION_JSON_VALUE)
    public Restaurant one(@PathVariable("id") Long id) {
        return restaurantService.one(id);
    }

    @PutMapping(value = ID, produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Restaurant update(@PathVariable("id") Long id,
                                       @RequestBody Restaurant restaurant) {
        return restaurantService.update(id, restaurant);
    }

    @DeleteMapping(value = ID, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        try {
            restaurantService.delete(id);
        } catch (EmptyResultDataAccessException ex) {
            LOG.info("No Restaurant with id {} found", id);
        }
    }

    @GetMapping(value = SEARCH, produces = APPLICATION_JSON_VALUE)
    public Page<RestaurantSearchResultDto> search(@RequestParam Integer x,
                                                  @RequestParam Integer y,
                                                  @RequestParam(required = false) String foodType,
                                                  Pageable pageable) {
        return restaurantService.search(x, y, foodType, pageable);
    }

    @GetMapping(value = EXPORT)
    public void exportXlsx(@RequestParam Integer x,
                           @RequestParam Integer y,
                           @RequestParam(required = false) String foodType,
                           HttpServletResponse response) {
        ResponseFormatter.formatResponseForXlsx(response, EXPORT_FILENAME);
        try (OutputStream out = response.getOutputStream()) {
            restaurantService.export(x, y, foodType, out);
        } catch (IOException ex) {
            LOG.error("Output stream could not be handled", ex);
        }
    }
}
