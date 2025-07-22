package com.grepp.spring.infra.config.security;

import com.grepp.spring.app.model.auth.domain.Principal;
import com.grepp.spring.app.model.member.repos.MemberRepository;
import com.grepp.spring.app.model.member.domain.Member;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        System.out.println("UserDetailsServiceImpl.loadUserByUsername 호출됨: username=" + username);
        
        Member member = memberRepository.findByEmailIgnoreCase(username)
                            .orElseThrow(() -> {
                                System.out.println("사용자를 찾을 수 없음: " + username);
                                return new UsernameNotFoundException(username);
                            });
        
        System.out.println("사용자 찾음: " + member.getEmail() + ", 비밀번호: " + member.getPassword());
        
        List<SimpleGrantedAuthority> authorities = findUserAuthorities(username);
        return Principal.createPrincipal(member, authorities);
    }

    public List<SimpleGrantedAuthority> findUserAuthorities(String username){
        Member member = memberRepository.findByEmailIgnoreCase(username)
                            .orElseThrow(() -> new UsernameNotFoundException(username));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole()));
        return authorities;
    }
    
    public Long findMemberIdByEmail(String email) {
        Member member = memberRepository.findByEmailIgnoreCase(email)
                            .orElseThrow(() -> new UsernameNotFoundException(email));
        return member.getMemberId();
    }
}
