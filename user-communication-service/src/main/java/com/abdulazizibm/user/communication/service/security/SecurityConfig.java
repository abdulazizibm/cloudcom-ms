package com.abdulazizibm.user.communication.service.security;

import com.abdulazizibm.security.core.BaseSecurityConfig;
import com.abdulazizibm.security.core.JwtAuthFilter;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig extends BaseSecurityConfig {

  public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
    super(jwtAuthFilter);
  }
}
