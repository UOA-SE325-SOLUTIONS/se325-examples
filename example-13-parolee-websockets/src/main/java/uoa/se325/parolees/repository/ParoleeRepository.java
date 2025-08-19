package uoa.se325.parolees.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uoa.se325.parolees.model.Parolee;

@Repository
public interface ParoleeRepository extends JpaRepository<Parolee, Long> {
    
}
