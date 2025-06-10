package br.com.gymtime.repository;

import br.com.gymtime.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    Optional<Aluno> findByEmail(String email);
    boolean existsByEmail(String email);

    // Novos métodos para CPF
    Optional<Aluno> findByCpf(String cpf);
    boolean existsByCpf(String cpf);
}