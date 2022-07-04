package com.example.user_service.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_details")
@ToString(exclude = "")
public class UserDetails {
    @Id
    @Column(name = "userdet_id",nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String userDetId;

    @Column(name = "bio")
    private String bio;

    @Column(name = "pic_path",nullable = false)
    private String picPath;

    @Column(name = "age")
    private int age;

    @Column(name = "fcm_token",nullable = false)
    private String fcmToken;

    @Column(name = "pincode")
    private int pincode;

    @Column(name = "user_contact")
    private Long userContact;

    @Column(name = "lattitude")
    private float lattitude;

    @Column(name = "longitude")
    private float longitude;

    @Column(name = "address")
    private String address;

    @Column(name = "gender")
    private String gender;

    @Column(name = "blood_group")
    private String bloodGroup;

    @Column(name = "martial_status")
    private String martialStatus;

    @Column(name = "weight")
    private int weight;

    @Column(name = "emergency_contact")
    private int emergencyContact;

    @Column(name = "past_medication")
    private String pastMedication;

    @OneToOne(
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "user_user_id",
            referencedColumnName = "user_id"
    )
    @JsonIgnore
    private UserEntity user;
}
///