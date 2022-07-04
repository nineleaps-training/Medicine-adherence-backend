package com.example.user_service.model.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "my_caretakers")
public class UserCaretaker {

    @Id
    @Column(name = "c_id",nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String cId;

    @Column(name = "patient_name",nullable = false)
    private String patientName;

    @Column(name = "req_status")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean reqStatus;

    @Column(name = "caretaker_id",nullable = false)
    private String caretakerId;

    @Column(name = "patient_id",nullable = false)
    private String patientId;

    @Column(name = "caretaker_username",nullable = false)
    private String caretakerUsername;

    @Column(name = "created_at",nullable = false)
    private String createdAt;

    @Column(name = "sent_by",nullable = false)
    private String sentBy;
}
