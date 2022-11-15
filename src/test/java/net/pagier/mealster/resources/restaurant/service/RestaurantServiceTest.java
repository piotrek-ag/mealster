package net.pagier.mealster.resources.restaurant.service;

import net.pagier.mealster.resources.restaurant.dao.RestaurantRepository;
import net.pagier.mealster.resources.restaurant.exception.RestaurantNotFoundException;
import net.pagier.mealster.resources.restaurant.mapper.RestaurantMapper;
import net.pagier.mealster.resources.restaurant.model.dto.RestaurantSearchResultDto;
import net.pagier.mealster.resources.restaurant.model.entity.FoodType;
import net.pagier.mealster.resources.restaurant.model.entity.Location;
import net.pagier.mealster.resources.restaurant.model.entity.Restaurant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.OutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(MockitoExtension.class)
class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantMapper mapper;

    @Mock
    private RestaurantWorkbookWriter restaurantWorkbookWriter;

    @Captor
    private ArgumentCaptor<Long> longArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<Integer> integerArgumentCaptor;

    @Captor
    private ArgumentCaptor<Pageable> pageableArgumentCaptor;

    @Captor
    private ArgumentCaptor<List<RestaurantSearchResultDto>> resultsListArgumentCaptor;

    @Captor
    private ArgumentCaptor<OutputStream> outputStreamArgumentCaptor;

    @InjectMocks
    RestaurantService restaurantService;

    @Test
    void all() {
        Page<Restaurant> restaurantsPaged = new PageImpl<>(List.of(new Restaurant()), Pageable.unpaged(), 0);
        when(restaurantRepository.findAll(isA(Pageable.class))).thenReturn(restaurantsPaged);
        Page<Restaurant> actual = restaurantService.all(Pageable.unpaged());
        assertNotNull(actual.getContent());
    }

    @Test
    void create() {
        Restaurant restaurant = new Restaurant();
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurant);
        var actual = restaurantService.create(restaurant);
        assertEquals(actual, restaurant);
    }

    @Test
    void one_shouldReturnRestaurant() {
        Restaurant restaurant = new Restaurant();
        when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.of(restaurant));
        var result = restaurantService.one(1L);
        assertEquals(result, restaurant);
    }

    @Test
    void one_shouldThrowException() {
        when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(RestaurantNotFoundException.class, () -> restaurantService.one(1L));
    }

    @Test
    void update() {
        Restaurant restaurantOne = new Restaurant();
        restaurantOne.setName("Polskie Jadło");
        Location locationOne = new Location();
        locationOne.setX(123);
        locationOne.setY(123);
        restaurantOne.setLocation(locationOne);
        restaurantOne.setFoodType(FoodType.POLISH);

        Restaurant restaurantTwo = new Restaurant();
        restaurantTwo.setName("Karczma");
        Location locationTwo = new Location();
        locationTwo.setX(456);
        locationTwo.setY(456);
        restaurantTwo.setLocation(locationTwo);
        restaurantTwo.setFoodType(FoodType.POLISH);

        when(restaurantRepository.findById(any(Long.class))).thenReturn(Optional.of(restaurantOne));
        when(restaurantRepository.save(any(Restaurant.class))).thenReturn(restaurantTwo);
        var result = restaurantService.update(1L, restaurantTwo);

        assertEquals("Karczma", result.getName());
        assertEquals(456 , result.getLocation().getX());
        assertEquals(456 , result.getLocation().getY());
        assertEquals(FoodType.POLISH , result.getFoodType());
    }

    @Test
    void delete() {
        restaurantService.delete(1L);
        Mockito.verify(restaurantRepository).deleteById(longArgumentCaptor.capture());
    }

    @Test
    void search_withFoodType() {
        List<RestaurantSearchResultDto> restaurants = List.of(new RestaurantSearchResultDto("Italian 1", 123, 123, "ITALIAN", BigInteger.ONE));
        when(restaurantRepository.searchWithFoodType(any(Integer.class), any(Integer.class), anyString(), isA(Pageable.class))).thenReturn(restaurants);
        var result = restaurantService.search(123, 123, "ITALIAN", Pageable.unpaged());
        Mockito.verify(restaurantRepository).searchWithFoodType(integerArgumentCaptor.capture(), integerArgumentCaptor.capture(), stringArgumentCaptor.capture(), pageableArgumentCaptor.capture());
        assertEquals("Italian 1", result.getContent().get(0).name());
        assertEquals(123 , result.getContent().get(0).x());
        assertEquals(123 , result.getContent().get(0).y());
        assertEquals("ITALIAN" , result.getContent().get(0).foodType());
        assertEquals(BigInteger.ONE , result.getContent().get(0).distance());
    }


    @Test
    void search_woFoodType() {
        List<RestaurantSearchResultDto> restaurants = List.of(new RestaurantSearchResultDto("Italian 1", 123, 123, "ITALIAN", BigInteger.ONE));
        when(restaurantRepository.searchWoFoodType(any(Integer.class), any(Integer.class), isA(Pageable.class))).thenReturn(restaurants);
        var result = restaurantService.search(123, 123, null, Pageable.unpaged());
        Mockito.verify(restaurantRepository).searchWoFoodType(integerArgumentCaptor.capture(), integerArgumentCaptor.capture(), pageableArgumentCaptor.capture());
        assertEquals("Italian 1", result.getContent().get(0).name());
        assertEquals(123 , result.getContent().get(0).x());
        assertEquals(123 , result.getContent().get(0).y());
        assertEquals("ITALIAN" , result.getContent().get(0).foodType());
        assertEquals(BigInteger.ONE , result.getContent().get(0).distance());
    }

    @Test
    void export_withFoodType() {
        List<RestaurantSearchResultDto> restaurants =
                List.of(new RestaurantSearchResultDto("Italian 1", 123, 123, "ITALIAN", BigInteger.ONE));
        when(restaurantRepository.searchWithFoodType(any(Integer.class), any(Integer.class), anyString(), isA(Pageable.class)))
                .thenReturn(restaurants);
        doNothing().when(restaurantWorkbookWriter).write(anyList(), any(OutputStream.class));

        restaurantService.export(123, 123, "ITALIAN", OutputStream.nullOutputStream());
        Mockito.verify(restaurantRepository)
                .searchWithFoodType(integerArgumentCaptor.capture(), integerArgumentCaptor.capture(), stringArgumentCaptor.capture(), pageableArgumentCaptor.capture());
        Mockito.verify(restaurantWorkbookWriter)
                .write(resultsListArgumentCaptor.capture(), outputStreamArgumentCaptor.capture());
    }

    @Test
    void export_woFoodType() {
        List<RestaurantSearchResultDto> restaurants =
                List.of(new RestaurantSearchResultDto("Italian 1", 123, 123, "ITALIAN", BigInteger.ONE));
        when(restaurantRepository.searchWoFoodType(any(Integer.class), any(Integer.class), isA(Pageable.class)))
                .thenReturn(restaurants);
        doNothing().when(restaurantWorkbookWriter).write(anyList(), any(OutputStream.class));

        restaurantService.export(123, 123, null, OutputStream.nullOutputStream());
        Mockito.verify(restaurantRepository)
                .searchWoFoodType(integerArgumentCaptor.capture(), integerArgumentCaptor.capture(), pageableArgumentCaptor.capture());
        Mockito.verify(restaurantWorkbookWriter)
                .write(resultsListArgumentCaptor.capture(), outputStreamArgumentCaptor.capture());
    }
}
