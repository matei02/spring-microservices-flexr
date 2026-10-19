package flexr.gateway.configuration;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFilter that extracts 'sub' and 'email' from the JWT and forwards them as headers to downstream services.
 * Adds:
 *   - x-user-id: sub
 *   - x-user-email: email
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class JwtClaimsForwardingFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.getPrincipal()
            .filter(Jwt.class::isInstance)
            .cast(Jwt.class)
            .flatMap(jwt -> {
                String userId = jwt.getSubject();
                String email = jwt.getClaimAsString("email");
                ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                        .header("x-user-id", userId)
                        .header("x-user-email", email != null ? email : "")
                        .build();
                ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
                return chain.filter(mutatedExchange);
            })
            .switchIfEmpty(chain.filter(exchange));
    }
}

