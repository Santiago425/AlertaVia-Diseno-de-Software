package com.alertavia.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.alertavia.dto.LoginResponse;
import com.alertavia.dto.RegisterRequest;
import com.alertavia.exception.ConflictException;
import com.alertavia.exception.ResourceNotFoundException;
import com.alertavia.model.Administrator;
import com.alertavia.model.Citizen;
import com.alertavia.model.User;
import com.alertavia.repository.UserRepository;
import com.alertavia.security.JwtTokenProvider;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    private AuthService authService;

    @Test
    void registersACitizenWithEncryptedPassword() {
        when(userRepository.existsByEmail("laura@correo.com")).thenReturn(false);
        when(passwordEncoder.encode("clave123")).thenReturn("hash");
        when(tokenProvider.generateToken("laura@correo.com", "CITIZEN")).thenReturn("token");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoginResponse response = authService.register(
                new RegisterRequest(" Laura Gómez ", "Laura@Correo.com", "clave123", "3001234567"));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(saved.capture());
        assertThat(saved.getValue()).isInstanceOf(Citizen.class);
        assertThat(saved.getValue().getPassword()).isEqualTo("hash");
        assertThat(saved.getValue().getName()).isEqualTo("Laura Gómez");
        assertThat(saved.getValue().getEmail()).isEqualTo("laura@correo.com");
        assertThat(response.token()).isEqualTo("token");
        assertThat(response.role()).isEqualTo("CITIZEN");
    }

    @Test
    void rejectsADuplicatedEmail() {
        when(userRepository.existsByEmail("laura@correo.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(
                new RegisterRequest("Laura", "laura@correo.com", "clave123", null)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("Ya existe una cuenta con ese correo");
        verify(userRepository, never()).save(any());
    }

    @Test
    void findCitizenRejectsAnAdministrator() {
        when(userRepository.findByEmail("admin@alertavia.com"))
                .thenReturn(Optional.of(new Administrator("Admin", "admin@alertavia.com", "hash", "Coordinador")));

        assertThatThrownBy(() -> authService.findCitizen("admin@alertavia.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void normalizesTheEmail() {
        assertThat(AuthService.normalizeEmail("  Laura@Correo.COM ")).isEqualTo("laura@correo.com");
    }

    @Test
    void failsWhenTheUserDoesNotExist() {
        when(userRepository.findByEmail("nadie@correo.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.findByEmail("nadie@correo.com"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
