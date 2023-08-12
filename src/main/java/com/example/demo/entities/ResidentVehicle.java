package com.example.demo.entities;

import com.example.demo.enums.VehicleEnum;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("RESIDENT")
@NoArgsConstructor
@Getter
@Setter
public class ResidentVehicle extends Vehicle{
    private long accumulatedTime;

    private static final BigDecimal tax = BigDecimal.valueOf(0.05);

    public ResidentVehicle(String licensePlate) {
        super(licensePlate, VehicleEnum.RESIDENT);
    }

    @Override
    public BigDecimal calculateParkingFee() {
        return millisecondsToMinutes(accumulatedTime).multiply(tax);
    }

    public void addStayTimeToAccumulatedTime(){
        this.accumulatedTime += calculateDurationOfStay();
    }

    public void clearAccumulatedTime(){
        this.accumulatedTime = 0;
    }

}
