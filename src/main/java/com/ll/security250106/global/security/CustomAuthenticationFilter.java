package com.ll.security250106.global.security;

import com.ll.security250106.domain.member.member.entity.Member;
import com.ll.security250106.domain.member.member.service.MemberService;
import com.ll.security250106.global.rq.Rq;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {
    private final MemberService memberService;
    private final Rq rq;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (!request.getRequestURI().startsWith("/api/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = authorization.substring("Bearer ".length());

        Optional<Member> opMember = memberService.findByApiKey(apiKey);

        if (opMember.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        Member member = opMember.get();

        rq.setLogin(member.getUsername());

        filterChain.doFilter(request, response);
    }
}
