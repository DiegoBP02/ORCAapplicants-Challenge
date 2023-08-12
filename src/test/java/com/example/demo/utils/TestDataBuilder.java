package com.example.demo.utils;

import com.example.demo.dtos.VehicleDTO;
import com.example.demo.entities.NonResidentVehicle;
import com.example.demo.entities.OfficialVehicle;
import com.example.demo.entities.ResidentVehicle;
import com.example.demo.entities.Vehicle;
import com.example.demo.enums.VehicleEnum;

public class TestDataBuilder {

    private static final VehicleEnum defaultVehicleEnum = VehicleEnum.RESIDENT;
    private static final String defaultLicensePlate = "123";

    public static Vehicle buildVehicleNoId(){
        return createVehicle(defaultVehicleEnum, defaultLicensePlate);
    }

    public static Vehicle buildVehicleNoId(VehicleEnum vehicleEnum, String licensePlate){
        return createVehicle(vehicleEnum, licensePlate);
    }

    public static VehicleDTO buildVehicleDTO(){
        return VehicleDTO.builder()
                .vehicleEnum(String.valueOf(defaultVehicleEnum))
                .licensePlate(defaultLicensePlate)
                .build();
    }

    public static VehicleDTO buildVehicleDTO(VehicleEnum vehicleEnum, String licensePlate){
        return VehicleDTO.builder()
                .vehicleEnum(String.valueOf(vehicleEnum))
                .licensePlate(licensePlate)
                .build();
    }

    private static Vehicle createVehicle(VehicleEnum vehicleEnum, String licensePlate) {
        return switch (vehicleEnum) {
            case OFFICIAL -> new OfficialVehicle(licensePlate);
            case RESIDENT -> new ResidentVehicle(licensePlate);
            case NON_RESIDENT -> new NonResidentVehicle(licensePlate);
            default -> throw new IllegalArgumentException("Invalid vehicle type");
        };
    }
}
