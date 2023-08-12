package com.example.demo.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Calendar;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "official_vehicle_id")
    private OfficialVehicle officialVehicle;

    private Calendar entryTime;
    private Calendar exitTime;
    private BigDecimal durationOfStay;
}
