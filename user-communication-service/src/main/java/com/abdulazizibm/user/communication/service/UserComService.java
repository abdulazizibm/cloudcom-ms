package com.abdulazizibm.user.communication.service;

import static java.text.MessageFormat.format;

import com.abdulazizibm.security.core.JwtService;
import com.abdulazizibm.user.communication.service.data.User;
import com.abdulazizibm.user.communication.service.data.UserRepository;
import com.abdulazizibm.user.communication.service.dto.LoginRequest;
import com.abdulazizibm.user.communication.service.dto.RegisterRequest;
import com.abdulazizibm.user.communication.service.exception.IncorrectPasswordException;
import com.abdulazizibm.user.communication.service.exception.UserAlreadyExistsException;
import com.abdulazizibm.user.communication.service.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class UserComService {
  private final UserRepository userRepository;
  private final PasswordEncoder encoder = new BCryptPasswordEncoder();
  private final JwtService jwtService;


  public String register(RegisterRequest request) {
    val email = request.email();
    val password = request.password();

    val userOptional = userRepository.findByEmail(email);

    if (userOptional.isPresent()) {
      throw new UserAlreadyExistsException(email);
    }

    val user = User.builder()
        .email(email)
        .password(encoder.encode(password))
        .build();
    userRepository.save(user);
    return format("User with e-mail {0} was successfully registered", email);
  }
  public String login(LoginRequest request) {
    val email = request.email();
    val rawPassword = request.password();

    val userOptional = userRepository.findByEmail(email);

    if (userOptional.isEmpty()) {
      throw new UserNotFoundException(email);
    }
    val user = userOptional.get();
    val encodedPassword = user.getPassword();

    if (!encoder.matches(rawPassword, encodedPassword)) {
      throw new IncorrectPasswordException(email);
    }
    return jwtService.generateToken(user.getEmail(), String.valueOf(user.getId()));

  }

}
