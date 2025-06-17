package br.com.gymtime.repository;

import br.com.gymtime.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Interface de repositório para a entidade {@link Aluno}.
 * Estende {@link JpaRepository} para herdar métodos CRUD padrão (create, read, update, delete)
 * e funcionalidades de paginação e ordenação para a entidade Aluno.
 * O Spring Data JPA implementará esta interface automaticamente em tempo de execução.
 */
@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    /**
     * Busca um aluno pelo seu endereço de e-mail.
     * O Spring Data JPA gera a consulta a partir do nome do método.
     *
     * @param email O e-mail a ser buscado.
     * @return Um {@link Optional} contendo o {@link Aluno} encontrado, ou um Optional vazio se nenhum aluno for encontrado com o e-mail fornecido.
     */
    Optional<Aluno> findByEmail(String email);

    /**
     * Verifica se já existe um aluno com o e-mail fornecido.
     *
     * @param email O e-mail a ser verificado.
     * @return {@code true} se um aluno com o e-mail existir, {@code false} caso contrário.
     */
    boolean existsByEmail(String email);

    /**
     * Busca um aluno pelo seu número de CPF.
     * O Spring Data JPA gera a consulta a partir do nome do método.
     *
     * @param cpf O CPF (apenas dígitos) a ser buscado.
     * @return Um {@link Optional} contendo o {@link Aluno} encontrado, ou um Optional vazio se nenhum aluno for encontrado com o CPF fornecido.
     */
    Optional<Aluno> findByCpf(String cpf);

    /**
     * Verifica se já existe um aluno com o CPF fornecido.
     *
     * @param cpf O CPF (apenas dígitos) a ser verificado.
     * @return {@code true} se um aluno com o CPF existir, {@code false} caso contrário.
     */
    boolean existsByCpf(String cpf);
}
