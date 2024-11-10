package com.finance.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "[user_details]")
public class UserDetailsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String emailId;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private Integer phoneNumber;

    @Column
    private String address;

    @Lob
    private byte[] profilePic;


}
