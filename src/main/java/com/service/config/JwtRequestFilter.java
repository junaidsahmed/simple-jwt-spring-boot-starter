package com.service.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * @author junaid shakeel
 * @date 16/04/2023
 */
@Configuration
@EnableConfigurationProperties({JwtUtil.class})
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    JwtUtil jwtTokenUtil;

    @Autowired
    public JwtRequestFilter ( JwtUtil jwtUtil){
        this.jwtTokenUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        try {
            String jwt = parseJWT(request);
            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwt != null && jwtTokenUtil.validateJwtToken(jwt)) {
                    String username = jwtTokenUtil.getUserNameFromJwtToken(jwt);
                    UserDetails userDetails = JWTUserDetail.build(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, new ArrayList<>());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            }
        }
        catch (Exception e){
            log.error("error occurred during user authentication: "+e);
        }

        filterChain.doFilter(request, response);
    }


    private String parseJWT(HttpServletRequest request) {
        String header = request.getHeader(HEADER_STRING);

        if (StringUtils.hasText(header) && header.startsWith(TOKEN_PREFIX)) {
            return header.substring(7);
        }
        log.debug(HEADER_STRING +" header is not found or JWT token is not starting with "+ TOKEN_PREFIX);
        return null;
    }



}
