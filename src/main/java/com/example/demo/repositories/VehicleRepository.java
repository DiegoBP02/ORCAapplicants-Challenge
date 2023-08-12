package com.example.demo.repositories;

import com.example.demo.entities.OfficialVehicle;
import com.example.demo.entities.ResidentVehicle;
import com.example.demo.entities.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String licensePlate);

    @Query("SELECT v FROM ResidentVehicle v")
    List<ResidentVehicle> findAllResidentVehicle();
}
