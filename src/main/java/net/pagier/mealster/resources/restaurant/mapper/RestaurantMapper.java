package net.pagier.mealster.resources.restaurant.mapper;

import net.pagier.mealster.resources.restaurant.model.entity.Restaurant;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RestaurantMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(source = "name", target = "name"),
            @Mapping(source = "location", target = "location"),
            @Mapping(source = "foodType", target = "foodType"),
    })
    void update(@MappingTarget Restaurant target, Restaurant source);
}
