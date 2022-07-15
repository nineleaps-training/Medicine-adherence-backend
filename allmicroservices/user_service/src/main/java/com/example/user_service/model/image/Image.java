package com.example.user_service.model.image;

import java.util.Date;

import javax.persistence.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;

import com.example.user_service.model.medicine.UserMedicines;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "image")
public class Image {
    @Id
    @Column(
        name     = "image_id",
        nullable = false
    )
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name     = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String imageId;
    @Column(
        name     = "date",
        nullable = false
    )
    @Temporal(TemporalType.DATE)
    @JsonFormat(
        shape    = JsonFormat.Shape.STRING,
        pattern  = "yyyy-MM-dd"
    )
    @NotNull(message = "Date should not be empty")
    private Date   date;
    @Column(
        name     = "time",
        nullable = false,
        length   = 50
    )
    @NotNull(message = "Time should not be empty")
    @NotBlank(message = "Time should not be empty")
    private String time;
    @Column(
        name     = "Caretaker_name",
        nullable = false,
        length   = 100
    )
    @NotNull(message = "Caretaker name should not be empty")
    @NotBlank(message = "Caretaker name should not be empty")
    private String caretakerName;
    @Column(
        name     = "image_url",
        nullable = false,
        length   = 250
    )
    @NotNull(message = "ImageUrl should not be empty")
    @NotBlank(message = "ImageUrl should not be empty")
    private String imageUrl;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name                 = "medimage_id",
        referencedColumnName = "medicine_id"
    )
    @JsonIgnore
    UserMedicines  userMedicines;
}
