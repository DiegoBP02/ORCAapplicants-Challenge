package com.example.demo.controllers;

import com.example.demo.dtos.VehicleDTO;
import com.example.demo.entities.Vehicle;
import com.example.demo.enums.VehicleEnum;
import com.example.demo.exceptions.InvalidVehicleEnum;
import com.example.demo.services.VehicleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping(value = "/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/register")
    public ResponseEntity<Vehicle> register(@Valid @RequestBody VehicleDTO vehicleDTO){
        try{
            VehicleEnum.valueOf(vehicleDTO.getVehicleEnum().toUpperCase());
            return ResponseEntity.ok(vehicleService.registerVehicle(vehicleDTO));
        } catch (IllegalArgumentException e){
            throw new InvalidVehicleEnum(vehicleDTO.getVehicleEnum());
        }
    }

    @PostMapping("/check-in/{licensePlate}")
    public ResponseEntity<Void> checkIn(@PathVariable String licensePlate){
        vehicleService.checkIn(licensePlate);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-out/checkoutOfficial/{licensePlate}")
    public ResponseEntity<Void> checkOut(@PathVariable String licensePlate){
        vehicleService.checkOutOfficial(licensePlate);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-out/checkoutResident/{licensePlate}")
    public ResponseEntity<Void> checkOutResident(@PathVariable String licensePlate) {
        vehicleService.checkOutResident(licensePlate);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/check-out/checkoutNonResident/{licensePlate}")
    public ResponseEntity<BigDecimal> checkOutNonResident(@PathVariable String licensePlate) {
        BigDecimal parkingFee = vehicleService.checkOutNonResident(licensePlate);
        return ResponseEntity.ok(parkingFee);
    }

    @PostMapping("/startOfMonth")
    public ResponseEntity<Void> startOfMonth() {
        vehicleService.startOfMonth();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/residentPayment")
    public ResponseEntity<List<HashMap<String, Object>>> residentPayment() {
        List<HashMap<String, Object>> result = vehicleService.residentPayment();
        return ResponseEntity.ok(result);
    }

}
