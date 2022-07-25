package com.example.auth.dao.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.validation.constraints.NotBlank;
import java.util.Date;

import com.example.auth.config.UserRole;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@ToString
@Table(name = "user")
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", unique = true)
    @NotBlank
    private String email;

    @Column(name = "nickname")
    @NotBlank
    private String nickname;

    @Column(name = "password")
    @NotBlank
    private String password;

    @Column(name = "name")
    @NotBlank
    private String name;

    @Column(name = "phone_num", unique = true)
    @NotBlank
    private String phoneNum;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_NOT_PERMITTED;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date createAt;
    /* 객체의 불변성이 비밀번호 변경 시 불가피하게 깨지므로,
       유지보수 등을 위해 수정되는 시점을 기록해야 함 */
    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date updateAt;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "salt_id")
    private Salt salt;


    @Builder
    public User(String email, String nickname, String password, 
    String name, String phoneNum, UserRole role, Salt salt) {
        
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.name = name;
        this.phoneNum = phoneNum;
        this.role = role;
        this.salt = salt;

    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

}
