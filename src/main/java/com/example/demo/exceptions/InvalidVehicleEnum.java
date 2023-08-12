package com.example.demo.exceptions;

import com.example.demo.enums.VehicleEnum;

public class InvalidVehicleEnum extends RuntimeException{
    public InvalidVehicleEnum(String invalidVehicle) {
        super("Invalid vehicle type: " + invalidVehicle + ". Vehicles available: " + getAllVehicles());
    }

    private static String getAllVehicles() {
        StringBuilder vehicles = new StringBuilder();
        VehicleEnum[] allVehicles = VehicleEnum.values();
        for (int i = 0; i < allVehicles.length; i++) {
            vehicles.append(allVehicles[i].name());
            if (i < allVehicles.length - 1) {
                vehicles.append(", ");
            }
        }
        return vehicles.toString();
    }
}
