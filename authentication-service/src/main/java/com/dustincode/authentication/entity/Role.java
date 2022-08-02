package com.dustincode.authentication.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "m_role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}
