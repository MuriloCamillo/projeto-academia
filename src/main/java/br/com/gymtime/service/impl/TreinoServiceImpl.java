package br.com.gymtime.service.impl;

import br.com.gymtime.dto.ExercicioCreateDTO;
import br.com.gymtime.dto.ExercicioResponseDTO;
import br.com.gymtime.dto.TreinoCreateDTO;
import br.com.gymtime.dto.TreinoResponseDTO;
import br.com.gymtime.dto.TreinoUpdateDTO;
import br.com.gymtime.exception.ResourceNotFoundException;
import br.com.gymtime.model.Aluno;
import br.com.gymtime.model.Exercicio;
import br.com.gymtime.model.Treino;
import br.com.gymtime.repository.AlunoRepository;
import br.com.gymtime.repository.TreinoRepository;
import br.com.gymtime.service.TreinoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação da camada de serviço para a entidade Treino.
 * Contém a lógica de negócio para manipulação de dados de treinos.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TreinoServiceImpl implements TreinoService {

    private final TreinoRepository treinoRepository;
    private final AlunoRepository alunoRepository;

    /**
     * Converte uma entidade {@link Exercicio} para seu DTO de resposta {@link ExercicioResponseDTO}.
     */
    private ExercicioResponseDTO convertToExercicioResponseDTO(Exercicio exercicio) {
        if (exercicio == null) return null;
        return new ExercicioResponseDTO(
                exercicio.getId(),
                exercicio.getNomeExercicio(),
                exercicio.getSeriesRepeticoes()
        );
    }

    /**
     * Converte uma entidade {@link Treino} para seu DTO de resposta {@link TreinoResponseDTO}.
     */
    private TreinoResponseDTO convertToTreinoResponseDTO(Treino treino) {
        if (treino == null) return null;

        List<ExercicioResponseDTO> exercicioDTOs = treino.getExercicios() == null ? Collections.emptyList()
                : treino.getExercicios().stream()
                .map(this::convertToExercicioResponseDTO)
                .collect(Collectors.toList());

        return new TreinoResponseDTO(
                treino.getId(),
                treino.getNome(),
                treino.getDescricao(),
                treino.getDataCriacao(),
                treino.getDataAtualizacao(),
                treino.getAluno() != null ? treino.getAluno().getId() : null,
                exercicioDTOs
        );
    }

    @Transactional
    @Override
    public TreinoResponseDTO createTreino(TreinoCreateDTO treinoCreateDTO) {
        log.debug("Iniciando criação de treino para o aluno ID: {}", treinoCreateDTO.alunoId());
        Aluno aluno = alunoRepository.findById(treinoCreateDTO.alunoId())
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + treinoCreateDTO.alunoId() + " para associar ao treino."));

        Treino treino = new Treino();
        treino.setNome(treinoCreateDTO.nome());
        treino.setDescricao(treinoCreateDTO.descricao());
        treino.setAluno(aluno); // Associa o treino ao aluno

        // Adiciona os exercícios ao treino
        treinoCreateDTO.exercicios().stream()
                .filter(exDTO -> StringUtils.hasText(exDTO.getNomeExercicio()))
                .map(exDTO -> new Exercicio(exDTO.getNomeExercicio(), exDTO.getSeriesRepeticoes()))
                .forEach(treino::addExercicio);

        Treino savedTreino = treinoRepository.save(treino);
        log.info("Treino ID {} criado com sucesso para o aluno ID {}.", savedTreino.getId(), aluno.getId());
        return convertToTreinoResponseDTO(savedTreino);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TreinoResponseDTO> getTreinosByAlunoId(Long alunoId) {
        if (!alunoRepository.existsById(alunoId)) {
            throw new ResourceNotFoundException("Não é possível listar treinos. Aluno não encontrado com ID: " + alunoId);
        }
        return treinoRepository.findByAlunoId(alunoId).stream()
                .map(this::convertToTreinoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<TreinoResponseDTO> getTreinoById(Long id) {
        return treinoRepository.findById(id)
                .map(this::convertToTreinoResponseDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<TreinoResponseDTO> getTreinoByIdAndAlunoId(Long treinoId, Long alunoId) {
        return treinoRepository.findById(treinoId)
                // Garante que o treino pertence ao aluno especificado
                .filter(treino -> treino.getAluno() != null && treino.getAluno().getId().equals(alunoId))
                .map(this::convertToTreinoResponseDTO);
    }

    @Transactional
    @Override
    public TreinoResponseDTO updateTreino(Long id, TreinoUpdateDTO treinoUpdateDTO) {
        log.debug("Iniciando atualização do treino ID: {}", id);
        Treino treino = treinoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Treino não encontrado com ID: " + id));

        // Atualiza os campos do treino se eles forem fornecidos no DTO
        if (StringUtils.hasText(treinoUpdateDTO.nome())) {
            treino.setNome(treinoUpdateDTO.nome());
        }
        if (treinoUpdateDTO.descricao() != null) {
            treino.setDescricao(treinoUpdateDTO.descricao());
        }

        treino.getExercicios().clear();
        treinoUpdateDTO.exercicios().stream()
                .filter(exDTO -> StringUtils.hasText(exDTO.getNomeExercicio()))
                .map(exDTO -> new Exercicio(exDTO.getNomeExercicio(), exDTO.getSeriesRepeticoes()))
                .forEach(treino::addExercicio);

        Treino updatedTreino = treinoRepository.save(treino);
        log.info("Treino ID {} atualizado com sucesso.", updatedTreino.getId());
        return convertToTreinoResponseDTO(updatedTreino);
    }

    @Transactional
    @Override
    public void deleteTreino(Long id) {
        log.debug("Iniciando deleção do treino ID: {}", id);
        // Verifica se o treino existe antes de deletar para fornecer uma exceção clara.
        if (!treinoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Treino não encontrado com id: " + id);
        }
        // A deleção do treino irá remover os exercícios associados em cascata
        // devido à configuração `cascade = CascadeType.ALL` na entidade Treino.
        treinoRepository.deleteById(id);
        log.info("Treino com ID: {} deletado com sucesso.", id);
    }
}
