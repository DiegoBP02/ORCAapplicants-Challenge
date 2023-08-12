package com.example.demo.services;

import com.example.demo.ApplicationConfigTest;
import com.example.demo.dtos.VehicleDTO;
import com.example.demo.entities.NonResidentVehicle;
import com.example.demo.entities.OfficialVehicle;
import com.example.demo.entities.ResidentVehicle;
import com.example.demo.entities.Vehicle;
import com.example.demo.enums.VehicleEnum;
import com.example.demo.exceptions.UniqueConstraintViolationError;
import com.example.demo.repositories.StayRepository;
import com.example.demo.repositories.VehicleRepository;
import com.example.demo.utils.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleServiceTest extends ApplicationConfigTest {

    @Autowired
    private VehicleService vehicleService;

    @MockBean
    private VehicleRepository vehicleRepository;

    @MockBean
    private StayRepository stayRepository;

    Vehicle vehicle = TestDataBuilder.buildVehicleNoId();
    ResidentVehicle residentVehicle = (ResidentVehicle) TestDataBuilder.buildVehicleNoId(VehicleEnum.RESIDENT, "123");
    OfficialVehicle officialVehicle = (OfficialVehicle) TestDataBuilder.buildVehicleNoId(VehicleEnum.OFFICIAL, "123");
    NonResidentVehicle nonResidentVehicle = (NonResidentVehicle) TestDataBuilder.buildVehicleNoId(VehicleEnum.NON_RESIDENT, "123");
    VehicleDTO vehicleDTO = TestDataBuilder.buildVehicleDTO();

    @BeforeEach
    void setUp() {
        vehicle.setEntryTimeCurrentTime();
        vehicle.setExitTimeCurrentTime();

        residentVehicle.setEntryTimeCurrentTime();
        residentVehicle.setExitTimeCurrentTime();

        officialVehicle.setEntryTimeCurrentTime();
        officialVehicle.setExitTimeCurrentTime();

        nonResidentVehicle.setEntryTimeCurrentTime();
        nonResidentVehicle.setExitTimeCurrentTime();
    }

    @Test
    void givenValidVehicleDTO_whenRegisterVehicle_thenSaveAndReturnVehicle() {
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        Vehicle result = vehicleService.registerVehicle(vehicleDTO);

        assertEquals(vehicle, result);

        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void givenVehicleAlreadyExists_whenRegisterVehicle_thenThrowUniqueConstraintViolationError() {
        when(vehicleRepository.save(any(Vehicle.class)))
                .thenThrow(DataIntegrityViolationException.class);

        assertThrows(UniqueConstraintViolationError.class, () -> {
            vehicleService.registerVehicle(vehicleDTO);
        });

        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void givenValidLicensePlate_whenCheckIn_thenSetEntryTimeAndSaveVehicle() {
        when(vehicleRepository.findByLicensePlate(vehicle.getLicensePlate()))
                .thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        vehicleService.checkIn(vehicle.getLicensePlate());

        assertNotNull(vehicle.getEntryTime());

        verify(vehicleRepository, times(1))
                .findByLicensePlate(vehicle.getLicensePlate());
        verify(vehicleRepository, times(1)).save(vehicle);
    }

    @Test
    void givenValidLicensePlate_whenCheckOutResident_thenSetExitTimeAndSaveResidentVehicle() {
        when(vehicleRepository.findByLicensePlate(residentVehicle.getLicensePlate()))
                .thenReturn(Optional.of(residentVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(residentVehicle);

        vehicleService.checkOutResident(residentVehicle.getLicensePlate());

        assertThat(residentVehicle.getAccumulatedTime()).isNotZero();
        assertNull(residentVehicle.getEntryTime());
        assertNull(residentVehicle.getExitTime());

        verify(vehicleRepository, times(1))
                .findByLicensePlate(residentVehicle.getLicensePlate());
        verify(vehicleRepository, times(1)).save(residentVehicle);
    }

    @Test
    void givenValidLicensePlate_whenCheckOutOfficial_thenSetExitTimeAndSaveOfficialVehicle() {
        when(vehicleRepository.findByLicensePlate(officialVehicle.getLicensePlate()))
                .thenReturn(Optional.of(officialVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(officialVehicle);

        vehicleService.checkOutOfficial(officialVehicle.getLicensePlate());

        assertThat(officialVehicle.getStayList().size()).isNotZero();
        assertNull(officialVehicle.getEntryTime());
        assertNull(officialVehicle.getExitTime());

        verify(vehicleRepository, times(1))
                .findByLicensePlate(officialVehicle.getLicensePlate());
        verify(vehicleRepository, times(1)).save(officialVehicle);
    }

    @Test
    void givenValidLicensePlate_whenCheckOutNonResident_thenCalculateParkingFeeAndSaveNonResidentVehicle() {
        when(vehicleRepository.findByLicensePlate(nonResidentVehicle.getLicensePlate()))
                .thenReturn(Optional.of(nonResidentVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(nonResidentVehicle);

        BigDecimal result = vehicleService.checkOutNonResident(nonResidentVehicle.getLicensePlate());

        System.out.println("check" + result);
        assertNull(nonResidentVehicle.getEntryTime());
        assertNull(nonResidentVehicle.getExitTime());

        verify(vehicleRepository, times(1))
                .findByLicensePlate(nonResidentVehicle.getLicensePlate());
        verify(vehicleRepository, times(1)).save(nonResidentVehicle);
    }

    @Test
    void whenStartOfMonth_thenClearStaysForOfficialVehiclesAndResetAccumulatedTimeForResidentVehicles() {
        residentVehicle.setAccumulatedTime(1);
        List<ResidentVehicle> residentVehicleList = Collections.singletonList(residentVehicle);
        when(vehicleRepository.findAllResidentVehicle()).thenReturn(residentVehicleList);

        vehicleService.startOfMonth();

        assertEquals(0, residentVehicle.getAccumulatedTime());

        verify(stayRepository, times(1)).deleteAll();
        verify(vehicleRepository,times(1)).findAllResidentVehicle();
        verify(vehicleRepository,times(1)).save(residentVehicle);
    }

    @Test
    void givenVehicles_whenResidentPayment_thenRetrieveResidentVehiclesCalculateValuesAndReturnList() {
        residentVehicle.setAccumulatedTime(1);
        List<ResidentVehicle> residentVehicleList = Collections.singletonList(residentVehicle);
        when(vehicleRepository.findAllResidentVehicle()).thenReturn(residentVehicleList);

        List<HashMap<String, Object>> expectedResult = new ArrayList<>();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("licensePlate", residentVehicle.getLicensePlate());
        hashMap.put("parkingTime", Vehicle.millisecondsToMinutes(residentVehicle.getAccumulatedTime()));
        hashMap.put("value", residentVehicle.calculateParkingFee());
        expectedResult.add(hashMap);

        List<HashMap<String, Object>>  result = vehicleService.residentPayment();

        assertEquals(expectedResult, result);

        verify(vehicleRepository,times(1)).findAllResidentVehicle();
    }


}