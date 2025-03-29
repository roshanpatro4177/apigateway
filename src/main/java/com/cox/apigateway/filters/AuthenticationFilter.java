package com.cox.apigateway.filters;

import com.cox.apigateway.security.JwtUtil;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements WebFilter {
    private final JwtUtil jwtUtil;

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (request.getPath().toString().contains("/auth")) {
            return chain.filter(exchange); // Skip auth endpoints
        }

        return jwtUtil.authenticate(request)
                .then(chain.filter(exchange))
                .onErrorResume(e -> exchange.getResponse().setComplete());
    }
}
