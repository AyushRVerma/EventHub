package com.eventbook.EventHub.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;


@Component
@RequiredArgsConstructor
public class RateLimiterFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, String> redisTemplate;

    private static final long MAX_TOKENS = 5;
    private static final double REFILL_RATE_PER_MILLISECOND = 1.0 / 12_000.0;

//    private static final int MAX_REQUESTS = 5;
//    private static final Duration TIME_WINDOW = Duration.ofSeconds(60);

    /*
     * Redis runs this entire script atomically.
     *
     * KEYS[1] = Redis bucket key
     * ARGV[1] = maximum bucket capacity
     * ARGV[2] = refill rate (tokens per millisecond)
     * ARGV[3] = current time in milliseconds
     *
     * Returns:
     * 1 = request allowed
     * 0 = request denied
     */

    private static final String TOKEN_BUCKET_SCRIPT = """
            
            local capacity = tonumber(ARGV[1])
            local refillRate = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])

            local tokens = tonumber(redis.call('HGET', KEYS[1], 'tokens'))
            local lastRefill = tonumber(redis.call('HGET', KEYS[1], 'lastRefill'))

            -- New user: create a full bucket.
            if tokens == nil or lastRefill == nil then
                tokens = capacity
                lastRefill = now
            end

            -- Refill based on elapsed time, but never exceed capacity.
            local elapsed = math.max(0, now - lastRefill)
            local refilledTokens = math.min(
                capacity,
                tokens + (elapsed * refillRate)
            )

            -- No complete token available: reject the request.
            if refilledTokens < 1 then
                redis.call('HSET', KEYS[1],
                    'tokens', refilledTokens,
                    'lastRefill', now
                )

                redis.call('PEXPIRE', KEYS[1], 120000)
                return 0
            end

            -- Consume one token for this purchase request.
            local remainingTokens = refilledTokens - 1

            redis.call('HSET', KEYS[1],
                'tokens', remainingTokens,
                'lastRefill', now
            )

            -- Delete inactive buckets after two minutes.
            redis.call('PEXPIRE', KEYS[1], 120000)

            return 1
            """;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException{

        String requestPath = request.getRequestURI();
        String method = request.getMethod();

        // Apply only to:
        // POST /api/v1/events/{eventId}/ticket-types/{ticketTypeId}/tickets
        boolean isTicketPurchaseRequest =
                method.equals("POST")
                        && requestPath.matches(
                        "^/api/v1/events/[^/]+/ticket-types/[^/]+/tickets$"
                );

        if (!isTicketPurchaseRequest) {
            filterChain.doFilter(request, response);
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = "anonymous";


        if(authentication != null && authentication.getPrincipal() instanceof Jwt jwt){
            userId = jwt.getSubject();
        }

        /*
         * One bucket per user.
         *
         * Add ":" + requestPath if you instead want a separate
         * limit per user per purchase endpoint.
         */
        String bucketKey = "token_bucket:" + userId;

        DefaultRedisScript<Long> script = new DefaultRedisScript<>(TOKEN_BUCKET_SCRIPT, Long.class);

        Long allowed = redisTemplate.execute(
                script,
                Collections.singletonList(bucketKey),
                String.valueOf(MAX_TOKENS),
                String.valueOf(REFILL_RATE_PER_MILLISECOND),
                String.valueOf(System.currentTimeMillis()));

        if ( allowed == null || allowed==0) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", "12");

            response.getWriter().write("""
                    {"error":"Too many purchase requests. Please try again in a few seconds."}
                    """);
            return;


        }

        filterChain.doFilter(request, response);

    }
}
