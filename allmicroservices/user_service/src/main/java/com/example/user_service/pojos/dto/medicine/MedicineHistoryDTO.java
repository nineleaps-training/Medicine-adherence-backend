package com.example.user_service.pojos.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicineHistoryDTO {

    @Min(value = 1,message = "Enter Valid Reminder Id")
    private int remId;

    @NotNull
    @NotBlank
    private String date;

    @NotNull
    private String[] taken;

    @NotNull
    private String[] notTaken;


}
