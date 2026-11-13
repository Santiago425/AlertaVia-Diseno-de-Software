package com.alertavia.service;

import com.alertavia.dto.LoginRequest;
import com.alertavia.dto.LoginResponse;
import com.alertavia.dto.RegisterRequest;
import com.alertavia.exception.ConflictException;
import com.alertavia.exception.ResourceNotFoundException;
import com.alertavia.model.Administrator;
import com.alertavia.model.Citizen;
import com.alertavia.model.User;
import com.alertavia.repository.UserRepository;
import com.alertavia.security.JwtTokenProvider;
import java.util.Locale;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
    }

    public static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public LoginResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Ya existe una cuenta con ese correo");
        }
        Citizen citizen = userRepository.save(new Citizen(request.name().trim(), email,
                passwordEncoder.encode(request.password()), request.phone()));
        return LoginResponse.of(tokenProvider.generateToken(citizen.getEmail(), citizen.getRole()), citizen);
    }

    public LoginResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        User user = findByEmail(email);
        return LoginResponse.of(tokenProvider.generateToken(user.getEmail(), user.getRole()), user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(normalizeEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    public Citizen findCitizen(String email) {
        if (findByEmail(email) instanceof Citizen citizen) {
            return citizen;
        }
        throw new IllegalArgumentException("El usuario no es un ciudadano");
    }

    public Administrator findAdministrator(String email) {
        if (findByEmail(email) instanceof Administrator administrator) {
            return administrator;
        }
        throw new IllegalArgumentException("El usuario no es un administrador");
    }
}
