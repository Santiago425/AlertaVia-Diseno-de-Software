package com.alertavia.service;

import com.alertavia.dto.LoginRequest;
import com.alertavia.dto.LoginResponse;
import com.alertavia.dto.RegisterRequest;
import com.alertavia.exception.ResourceNotFoundException;
import com.alertavia.model.Administrator;
import com.alertavia.model.Citizen;
import com.alertavia.model.User;
import com.alertavia.repository.UserRepository;
import com.alertavia.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Ya existe una cuenta con ese correo");
        }
        Citizen citizen = new Citizen(request.name().trim(), email, passwordEncoder.encode(request.password()),
                request.phone());
        userRepository.save(citizen);
        return LoginResponse.of(tokenProvider.generateToken(citizen.getEmail(), citizen.getRole()), citizen);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email().trim(), request.password()));
        User user = findByEmail(request.email().trim());
        return LoginResponse.of(tokenProvider.generateToken(user.getEmail(), user.getRole()), user);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    @Transactional(readOnly = true)
    public Citizen findCitizen(String email) {
        if (findByEmail(email) instanceof Citizen citizen) {
            return citizen;
        }
        throw new IllegalArgumentException("El usuario no es un ciudadano");
    }

    @Transactional(readOnly = true)
    public Administrator findAdministrator(String email) {
        if (findByEmail(email) instanceof Administrator administrator) {
            return administrator;
        }
        throw new IllegalArgumentException("El usuario no es un administrador");
    }
}
