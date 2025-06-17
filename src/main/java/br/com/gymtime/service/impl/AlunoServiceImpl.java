package br.com.gymtime.service.impl;

import br.com.gymtime.dto.*;
import br.com.gymtime.exception.CpfAlreadyExistsException;
import br.com.gymtime.exception.EmailAlreadyExistsException;
import br.com.gymtime.exception.ResourceNotFoundException;
import br.com.gymtime.model.Aluno;
import br.com.gymtime.model.Exercicio;
import br.com.gymtime.model.Treino;
import br.com.gymtime.repository.AlunoRepository;
import br.com.gymtime.service.AlunoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação da camada de serviço para a entidade Aluno.
 * Contém a lógica de negócio para manipulação de dados de alunos.
 */
@Service
@RequiredArgsConstructor // Anotação do Lombok que cria um construtor com os campos 'final'.
public class AlunoServiceImpl implements AlunoService {

    private static final Logger logger = LoggerFactory.getLogger(AlunoServiceImpl.class);
    private final AlunoRepository alunoRepository;

    /**
     * Converte uma entidade {@link Aluno} para seu DTO de resposta {@link AlunoResponseDTO}.
     * @param aluno A entidade a ser convertida.
     * @return O DTO correspondente.
     */
    private AlunoResponseDTO convertToAlunoResponseDTO(Aluno aluno) {
        if (aluno == null) {
            return null;
        }

        List<TreinoResponseDTO> treinoDTOs = (aluno.getTreinos() == null)
                ? Collections.emptyList()
                : aluno.getTreinos().stream().map(this::convertToTreinoResponseDTO).collect(Collectors.toList());

        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getTelefone(),
                aluno.getCpf(),
                treinoDTOs
        );
    }

    /**
     * Converte uma entidade {@link Treino} para seu DTO de resposta {@link TreinoResponseDTO}.
     * @param treino A entidade a ser convertida.
     * @return O DTO correspondente.
     */
    private TreinoResponseDTO convertToTreinoResponseDTO(Treino treino) {
        if (treino == null) {
            return null;
        }

        List<ExercicioResponseDTO> exercicioDTOs = (treino.getExercicios() == null)
                ? Collections.emptyList()
                : treino.getExercicios().stream()
                .map(ex -> new ExercicioResponseDTO(ex.getId(), ex.getNomeExercicio(), ex.getSeriesRepeticoes()))
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

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public AlunoResponseDTO createAluno(AlunoCreateDTO alunoCreateDTO) {
        logger.debug("Iniciando processo de criação de aluno com email: {}", alunoCreateDTO.email());

        validateEmailUniqueness(alunoCreateDTO.email());
        validateCpfUniqueness(alunoCreateDTO.cpf());

        Aluno aluno = new Aluno();
        aluno.setNome(alunoCreateDTO.nome());
        aluno.setEmail(alunoCreateDTO.email());
        aluno.setTelefone(alunoCreateDTO.telefone());
        aluno.setCpf(alunoCreateDTO.cpf());

        Aluno savedAluno = alunoRepository.save(aluno);
        logger.info("Aluno criado com sucesso. ID: {}", savedAluno.getId());
        return convertToAlunoResponseDTO(savedAluno);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public List<AlunoResponseDTO> getAllAlunos() {
        return alunoRepository.findAll().stream()
                .map(this::convertToAlunoResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public Optional<AlunoResponseDTO> getAlunoById(Long id) {
        return alunoRepository.findById(id)
                .map(this::convertToAlunoResponseDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    @Override
    public Optional<AlunoResponseDTO> getAlunoByEmail(String email) {
        return alunoRepository.findByEmail(email)
                .map(this::convertToAlunoResponseDTO);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public AlunoResponseDTO updateAluno(Long id, AlunoUpdateDTO alunoUpdateDTO) {
        logger.debug("Iniciando processo de atualização para o aluno ID: {}", id);

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + id));

        // Atualiza email se fornecido e diferente do atual
        if (StringUtils.hasText(alunoUpdateDTO.email()) && !alunoUpdateDTO.email().equalsIgnoreCase(aluno.getEmail())) {
            validateEmailUniqueness(alunoUpdateDTO.email());
            aluno.setEmail(alunoUpdateDTO.email());
        }

        // Atualiza CPF se fornecido e diferente do atual
        if (StringUtils.hasText(alunoUpdateDTO.cpf())) {
            String cleanCpf = alunoUpdateDTO.cpf().replaceAll("[^0-9]", "");
            if (!cleanCpf.equals(aluno.getCpf())) {
                validateCpfUniqueness(cleanCpf);
                aluno.setCpf(cleanCpf);
            }
        }

        // Atualiza nome se fornecido
        if (StringUtils.hasText(alunoUpdateDTO.nome())) {
            aluno.setNome(alunoUpdateDTO.nome());
        }

        // Atualiza telefone se fornecido (permite limpar o telefone passando string vazia ou nula)
        if (alunoUpdateDTO.telefone() != null) {
            aluno.setTelefone(alunoUpdateDTO.telefone());
        }

        Aluno updatedAluno = alunoRepository.save(aluno);
        logger.info("Aluno ID: {} atualizado com sucesso.", updatedAluno.getId());
        return convertToAlunoResponseDTO(updatedAluno);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional
    @Override
    public void deleteAluno(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aluno não encontrado com ID: " + id + " para deleção.");
        }
        alunoRepository.deleteById(id);
        logger.info("Aluno com ID: {} deletado com sucesso.", id);
    }

    /**
     * Valida se um email já existe no repositório.
     * @param email O email a ser verificado.
     * @throws EmailAlreadyExistsException se o email já estiver em uso.
     */
    private void validateEmailUniqueness(String email) {
        if (alunoRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("Email '" + email + "' já cadastrado.");
        }
    }

    /**
     * Valida se um CPF já existe no repositório.
     * @param cpf O CPF a ser verificado.
     * @throws CpfAlreadyExistsException se o CPF já estiver em uso.
     * @throws IllegalArgumentException se o CPF não tiver 11 dígitos.
     */
    private void validateCpfUniqueness(String cpf) {
        String cleanCpf = cpf.replaceAll("[^0-9]", "");
        if (cleanCpf.length() != 11) {
            throw new IllegalArgumentException("CPF inválido. Deve conter 11 dígitos.");
        }
        if (alunoRepository.existsByCpf(cleanCpf)) {
            throw new CpfAlreadyExistsException("CPF '" + formatCpfForDisplay(cleanCpf) + "' já cadastrado!");
        }
    }

    /**
     * Formata um CPF para exibição em mensagens de erro.
     * @param cpfDigits O CPF com 11 dígitos.
     * @return O CPF formatado (ex: "123.456.789-01").
     */
    private String formatCpfForDisplay(String cpfDigits) {
        if (cpfDigits != null && cpfDigits.matches("\\d{11}")) {
            return cpfDigits.substring(0, 3) + "." +
                    cpfDigits.substring(3, 6) + "." +
                    cpfDigits.substring(6, 9) + "-" +
                    cpfDigits.substring(9, 11);
        }
        return cpfDigits;
    }
}
