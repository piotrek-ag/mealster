package net.pagier.mealster.resources.restaurant.model.entity;

import lombok.Getter;
import lombok.Setter;
import net.pagier.mealster.resources.restaurant.model.dto.RestaurantSearchResultDto;

import javax.persistence.*;

@NamedNativeQueries({
        @NamedNativeQuery(name = "searchWithFoodType",
                query = "select * from (select name, x, y, food_type, cast(abs(x - :x) as bigint) + cast(abs(y - :y) as bigint) as distance " +
                        "from restaurant where food_type = :foodType order by distance asc) " +
                        "union all " +
                        "select * from (select name, x, y, food_type, cast(abs(x - :x) as bigint) + cast(abs(y - :y) as bigint) as distance " +
                        "from restaurant where food_type != :foodType order by distance asc)",
                resultSetMapping = "RestaurantSearchResultDto"),
        @NamedNativeQuery(name = "searchWoFoodType",
                query = "select name, x, y, food_type, cast(abs(x - :x) as bigint) + cast(abs(y - :y) as bigint) as distance " +
                        "from restaurant order by distance asc",
                resultSetMapping = "RestaurantSearchResultDto")
})
@SqlResultSetMapping(name = "RestaurantSearchResultDto",
        classes = @ConstructorResult(targetClass = RestaurantSearchResultDto.class,
                columns = {
                        @ColumnResult(name = "name"),
                        @ColumnResult(name = "x"),
                        @ColumnResult(name = "y"),
                        @ColumnResult(name = "food_type"),
                        @ColumnResult(name = "distance")
                }))
@Entity
@Getter
@Setter
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @Embedded
    private Location location;

    @Column(name = "FOOD_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodType foodType;
}
