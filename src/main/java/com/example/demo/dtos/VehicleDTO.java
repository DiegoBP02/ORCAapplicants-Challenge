package com.example.demo.dtos;

import com.example.demo.enums.VehicleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class VehicleDTO {
    @NotBlank
    @Size(min = 3, max = 10)
    private String licensePlate;
    @NotBlank
    private String vehicleEnum;
}
