package com.example.demo.entities;

import com.example.demo.enums.VehicleEnum;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("OFFICIAL")
@NoArgsConstructor
@Getter
@Setter
public class OfficialVehicle extends Vehicle {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "official_vehicle_id")
    private List<Stay> stayList = new ArrayList<>();

    public OfficialVehicle(String licensePlate) {
        super(licensePlate, VehicleEnum.OFFICIAL);
    }

    @Override
    public BigDecimal calculateParkingFee() {
        return BigDecimal.ZERO;
    }

    public void addStay() {
        Stay stay = Stay.builder()
                .entryTime(getEntryTime())
                .exitTime(getExitTime())
                .durationOfStay(millisecondsToMinutes(calculateDurationOfStay()))
                .officialVehicle(this)
                .build();
        stayList.add(stay);

        clearTimes();
    }

    public void clearStayList() {
        this.stayList = null;
    }

}
