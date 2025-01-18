package com.dailycodework.dreamshops.model;

import com.dailycodework.dreamshops.security.entities.ForgotPassword;
import com.dailycodework.dreamshops.security.entities.RefreshToken;
import com.dailycodework.dreamshops.security.entities.UserRole;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name ="users")
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username ;
    @NaturalId
    private String email;

    private String password;

    @OneToMany(mappedBy = "user" , cascade = CascadeType.ALL , orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Order> orders;

    @OneToOne(mappedBy = "user" ,cascade = CascadeType.ALL, orphanRemoval = true)
    private ForgotPassword forgotPassword;

    @Enumerated(EnumType.STRING)
    private UserRole role ;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RefreshToken> refreshTokens;

    private boolean verified ; // track whether email is verified
    private String  verificationToken ; // stores the verification token

    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpiresAt;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public RefreshToken getRefreshToken() {
        if (refreshTokens == null || refreshTokens.isEmpty()) {
            return null;
        }
        // Sort refresh tokens by expiration time (most recent first)
        refreshTokens.sort((t1, t2) -> t2.getExpirationTime().compareTo(t1.getExpirationTime()));
        return refreshTokens.getFirst(); // Return the most recent refresh token
    }
}
