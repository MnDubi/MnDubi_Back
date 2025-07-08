package festival.dev.global.security.filter;

import festival.dev.domain.user.entity.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SpikeArrestFilter extends OncePerRequestFilter {
    private final Logger logger = LoggerFactory.getLogger(SpikeArrestFilter.class);

    private final Map<String , Long> lastRequestTimeMap = new ConcurrentHashMap<>();

    private static final long MIN_INTERVAL_MS = 100;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()){
            chain.doFilter(request, response);
            return;
        }

        Object principal = Objects.requireNonNull(auth).getPrincipal();
        String code;
        String path = request.getRequestURI();
        String key;

        if(principal instanceof CustomUserDetails userDetails){
            code = userDetails.getUserCode();
            logger.info("SpikeArrestFilter : doFilterInternal() - User code : {}", code);
        } else {
            logger.warn("SpikeArrestFilter : doFilterInternal() - Principal is not an instance of UserDetails: {}", principal);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized");
            return;
        }
        key = code + ":" + path;
        long currentTime = System.currentTimeMillis();
        Long lastRequestTime = lastRequestTimeMap.get(key);
        if (lastRequestTime != null && (currentTime - lastRequestTime < MIN_INTERVAL_MS)) {
            logger.warn("SpikeArrestFilter : doFilterInternal() - Too many requests from user code : {}", code);
            response.setStatus(492);
            response.getWriter().write("Too many requests : Spike arrest triggered");
            return;
        }
        lastRequestTimeMap.put(key, currentTime);
        logger.info("SpikeArrestFilter : doFilterInternal() - Request allowed for user code : {}", code);
        chain.doFilter(request, response);
    }
}
