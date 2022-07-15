package com.example.user_service.pojos.dto.medicine;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicinePojo {
    private int medicineId;
    @NotNull(message = "Days should not be null")
    @NotBlank(message = "Days should not be empty")
    private String days;
    @Min(value = 0)
    private int currentCount;
    @NotNull(message = "EndDate should not be null")
    @NotBlank(message = "EndDate should not be Empty")
    private String endDate;
    @NotNull(message = "Medicine Description should not be null")
    @NotBlank(message = "Medicine Description should not be Empty")
    private String medicineDes;
    @Min(value = 0)
    private int totalMedReminders;
    @NotNull(message = "Medicine name should not be null")
    @NotBlank(message = "Medicine name should not be Empty")
    private String medicineName;
    @NotNull(message = "Title should not be null")
    @NotBlank(message = "Title should not be Empty")
    private String title;
    @NotNull(message = "StartDate should not be null")
    @NotBlank(message = "StartDate should not be Empty")
    private String startDate;
    @Min(value = 0)
    private int status;
    @NotNull(message = "Time should not be null")
    @NotBlank(message = "Time should not be Empty")
    private String time;
}


//~ Formatted by Jindent --- http://www.jindent.com
