package com.clinicmanager.infrastructure.persistence.patient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPatientRepository extends JpaRepository<PatientEntity, String> {
    Optional<PatientEntity> findByPhone(String phone);
    Optional<PatientEntity> findByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    List<PatientEntity> findByFullNameContainingIgnoreCaseAndPhoneContaining(String name, String phone);
}
