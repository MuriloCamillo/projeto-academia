package br.com.gymtime.repository;

import br.com.gymtime.model.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TreinoRepository extends JpaRepository<Treino, Long> {
    // Busca todos os treinos de um aluno específico
    List<Treino> findByAlunoId(Long alunoId);

    // Você pode adicionar outras consultas personalizadas aqui, se necessário
    // Ex: List<Treino> findByAlunoIdAndNomeContainingIgnoreCase(Long alunoId, String nome);
}