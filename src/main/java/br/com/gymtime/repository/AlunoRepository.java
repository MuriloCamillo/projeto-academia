package br.com.gymtime.repository;

import br.com.gymtime.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    // Spring Data JPA infere a query pelo nome do metodo
    Optional<Aluno> findByEmail(String email);
    boolean existsByEmail(String email);
}