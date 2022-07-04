package com.example.user_service.pojos.dto.medicine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicinePojo {


    @NotNull
    @NotBlank
    private int medicineId;

    @NotNull
    @NotBlank
    private String days;

    @NotNull
    @NotBlank
    private int currentCount;

    @NotNull
    @NotBlank
    private String endDate;

    @NotNull
    @NotBlank
    private String medicineDes;

    @NotNull
    @NotBlank
    private int totalMedReminders;

    @NotNull
    @NotBlank
    private String medicineName;
    private String title;
    private String startDate;
    private int status;
    private String time;



///
}
