package br.com.gymtime.repository;

import br.com.gymtime.model.Treino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Interface de repositório para a entidade {@link Treino}.
 * Estende {@link JpaRepository} para herdar métodos CRUD padrão para a entidade Treino.
 * O Spring Data JPA implementará esta interface automaticamente em tempo de execução.
 */
@Repository
public interface TreinoRepository extends JpaRepository<Treino, Long> {

    /**
     * Busca todos os treinos associados a um aluno específico pelo ID do aluno.
     * O Spring Data JPA gera a consulta baseado no nome do método, procurando pela propriedade 'id'
     * dentro da propriedade 'aluno' da entidade Treino.
     *
     * @param alunoId O ID do aluno cujos treinos serão buscados.
     * @return Uma {@link List} de {@link Treino} pertencentes ao aluno. Retorna uma lista vazia se o aluno não tiver treinos.
     */
    List<Treino> findByAlunoId(Long alunoId);
}
