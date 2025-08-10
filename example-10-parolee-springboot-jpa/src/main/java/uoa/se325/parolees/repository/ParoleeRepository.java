package uoa.se325.parolees.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uoa.se325.parolees.model.Parolee;

public interface ParoleeRepository extends JpaRepository<Parolee, Long> {
    
}
