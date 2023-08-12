package com.example.demo.entities;

import com.example.demo.enums.VehicleEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicle_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String licensePlate;
    private Calendar entryTime;
    private Calendar exitTime;
    private VehicleEnum vehicleEnum;

    public Vehicle(String licensePlate, VehicleEnum vehicleEnum) {
        this.licensePlate = licensePlate;
        this.vehicleEnum = vehicleEnum;
    }

    public abstract BigDecimal calculateParkingFee();

    public long calculateDurationOfStay() {
        if (entryTime != null && exitTime != null) {
            return exitTime.getTimeInMillis() - entryTime.getTimeInMillis();
        } else {
            throw new IllegalStateException("Entry or exit time not set");
        }
    }

    public void setEntryTimeCurrentTime() {
        this.entryTime = Calendar.getInstance();
    }

    public void setExitTimeCurrentTime() {
        this.exitTime = Calendar.getInstance();
    }

    public void clearTimes() {
        this.entryTime = null;
        this.exitTime = null;
    }

    public static BigDecimal millisecondsToMinutes(long milliseconds){
        return BigDecimal.valueOf(milliseconds)
                .divide(BigDecimal.valueOf(1000 * 60), 2, RoundingMode.HALF_UP);
    }

}
