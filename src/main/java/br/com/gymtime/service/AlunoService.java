package br.com.gymtime.service;

import br.com.gymtime.dto.AlunoCreateDTO;
import br.com.gymtime.dto.AlunoResponseDTO;
import br.com.gymtime.dto.AlunoUpdateDTO;
import br.com.gymtime.exception.CpfAlreadyExistsException;
import br.com.gymtime.exception.EmailAlreadyExistsException;
import br.com.gymtime.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Interface que define o contrato para os serviços relacionados à entidade Aluno.
 * Esta camada de abstração desacopla a implementação da lógica de negócio
 * dos controllers, permitindo diferentes implementações e facilitando os testes.
 */
public interface AlunoService {

    /**
     * Cria um novo aluno no sistema com base nos dados fornecidos.
     *
     * @param alunoCreateDTO DTO contendo os dados para a criação do novo aluno.
     * @return O DTO de resposta do aluno recém-criado.
     * @throws EmailAlreadyExistsException se o email fornecido já estiver em uso por outro aluno.
     * @throws CpfAlreadyExistsException se o CPF fornecido já estiver em uso por outro aluno.
     */
    AlunoResponseDTO createAluno(AlunoCreateDTO alunoCreateDTO);

    /**
     * Retorna uma lista com todos os alunos cadastrados no sistema.
     *
     * @return Uma lista de {@link AlunoResponseDTO}. A lista estará vazia se não houver alunos.
     */
    List<AlunoResponseDTO> getAllAlunos();

    /**
     * Busca um aluno específico pelo seu ID.
     *
     * @param id O ID único do aluno a ser buscado.
     * @return Um {@link Optional} contendo o DTO do aluno encontrado, ou um Optional vazio se não for encontrado.
     */
    Optional<AlunoResponseDTO> getAlunoById(Long id);

    /**
     * Busca um aluno específico pelo seu endereço de e-mail.
     *
     * @param email O e-mail único do aluno a ser buscado.
     * @return Um {@link Optional} contendo o DTO do aluno encontrado, ou um Optional vazio se não for encontrado.
     */
    Optional<AlunoResponseDTO> getAlunoByEmail(String email);

    /**
     * Atualiza os dados de um aluno existente com base no seu ID.
     *
     * @param id O ID do aluno a ser atualizado.
     * @param alunoUpdateDTO DTO contendo os novos dados a serem aplicados.
     * @return O DTO do aluno com os dados atualizados.
     * @throws ResourceNotFoundException se nenhum aluno for encontrado com o ID fornecido.
     * @throws EmailAlreadyExistsException se o novo email fornecido já pertencer a outro aluno.
     * @throws CpfAlreadyExistsException se o novo CPF fornecido já pertencer a outro aluno.
     */
    AlunoResponseDTO updateAluno(Long id, AlunoUpdateDTO alunoUpdateDTO);

    /**
     * Deleta um aluno do sistema com base no seu ID.
     * A implementação deve garantir que os dados associados (como treinos) também sejam removidos.
     *
     * @param id O ID do aluno a ser deletado.
     * @throws ResourceNotFoundException se nenhum aluno for encontrado com o ID fornecido.
     */
    void deleteAluno(Long id);
}
