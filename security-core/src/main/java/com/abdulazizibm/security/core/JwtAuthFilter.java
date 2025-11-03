package com.abdulazizibm.security.core;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@AllArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtService jwtService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    log.info("JwtAuthFilter triggered for " + request.getRequestURI());

    val authHeader = request.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      //public endpoint (e.g. /register)
      log.warn("No Bearer token found");
      filterChain.doFilter(request, response);
      return;
    }
    try {
      // remove "Bearer"
      val token = authHeader.substring(7);

      log.info("Validating token: " + token);
      val claims = jwtService.validate(token);
      log.info("Successfully validated token: " + token);

      if (jwtService.isExpired(claims)) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
        return;
      }
      // Extract info from JWT
      val userEmail = claims.getSubject();
      val role = claims.get("role", String.class);

      // Create Authentication object so Spring knows user info
      var authToken = new UsernamePasswordAuthenticationToken(
          userEmail, null, List.of(new SimpleGrantedAuthority("ROLE_" + role)));

      SecurityContextHolder.getContext().setAuthentication(authToken);
    } catch (JwtException e) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
      return;
    }
    filterChain.doFilter(request, response);
  }
}
