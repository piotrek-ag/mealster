package net.pagier.mealster.resources.restaurant.controller;

import net.pagier.mealster.resources.restaurant.model.dto.RestaurantSearchResultDto;
import net.pagier.mealster.resources.restaurant.model.entity.FoodType;
import net.pagier.mealster.resources.restaurant.model.entity.Location;
import net.pagier.mealster.resources.restaurant.model.entity.Restaurant;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestaurantControllerTest {

    @LocalServerPort
    private int port;

    private String apiUrl;

    private final Path path = Path.of("test_output/exported_restaurants.xlsx");

    @Autowired
    private TestRestTemplate testRestTemplate;

    @BeforeAll
    void init() {
        apiUrl = "http://localhost:" + port + "/api/restaurants";
    }

    @Test
    void all() {
        var response = testRestTemplate
                .getRestTemplate()
                .exchange(apiUrl + "?page=0&size=1", HttpMethod.GET, null, new ParameterizedTypeReference<Page<Restaurant>>(){});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get().count()).isEqualTo(1);
        assertThat(response.getBody().getContent().get(0).getName()).isEqualTo("Chińska 2");
        assertThat(response.getBody().getContent().get(0).getLocation().getX()).isEqualTo(29130);
        assertThat(response.getBody().getContent().get(0).getLocation().getY()).isEqualTo(12334);
        assertThat(response.getBody().getContent().get(0).getFoodType()).isEqualTo(FoodType.CHINESE);
    }

    @Test
    void create() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Pierogarnia");
        Location location = new Location();
        location.setX(123);
        location.setY(123);
        restaurant.setLocation(location);
        restaurant.setFoodType(FoodType.POLISH);
        HttpEntity<Restaurant> httpEntity = new HttpEntity<>(restaurant);

        var response = testRestTemplate
                .getRestTemplate()
                .exchange(apiUrl, HttpMethod.POST, httpEntity, Restaurant.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(Restaurant.class);
        assertThat(response.getBody().getName()).isEqualTo("Pierogarnia");
        assertThat(response.getBody().getLocation().getX()).isEqualTo(123);
        assertThat(response.getBody().getLocation().getY()).isEqualTo(123);
        assertThat(response.getBody().getFoodType()).isEqualTo(FoodType.POLISH);
    }

    @Test
    void one() {
        var response = testRestTemplate
                .getRestTemplate()
                .exchange(apiUrl + "/2", HttpMethod.GET, null, Restaurant.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(Restaurant.class);
        assertThat(response.getBody().getName()).isEqualTo("Chińska 2");
        assertThat(response.getBody().getLocation().getX()).isEqualTo(29130);
        assertThat(response.getBody().getLocation().getY()).isEqualTo(12334);
        assertThat(response.getBody().getFoodType()).isEqualTo(FoodType.CHINESE);
    }

    @Test
    void update() {
        Restaurant restaurant = new Restaurant();
        restaurant.setName("Pierogarnia");
        Location location = new Location();
        location.setX(123);
        location.setY(123);
        restaurant.setLocation(location);
        restaurant.setFoodType(FoodType.POLISH);
        HttpEntity<Restaurant> httpEntity = new HttpEntity<>(restaurant);

        var response = testRestTemplate
                .getRestTemplate()
                .exchange(apiUrl + "/1", HttpMethod.PUT, httpEntity, Restaurant.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(Restaurant.class);
        assertThat(response.getBody().getName()).isEqualTo("Pierogarnia");
        assertThat(response.getBody().getLocation().getX()).isEqualTo(123);
        assertThat(response.getBody().getLocation().getY()).isEqualTo(123);
        assertThat(response.getBody().getFoodType()).isEqualTo(FoodType.POLISH);
    }

    @Test
    void delete() {
        var response = testRestTemplate
                .getRestTemplate()
                .exchange(apiUrl + "/1", HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void search() {
        var response = testRestTemplate
                .getRestTemplate()
                .exchange(apiUrl + "/search?x=123&y=123&foodType=ITALIAN&page=0&size=1",
                        HttpMethod.GET, null, new ParameterizedTypeReference<Page<RestaurantSearchResultDto>>(){});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get().count()).isEqualTo(1);
        assertThat(response.getBody().getContent().get(0).name()).isEqualTo("Włoska 3");
        assertThat(response.getBody().getContent().get(0).x()).isEqualTo(1415);
        assertThat(response.getBody().getContent().get(0).y()).isEqualTo(82182);
        assertThat(response.getBody().getContent().get(0).foodType()).isEqualTo("ITALIAN");
    }

    @Test
    void exportXlsx() throws IOException {
        var response = testRestTemplate
                .getRestTemplate()
                .exchange(apiUrl + "/export?x=123&y=123&foodType=ITALIAN",
                        HttpMethod.GET, null, byte[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        byte[] body = response.getBody();
        Files.write(path, body);
        assertTrue(Files.exists(path));
    }

    @AfterAll
    public void deleteOutputFile() throws IOException {
        Files.delete(path);
    }
}