package com.alertavia.config;

import com.alertavia.model.Administrator;
import com.alertavia.model.Citizen;
import com.alertavia.model.MobilityEntity;
import com.alertavia.repository.MobilityEntityRepository;
import com.alertavia.repository.UserRepository;
import com.alertavia.service.AuthService;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MobilityEntityRepository mobilityEntityRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;
    private final String citizenEmail;
    private final String citizenPassword;

    public DataInitializer(UserRepository userRepository, MobilityEntityRepository mobilityEntityRepository,
                           PasswordEncoder passwordEncoder,
                           @Value("${app.admin.email}") String adminEmail,
                           @Value("${app.admin.password}") String adminPassword,
                           @Value("${app.demo.citizen-email}") String citizenEmail,
                           @Value("${app.demo.citizen-password}") String citizenPassword) {
        this.userRepository = userRepository;
        this.mobilityEntityRepository = mobilityEntityRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = AuthService.normalizeEmail(adminEmail);
        this.adminPassword = adminPassword;
        this.citizenEmail = AuthService.normalizeEmail(citizenEmail);
        this.citizenPassword = citizenPassword;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            userRepository.save(new Administrator("Administrador AlertaVía", adminEmail,
                    passwordEncoder.encode(adminPassword), "Coordinador de movilidad"));
        }
        if (!userRepository.existsByEmail(citizenEmail)) {
            userRepository.save(new Citizen("Ciudadano de prueba", citizenEmail,
                    passwordEncoder.encode(citizenPassword), "3000000000"));
        }
        if (mobilityEntityRepository.count() == 0) {
            mobilityEntityRepository.saveAll(List.of(
                    new MobilityEntity("Secretaría de Tránsito y Transporte", "Zona urbana"),
                    new MobilityEntity("Secretaría de Infraestructura", "Vías y pavimentos"),
                    new MobilityEntity("Gestión del Riesgo de Desastres", "Inundaciones y emergencias"),
                    new MobilityEntity("Empresa de Semaforización", "Semáforos y señalización")));
        }
    }
}
