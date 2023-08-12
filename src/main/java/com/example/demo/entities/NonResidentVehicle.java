package com.example.demo.entities;

import com.example.demo.enums.VehicleEnum;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("NON_RESIDENT")
@NoArgsConstructor
@Getter
@Setter
public class NonResidentVehicle extends Vehicle {
    private static final BigDecimal tax = BigDecimal.valueOf(0.5);

    public NonResidentVehicle(String licensePlate) {
        super(licensePlate, VehicleEnum.NON_RESIDENT);
    }

    @Override
    public BigDecimal calculateParkingFee() {
        BigDecimal parkingFee = millisecondsToMinutes(calculateDurationOfStay()).multiply(tax);
        setEntryTime(null);
        setExitTime(null);
        return parkingFee;
    }

}
