package com.example.user_service.repository.medicine;

import com.example.user_service.model.medicine.MedicineHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserMedHistoryRepository extends JpaRepository<MedicineHistory,Integer> {


}
