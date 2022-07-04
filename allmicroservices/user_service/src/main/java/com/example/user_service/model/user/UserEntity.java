package com.example.user_service.model.user;


import com.example.user_service.model.medicine.UserMedicines;
import com.example.user_service.model.user.UserDetails;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
@NamedEntityGraph(name = "userDetail_graph",
        attributeNodes = @NamedAttributeNode(value = "userDetails"))
public class UserEntity {

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
    private String lastLogin;

    @Column(name = "created_at", nullable = false)
    private String createdAt;

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


}
///