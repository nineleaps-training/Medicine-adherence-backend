package com.example.user_service.model.medicine;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_med_reminder")
public class UserMedReminder {

    @Id
    @Column(name = "reminder_id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String reminderId;

    @Column(name = "reminder_title",nullable = false)
    private String reminderTitle;

    @Column(name = "created_at",nullable = false)
    private String createdAt;

    @Column(name = "start_date",nullable = false)
    private Date startDate;

    @Column(name = "end_date",nullable = false)
    private Date endDate;

    @Column(name = "everyday",nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean everyday;

    @Column(name = "reminder_status",nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean reminderStatus;

    @Column(name = "days",nullable = false)
    private String days;

    @Column(name = "reminder_time",nullable = false)
    private String reminderTime;

    @OneToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "med_rem_id",
            referencedColumnName = "medicine_id"
    )
    @JsonIgnore
    private UserMedicines userRem;
}
