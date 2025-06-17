package br.com.gymtime.service;

import br.com.gymtime.dto.TreinoCreateDTO;
import br.com.gymtime.dto.TreinoResponseDTO;
import br.com.gymtime.dto.TreinoUpdateDTO;
import br.com.gymtime.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Interface que define o contrato para os serviços relacionados à entidade Treino.
 * Esta camada de abstração desacopla a implementação da lógica de negócio
 * dos controllers, permitindo diferentes implementações e facilitando os testes.
 */
public interface TreinoService {

    /**
     * Cria um novo treino no sistema e o associa a um aluno existente.
     *
     * @param treinoCreateDTO DTO contendo os dados para a criação do novo treino, incluindo o ID do aluno.
     * @return O DTO de resposta do treino recém-criado.
     * @throws ResourceNotFoundException se o aluno especificado no DTO não for encontrado.
     */
    TreinoResponseDTO createTreino(TreinoCreateDTO treinoCreateDTO);

    /**
     * Busca todos os treinos associados a um aluno específico.
     *
     * @param alunoId O ID do aluno cujos treinos serão listados.
     * @return Uma lista de {@link TreinoResponseDTO}. Retorna uma lista vazia se o aluno não tiver treinos.
     * @throws ResourceNotFoundException se o aluno com o ID fornecido não for encontrado.
     */
    List<TreinoResponseDTO> getTreinosByAlunoId(Long alunoId);

    /**
     * Busca um treino específico pelo seu ID.
     *
     * @param id O ID único do treino a ser buscado.
     * @return Um {@link Optional} contendo o DTO do treino encontrado, ou um Optional vazio se não for encontrado.
     */
    Optional<TreinoResponseDTO> getTreinoById(Long id);

    /**
     * Busca um treino específico pelo seu ID, mas apenas se ele pertencer ao aluno especificado.
     * Este método é útil para verificações de segurança e autorização.
     *
     * @param treinoId O ID do treino a ser buscado.
     * @param alunoId O ID do aluno que deve ser o "dono" do treino.
     * @return Um {@link Optional} contendo o DTO do treino se ele for encontrado e pertencer ao aluno, ou um Optional vazio caso contrário.
     */
    Optional<TreinoResponseDTO> getTreinoByIdAndAlunoId(Long treinoId, Long alunoId);

    /**
     * Atualiza os dados de um treino existente com base no seu ID.
     * A lista de exercícios do treino é substituída pela nova lista fornecida.
     *
     * @param id O ID do treino a ser atualizado.
     * @param treinoUpdateDTO DTO contendo os novos dados a serem aplicados.
     * @return O DTO do treino com os dados atualizados.
     * @throws ResourceNotFoundException se nenhum treino for encontrado com o ID fornecido.
     */
    TreinoResponseDTO updateTreino(Long id, TreinoUpdateDTO treinoUpdateDTO);

    /**
     * Deleta um treino do sistema com base no seu ID.
     * A implementação deve garantir que os exercícios associados sejam removidos em cascata.
     *
     * @param id O ID do treino a ser deletado.
     * @throws ResourceNotFoundException se nenhum treino for encontrado com o ID fornecido.
     */
    void deleteTreino(Long id);
}
