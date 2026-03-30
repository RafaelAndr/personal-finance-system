package com.personal_finance.security;

import com.personal_finance.entity.Users;
import com.personal_finance.entity.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.UUID;

public class JwtUserDetails extends User {

    private final Users user;

    public JwtUserDetails(Users user) {
        super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public UUID getId() {
        return this.user.getId();
    }

    public Role getRole() {
        return this.user.getRole();
    }
}
