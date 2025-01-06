package com.ll.security250106.global.rq;

import com.ll.security250106.domain.member.member.entity.Member;
import com.ll.security250106.domain.member.member.service.MemberService;
import com.ll.security250106.global.exceptions.ServiceException;
import com.ll.security250106.util.Ut;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

// Request/Response 를 추상화한 객체
// Request, Response, Cookie, Session 등을 다룬다.
@RequestScope
@Component
@RequiredArgsConstructor
public class Rq {
    private final MemberService memberService;
    private final HttpServletRequest request;

    public Member checkAuthentication() {
        String credentials = request.getHeader("Authorization");
        String apiKey = credentials == null ?
                "" : credentials.substring("Bearer ".length());

        if (Ut.str.isBlank(apiKey)) {
            throw new ServiceException("401-1", "apiKey를 입력해주세요.");
        }

        Optional<Member> opActor = memberService.findByApiKey(apiKey);

        if (opActor.isEmpty()) {
            throw new ServiceException("401-1", "사용자 인증정보가 올바르지 않습니다.");
        }

        return opActor.get();
    }

    public Member getActorByUsername(String username) {
        return memberService.findByUsername(username).get();
    }

    // 스프링 시큐리티가 이해하는 방식으로 강제 로그인 처리
    // 임시
    public void setLogin(String username) {
        UserDetails user = new User(username, "", List.of());

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, user.getPassword(), user.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
