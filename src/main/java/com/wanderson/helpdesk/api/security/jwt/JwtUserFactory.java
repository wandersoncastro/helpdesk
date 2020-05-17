package com.wanderson.helpdesk.api.security.jwt;

import com.wanderson.helpdesk.api.entity.User;
import com.wanderson.helpdesk.api.enums.ProfileEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

public class JwtUserFactory {
    private JwtUserFactory(){

    }

    public static JwtUser create(User user){
        return new JwtUser(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                mapToGranteAuthorities(user.getProfile())
        );
    }

    private static List<GrantedAuthority> mapToGranteAuthorities(ProfileEnum profileEnum){
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(profileEnum.toString()));
        return authorities;
    }
}
