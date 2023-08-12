package com.example.demo.services;

import com.example.demo.dtos.VehicleDTO;
import com.example.demo.entities.NonResidentVehicle;
import com.example.demo.entities.OfficialVehicle;
import com.example.demo.entities.ResidentVehicle;
import com.example.demo.entities.Vehicle;
import com.example.demo.enums.VehicleEnum;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.exceptions.UniqueConstraintViolationError;
import com.example.demo.repositories.StayRepository;
import com.example.demo.repositories.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private StayRepository stayRepository;

    public Vehicle registerVehicle(VehicleDTO vehicleDTO) {
        try{
            Vehicle vehicle = createVehicle(VehicleEnum.valueOf
                    (vehicleDTO.getVehicleEnum()), vehicleDTO.getLicensePlate());
            return vehicleRepository.save(vehicle);
        }catch (DataIntegrityViolationException e) {
            throw new UniqueConstraintViolationError("vehicle", "license plate");
        }
    }

    private Vehicle createVehicle(VehicleEnum vehicleEnum, String licensePlate) {
        return switch (vehicleEnum) {
            case OFFICIAL -> new OfficialVehicle(licensePlate);
            case RESIDENT -> new ResidentVehicle(licensePlate);
            case NON_RESIDENT -> new NonResidentVehicle(licensePlate);
            default -> throw new IllegalArgumentException("Invalid vehicle type");
        };
    }

    public void checkIn(String licensePlate) {
        Vehicle vehicle = findByLicensePlate(licensePlate);
        vehicle.setEntryTimeCurrentTime();
        vehicleRepository.save(vehicle);
    }

    private Vehicle findByLicensePlate(String licensePlate) {
        return vehicleRepository.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new ResourceNotFoundException("vehicle", licensePlate));
    }

    public void checkOutResident(String licensePlate) {
        ResidentVehicle residentVehicle = (ResidentVehicle) findByLicensePlate(licensePlate);

        residentVehicle.setExitTimeCurrentTime();
        residentVehicle.addStayTimeToAccumulatedTime();

        residentVehicle.clearTimes();

        vehicleRepository.save(residentVehicle);
    }

    public void checkOutOfficial(String licensePlate) {
        OfficialVehicle officialVehicle = (OfficialVehicle) findByLicensePlate(licensePlate);

        officialVehicle.setExitTimeCurrentTime();
        officialVehicle.addStay();

        vehicleRepository.save(officialVehicle);
    }

    public BigDecimal checkOutNonResident(String licensePlate) {
        NonResidentVehicle nonResidentVehicle = (NonResidentVehicle) findByLicensePlate(licensePlate);

        nonResidentVehicle.setExitTimeCurrentTime();

        BigDecimal parkingFee = nonResidentVehicle.calculateParkingFee();

        nonResidentVehicle.clearTimes();

        vehicleRepository.save(nonResidentVehicle);

        return parkingFee;
    }

    public void startOfMonth() {
        clearStaysForOfficialVehicles();
        resetAccumulatedTimeForResidentVehicles();
    }

    private void clearStaysForOfficialVehicles() {
        stayRepository.deleteAll();
    }

    private void resetAccumulatedTimeForResidentVehicles() {
        List<ResidentVehicle> residentVehicles = findAllResidentVehicles();
        for (ResidentVehicle residentVehicle : residentVehicles) {
            residentVehicle.clearAccumulatedTime();
            vehicleRepository.save(residentVehicle);
        }
    }

    public List<HashMap<String, Object>> residentPayment() {
        List<ResidentVehicle> residentVehicles = findAllResidentVehicles();
        List<HashMap<String, Object>> result = new ArrayList<>();
        for(ResidentVehicle residentVehicle : residentVehicles){
            String licensePlate = residentVehicle.getLicensePlate();
            BigDecimal parkingTime = Vehicle.millisecondsToMinutes(residentVehicle.getAccumulatedTime());
            BigDecimal value = residentVehicle.calculateParkingFee();
            HashMap<String, Object> resultMap = new HashMap<>();
            resultMap.put("licensePlate", licensePlate);
            resultMap.put("parkingTime", parkingTime);
            resultMap.put("value", value);
            result.add(resultMap);
        }

        return result;
    }

    private List<ResidentVehicle> findAllResidentVehicles(){
        return vehicleRepository.findAllResidentVehicle();
    }
}
