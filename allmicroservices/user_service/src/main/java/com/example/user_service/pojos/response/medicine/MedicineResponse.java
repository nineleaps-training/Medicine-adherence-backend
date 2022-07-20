package com.example.user_service.pojos.response.medicine;


import com.example.user_service.model.medicine.MedicineHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MedicineResponse implements Serializable {
    private String status;
    private String message;
    private List<MedicineHistory> userMedicinesList;
}
