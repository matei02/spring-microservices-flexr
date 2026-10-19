package flexr.flex.context;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

/**
 * Filter that reads x-user-id and x-user-email headers and sets the UserContext for the request.
 * Clears the context at the end of the request.
 */
@Component
public class ContextFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String userId = request.getHeader("x-user-id");
        String email = request.getHeader("x-user-email");
        try {
            if (userId != null) {
                UserContextProvider.set(userId, email);
            }
            filterChain.doFilter(request, response);
        } finally {
            UserContextProvider.clear();
        }
    }
}

