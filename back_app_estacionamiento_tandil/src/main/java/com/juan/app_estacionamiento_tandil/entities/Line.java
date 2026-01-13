package com.juan.app_estacionamiento_tandil.entities;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "lines",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "id")
        }
)
public class Line {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String color;


    protected Line() {
    }

}
