package com.example.demo.controllers;

import com.example.demo.ApplicationConfigTest;
import com.example.demo.dtos.VehicleDTO;
import com.example.demo.entities.Vehicle;
import com.example.demo.exceptions.InvalidVehicleEnum;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.services.VehicleService;
import com.example.demo.utils.TestDataBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VehicleControllerTest extends ApplicationConfigTest {
    private static final String PATH = "/vehicle";

    @MockBean
    private VehicleService vehicleService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    Vehicle vehicle = TestDataBuilder.buildVehicleNoId();
    VehicleDTO vehicleDTO = TestDataBuilder.buildVehicleDTO();

    private MockHttpServletRequestBuilder buildMockRequestPost
            (String endpoint) throws Exception {
        return MockMvcRequestBuilders
                .post(PATH + endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
    }

    private MockHttpServletRequestBuilder buildMockRequestPost
            (String endpoint, Object requestObject) throws Exception {
        return MockMvcRequestBuilders
                .post(PATH + endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestObject));
    }

    @Test
    void givenValidVehicleDTO_whenRegisterVehicle_thenReturnVehicle() throws Exception {
        when(vehicleService.registerVehicle(any(VehicleDTO.class))).thenReturn(vehicle);

        MockHttpServletRequestBuilder mockRequest = buildMockRequestPost
                ("/register", vehicleDTO);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(vehicle)));

        verify(vehicleService, times(1)).registerVehicle(any(VehicleDTO.class));
    }

    @Test
    void givenInvalidVehicleEnum_whenRegisterVehicle_thenThrowInvalidVehicleEnum() throws Exception {
        vehicleDTO.setVehicleEnum("random");
        when(vehicleService.registerVehicle(vehicleDTO))
                .thenThrow(IllegalArgumentException.class);

        MockHttpServletRequestBuilder mockRequest = buildMockRequestPost
                ("/register", vehicleDTO);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue
                        (result.getResolvedException() instanceof InvalidVehicleEnum));

        verify(vehicleService, never()).registerVehicle(vehicleDTO);
    }

    @Test
    void givenInvalidVehicleDTO_whenRegisterVehicle_thenThrowMethodArgumentNotValidException() throws Exception {
        VehicleDTO vehicleDTO = VehicleDTO.builder().build();
        when(vehicleService.registerVehicle(vehicleDTO))
                .thenThrow(IllegalArgumentException.class);

        MockHttpServletRequestBuilder mockRequest = buildMockRequestPost
                ("/register", vehicleDTO);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue
                        (result.getResolvedException() instanceof MethodArgumentNotValidException));

        verify(vehicleService, never()).registerVehicle(vehicleDTO);
    }

    @Test
    void givenValidLicensePlate_whenCheckInVehicle_thenSucceed() throws Exception {
        MockHttpServletRequestBuilder mockRequest = buildMockRequestPost
                ("/check-in/" + vehicle.getLicensePlate());

        mockMvc.perform(mockRequest)
                .andExpect(status().isNoContent());

        verify(vehicleService, times(1)).checkIn(vehicle.getLicensePlate());
    }

    @Test
    void givenValidLicensePlate_whenCheckOutOfficialVehicle_thenSucceed() throws Exception {
        MockHttpServletRequestBuilder mockRequest = buildMockRequestPost
                ("/check-out/checkoutOfficial/" + vehicle.getLicensePlate());

        mockMvc.perform(mockRequest)
                .andExpect(status().isNoContent());

        verify(vehicleService, times(1))
                .checkOutOfficial(vehicle.getLicensePlate());
    }

    @Test
    void givenValidLicensePlate_whenCheckOutResidentVehicle_thenSucceed() throws Exception {
        MockHttpServletRequestBuilder mockRequest = buildMockRequestPost
                ("/check-out/checkoutResident/" + vehicle.getLicensePlate());

        mockMvc.perform(mockRequest)
                .andExpect(status().isNoContent());

        verify(vehicleService, times(1))
                .checkOutResident(vehicle.getLicensePlate());
    }

    @Test
    void givenValidLicensePlate_whenCheckOutNonResidentVehicle_thenSucceedAndReturnParkingFee() throws Exception {
        BigDecimal result = BigDecimal.ZERO;

        when(vehicleService.checkOutNonResident(vehicle.getLicensePlate()))
                .thenReturn(result);

        MockHttpServletRequestBuilder mockRequest = buildMockRequestPost
                ("/check-out/checkoutNonResident/" + vehicle.getLicensePlate());

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(result)));

        verify(vehicleService, times(1))
                .checkOutNonResident(vehicle.getLicensePlate());
    }

    @Test
    void givenNonExistentLicensePlate_whenCheckOutVehicle_thenReturn404() throws Exception {
        when(vehicleService.checkOutNonResident(vehicle.getLicensePlate()))
                .thenThrow(ResourceNotFoundException.class);

        MockHttpServletRequestBuilder mockRequest = buildMockRequestPost
                ("/check-out/checkoutNonResident/" + vehicle.getLicensePlate());

        mockMvc.perform(mockRequest)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue
                        (result.getResolvedException() instanceof ResourceNotFoundException));

        verify(vehicleService, times(1))
                .checkOutNonResident(vehicle.getLicensePlate());
    }

    @Test
    void givenRequestForStartOfMonth_whenStartOfMonth_thenSucceed() throws Exception {
        MockHttpServletRequestBuilder mockRequest = buildMockRequestPost
                ("/startOfMonth");

        mockMvc.perform(mockRequest)
                .andExpect(status().isNoContent());

        verify(vehicleService, times(1)).startOfMonth();
    }

    @Test
    void givenRequestForResidentPayment_whenGetResidentPaymentInfo_thenSucceed() throws Exception {
        List<HashMap<String, Object>> result = new ArrayList<>();
        when(vehicleService.residentPayment()).thenReturn(result);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders
                .get(PATH + "/residentPayment")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(result.size())));

        verify(vehicleService, times(1)).residentPayment();
    }


}