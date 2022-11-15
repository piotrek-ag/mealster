package net.pagier.mealster.resources.restaurant.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
public class Location {

    @Column(nullable = false)
    private Integer x;

    @Column(nullable = false)
    private Integer y;
}
