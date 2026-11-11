package com.alertavia.config;

import com.alertavia.model.Administrator;
import com.alertavia.model.Citizen;
import com.alertavia.model.MobilityEntity;
import com.alertavia.repository.ReportRepository;
import com.alertavia.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;
    private final String adminEmail;
    private final String adminPassword;
    private final String citizenEmail;
    private final String citizenPassword;

    public DataInitializer(UserRepository userRepository, ReportRepository reportRepository,
                           PasswordEncoder passwordEncoder, EntityManager entityManager,
                           @Value("${app.admin.email}") String adminEmail,
                           @Value("${app.admin.password}") String adminPassword,
                           @Value("${app.demo.citizen-email}") String citizenEmail,
                           @Value("${app.demo.citizen-password}") String citizenPassword) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.passwordEncoder = passwordEncoder;
        this.entityManager = entityManager;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.citizenEmail = citizenEmail;
        this.citizenPassword = citizenPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!userRepository.existsByEmailIgnoreCase(adminEmail)) {
            userRepository.save(new Administrator("Administrador AlertaVía", adminEmail,
                    passwordEncoder.encode(adminPassword), "Coordinador de movilidad"));
        }
        if (!userRepository.existsByEmailIgnoreCase(citizenEmail)) {
            userRepository.save(new Citizen("Ciudadano de prueba", citizenEmail,
                    passwordEncoder.encode(citizenPassword), "3000000000"));
        }
        if (reportRepository.findMobilityEntities().isEmpty()) {
            List.of(
                    new MobilityEntity("Secretaría de Tránsito y Transporte", "Zona urbana"),
                    new MobilityEntity("Secretaría de Infraestructura", "Vías y pavimentos"),
                    new MobilityEntity("Gestión del Riesgo de Desastres", "Inundaciones y emergencias"),
                    new MobilityEntity("Empresa de Semaforización", "Semáforos y señalización")
            ).forEach(entityManager::persist);
        }
    }
}
