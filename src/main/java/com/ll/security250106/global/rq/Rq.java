package com.ll.security250106.global.rq;

import com.ll.security250106.domain.member.member.entity.Member;
import com.ll.security250106.domain.member.member.service.MemberService;
import com.ll.security250106.global.exceptions.ServiceException;
import com.ll.security250106.util.Ut;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
}
