package com.example.user_service.model.medicine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "medicine_history")
public class MedicineHistory {

    @Id
    @Column(name = "history_id",nullable = false)
    private int historyId;

    @Column(name = "med_date",nullable = false)
    private String date;

    @Column(name = "taken",nullable = false,length = 50)
    private String taken;

    @Column(name = "not_taken",nullable = false,length = 50)
    private String notTaken;

    @ManyToOne()
    @JoinColumn(name = "medicine_history", referencedColumnName = "medicine_id")
    @JsonIgnore
    UserMedicines userMedicines;


}
///