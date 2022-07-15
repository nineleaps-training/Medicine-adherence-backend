package com.example.user_service.model.medicine;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @Column(
            name = "med_name",
            nullable = false
    )
    private String medName;
}


//~ Formatted by Jindent --- http://www.jindent.com
