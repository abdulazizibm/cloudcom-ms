package com.abdulazizibm.security.core;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.util.Date;

import lombok.val;
import org.springframework.beans.factory.annotation.Value;


public class JwtService {

  @Value("${JWT_SECRET}")
  private String secret;

  public Claims validate(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  public String generateToken(String userEmail) {
    val now = new Date();
    val expiry = new Date(new Date().getTime() + 1000 * 60 * 60);

    return Jwts.builder()
        .setSubject(userEmail)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
        .compact();
  }


  public boolean isExpired(Claims claims) {
    return claims.getExpiration()
        .before(new Date());
  }

}
