package com.abdulazizibm.security.core;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BaseJwtService {

  @Value("${JWT_SECRET}")
  private String secret;

  public Claims validate(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public boolean isExpired(Claims claims) {
    return claims.getExpiration()
        .before(new Date());
  }

}
