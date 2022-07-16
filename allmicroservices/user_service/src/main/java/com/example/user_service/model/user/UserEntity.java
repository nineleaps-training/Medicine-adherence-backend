package com.example.user_service.model.user;


import com.example.user_service.model.medicine.UserMedicines;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.jsonschema.JsonSerializableSchema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
@NamedEntityGraph(name = "userDetail_graph",
        attributeNodes = @NamedAttributeNode(value = "userDetails"))
public class UserEntity implements Serializable {

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "last_login", nullable = false)
    private LocalDateTime lastLogin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToOne(
            cascade = CascadeType.ALL,
            mappedBy = "user",
            fetch = FetchType.LAZY
    )

    private UserDetails userDetails;


    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "userEntity",
            fetch = FetchType.EAGER
    )
    @JsonIgnore
    private List<UserMedicines> userMedicines;

    public UserEntity(LocalDateTime lastLogin,LocalDateTime createdAt,UserDetails userDetails){
        this.lastLogin = lastLogin;
        this.createdAt = createdAt;
        this.userDetails = userDetails;
    }


}
