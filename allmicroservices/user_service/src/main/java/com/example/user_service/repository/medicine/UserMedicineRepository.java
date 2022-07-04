package com.example.user_service.repository.medicine;

import com.example.user_service.model.medicine.UserMedicines;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserMedicineRepository extends JpaRepository<UserMedicines,Integer> {


    @Query("SELECT u from UserMedicines u where u.medicineId = ?1")
    public UserMedicines getMedById(Integer medicineId);

    @Query("SELECT U FROM UserMedicines U WHERE U.days like (%?1%)")
    List<UserMedicines> getMedicinesforToday(String day);



}
//