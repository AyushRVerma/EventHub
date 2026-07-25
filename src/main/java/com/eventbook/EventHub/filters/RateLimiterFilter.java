package com.eventbook.EventHub.filters;

import com.eventbook.EventHub.mappers.EventMapper;
import com.eventbook.EventHub.services.EventService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.RequestContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;


@Component
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;

    private static final int MAX_REQUESTS = 5;

    private static final Duration TIME_WINDOW = Duration.ofSeconds(60);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        if(!requestPath.matches(".*/ticket-types/.*/tickets") || !method.equals("POST")){
            filterChain.doFilter(request, response);
            return;
    }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = "anonymous";

        if(authentication != null && authentication.getPrincipal() instanceof Jwt jwt){
            userId = jwt.getSubject();
        }

        String redisKey = "rate_limit:" + userId + ":" + requestPath;

        Long requestCount =redisTemplate.opsForValue().increment(redisKey);

        if(requestCount!=null && requestCount==1){
            redisTemplate.expire(redisKey, TIME_WINDOW);

        }

        if(requestCount!=null && requestCount>MAX_REQUESTS){
          response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
          response.setContentType("application/json");
          response.getWriter().write(
                  "{\"error\": \"Too many requests\"}"
          );

          return;

        }

        filterChain.doFilter(request, response);

    }
}
