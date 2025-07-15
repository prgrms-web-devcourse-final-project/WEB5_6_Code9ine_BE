package com.grepp.spring.app.model.auth.domain;

import com.grepp.spring.app.model.member.domain.Member;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class Principal extends User {
    
    private String accessToken;
    private Long memberId; // userId → memberId로 변경
    
    public Principal(String email, String password,
        Collection<? extends GrantedAuthority> authorities, Long memberId) {
        super(email, password, authorities);
        this.memberId = memberId;
    }
    
    public static Principal createPrincipal(Member member,
        List<SimpleGrantedAuthority> authorities){
        return new Principal(member.getEmail(), member.getPassword(), authorities, member.getMemberId());
    }
    
    public Optional<String> getAccessToken() {
        return Optional.of(accessToken);
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public Long getMemberId() { // getUserId → getMemberId로 변경
        return memberId;
    }
}
