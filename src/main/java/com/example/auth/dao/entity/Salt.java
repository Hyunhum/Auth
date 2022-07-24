package com.example.auth.dao.entity;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;


import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "salt")
@Getter
public class Salt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull()
    private String salt;

    @Builder
    public Salt(String salt) {
        this.salt = salt;
    }
}
