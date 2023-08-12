package com.example.demo.repositories;

import com.example.demo.dtos.VehicleDTO;
import com.example.demo.entities.ResidentVehicle;
import com.example.demo.entities.Vehicle;
import com.example.demo.enums.VehicleEnum;
import com.example.demo.utils.TestDataBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    private VehicleRepository vehicleRepository;

    Vehicle vehicle = TestDataBuilder.buildVehicleNoId(VehicleEnum.RESIDENT, "123");

    @BeforeEach
    void setUp() {
        vehicleRepository.save(vehicle);
    }

    @AfterEach
    void tearDown() throws Exception {
        vehicleRepository.deleteAll();
    }

    @Test
    void givenVehicle_whenFindByLicensePlate_thenReturnOptionalVehicle() {
        Optional<Vehicle> result = vehicleRepository.findByLicensePlate(vehicle.getLicensePlate());
        assertEquals(Optional.of(vehicle), result);
    }

    @Test
    void givenNoVehicle_whenFindByLicensePlate_thenReturnOptionalEmpty() {
        Optional<Vehicle> result = vehicleRepository.findByLicensePlate("random");
        assertEquals(Optional.empty(), result);
    }

    @Test
    void givenResidentVehicles_whenFindAllResidentVehicle_thenReturnListOfResidentVehicles() {
        List<Vehicle> expectedResult = Collections.singletonList(vehicle);
        List<ResidentVehicle> result = vehicleRepository.findAllResidentVehicle();
        assertEquals(expectedResult, result);
    }

}