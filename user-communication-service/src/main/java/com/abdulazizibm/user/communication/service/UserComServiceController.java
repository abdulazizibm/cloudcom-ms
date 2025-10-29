package com.abdulazizibm.user.communication.service;

import static java.text.MessageFormat.format;

import com.abdulazizibm.security.core.JwtService;
import com.abdulazizibm.user.communication.service.data.User;
import com.abdulazizibm.user.communication.service.data.UserRepository;
import com.abdulazizibm.user.communication.service.dto.LoginRequest;
import com.abdulazizibm.user.communication.service.dto.RegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class UserComServiceController {

  private final UserRepository userRepository;
  private final PasswordEncoder encoder = new BCryptPasswordEncoder();
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
    val email = request.email();
    val password = request.password();

    val userOptional = userRepository.findByEmail(email);

    if (userOptional.isPresent()) {
      return ResponseEntity.status(400)
          .body(format("User with e-mail {0} is already registered", email));
    }

    val user = User.builder()
        .email(email)
        .password(encoder.encode(password))
        .build();
    userRepository.save(user);
    return ResponseEntity.ok(format("User with e-mail {0} was successfully registered", email));
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest request) {
    val email = request.email();
    val rawPassword = request.password();

    val userOptional = userRepository.findByEmail(email);

    if (userOptional.isEmpty()) {
      return ResponseEntity.status(404)
          .body(format("User with e-mail {0} not found", email));
    }
    val user = userOptional.get();
    val encodedPassword = user.getPassword();

    if (!encoder.matches(rawPassword, encodedPassword)) {
      return ResponseEntity.status(401)
          .body(format("Incorrect password for user {0}", email));

    }
    val userJwt = jwtService.generateToken(user.getEmail());
    return ResponseEntity.ok(userJwt);


  }

}
