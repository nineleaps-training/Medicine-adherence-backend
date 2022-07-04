package com.example.user_service.model.medicine;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "medicine")
public class MedicineEntity {

    @Id
    @Column(name = "med_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private int medId;

    @Column(name = "med_name")
    private String medName;



}
////