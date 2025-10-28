package com.abdulazizibm.user.communication.service.security;

import com.abdulazizibm.security.core.BaseJwtService;
import com.abdulazizibm.user.communication.service.data.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService extends BaseJwtService {

  @Value("${JWT_SECRET}")
  private String secret;

  public String generateToken(User user) {
    val now = new Date();
    val expiry = new Date(new Date().getTime() + 1000 * 60 * 60);

    return Jwts.builder()
        .setSubject(user.getEmail())
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
        .compact();
  }

}
