package br.com.gymtime.service.impl;

import br.com.gymtime.dto.*; // Importa todos os DTOs do pacote
import br.com.gymtime.exception.CpfAlreadyExistsException;
import br.com.gymtime.exception.EmailAlreadyExistsException;
import br.com.gymtime.exception.ResourceNotFoundException;
import br.com.gymtime.model.Aluno;
import br.com.gymtime.model.Exercicio;
import br.com.gymtime.model.Treino;
import br.com.gymtime.repository.AlunoRepository;
import br.com.gymtime.service.AlunoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlunoServiceImpl implements AlunoService {

    private static final Logger logger = LoggerFactory.getLogger(AlunoServiceImpl.class);
    private final AlunoRepository alunoRepository;

    @Autowired
    public AlunoServiceImpl(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    private AlunoResponseDTO convertToAlunoResponseDTO(Aluno aluno) {
        if (aluno == null) {
            return null;
        }

        List<TreinoResponseDTO> treinoResponseDTOs = Collections.emptyList();
        if (aluno.getTreinos() != null && !aluno.getTreinos().isEmpty()) {
            treinoResponseDTOs = aluno.getTreinos().stream()
                    .map(treino -> {
                        List<ExercicioResponseDTO> exercicioDTOs = Collections.emptyList();
                        if (treino.getExercicios() != null) {
                            exercicioDTOs = treino.getExercicios().stream()
                                    .map(ex -> new ExercicioResponseDTO(ex.getId(), ex.getNomeExercicio(), ex.getSeriesRepeticoes()))
                                    .collect(Collectors.toList());
                        }
                        return new TreinoResponseDTO(
                                treino.getId(),
                                treino.getNome(),
                                treino.getDescricao(),
                                treino.getDataCriacao(),
                                treino.getDataAtualizacao(),
                                aluno.getId(),
                                exercicioDTOs
                        );
                    })
                    .collect(Collectors.toList());
        }

        return new AlunoResponseDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getTelefone(), // Vem da entidade (apenas dígitos)
                aluno.getCpf(),      // Vem da entidade (apenas dígitos)
                treinoResponseDTOs
        );
    }

    @Transactional
    @Override
    public AlunoResponseDTO createAluno(AlunoCreateDTO alunoCreateDTO) {
        logger.debug("Tentando criar aluno com DTO: {}", alunoCreateDTO);
        if (alunoRepository.existsByEmail(alunoCreateDTO.email())) {
            throw new EmailAlreadyExistsException("Email '" + alunoCreateDTO.email() + "' já cadastrado.");
        }

        String cpfApenasDigitos = alunoCreateDTO.cpf().replaceAll("[^0-9]", "");
        if (!cpfApenasDigitos.matches("\\d{11}")) { // Validação extra no service
            throw new IllegalArgumentException("CPF fornecido para criação não contém 11 dígitos após limpeza.");
        }
        if (alunoRepository.existsByCpf(cpfApenasDigitos)) {
            throw new CpfAlreadyExistsException("CPF '" + formatCpfForDisplay(cpfApenasDigitos) + "' já cadastrado!");
        }

        String telefoneApenasDigitos = alunoCreateDTO.telefone() != null ? alunoCreateDTO.telefone().replaceAll("[^0-9]", "") : null;
        if (telefoneApenasDigitos != null && !telefoneApenasDigitos.isEmpty() && !telefoneApenasDigitos.matches("\\d{10,11}")) {
            throw new IllegalArgumentException("Telefone fornecido para criação não contém 10 ou 11 dígitos após limpeza.");
        }


        Aluno aluno = new Aluno();
        aluno.setNome(alunoCreateDTO.nome());
        aluno.setEmail(alunoCreateDTO.email());
        aluno.setTelefone(telefoneApenasDigitos); // Salva apenas dígitos (ou null)
        aluno.setCpf(cpfApenasDigitos);         // Salva apenas dígitos

        Aluno savedAluno = alunoRepository.save(aluno);
        logger.info("Aluno criado com ID: {}", savedAluno.getId());
        return convertToAlunoResponseDTO(savedAluno);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AlunoResponseDTO> getAllAlunos() {
        return alunoRepository.findAll().stream()
                .map(this::convertToAlunoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AlunoResponseDTO> getAlunoById(Long id) {
        return alunoRepository.findById(id)
                .map(this::convertToAlunoResponseDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AlunoResponseDTO> getAlunoByEmail(String email) {
        return alunoRepository.findByEmail(email)
                .map(this::convertToAlunoResponseDTO);
    }

    @Transactional
    @Override
    public AlunoResponseDTO updateAluno(Long id, AlunoUpdateDTO alunoUpdateDTO) {
        logger.debug("Tentando atualizar aluno ID {} com DTO: {}", id, alunoUpdateDTO);
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + id));

        // Email
        if (alunoUpdateDTO.email() != null && !alunoUpdateDTO.email().isEmpty() &&
                !alunoUpdateDTO.email().equalsIgnoreCase(aluno.getEmail())) {
            if (alunoRepository.existsByEmail(alunoUpdateDTO.email())) {
                throw new EmailAlreadyExistsException("Email '" + alunoUpdateDTO.email() + "' já cadastrado para outro aluno.");
            }
            aluno.setEmail(alunoUpdateDTO.email());
        }

        // CPF
        if (alunoUpdateDTO.cpf() != null && !alunoUpdateDTO.cpf().isEmpty()) {
            String cpfApenasDigitosUpdate = alunoUpdateDTO.cpf().replaceAll("[^0-9]", "");
            if (!cpfApenasDigitosUpdate.matches("\\d{11}")) {
                throw new IllegalArgumentException("CPF fornecido para atualização não contém 11 dígitos após limpeza.");
            }
            if (!cpfApenasDigitosUpdate.equals(aluno.getCpf())) {
                if (alunoRepository.existsByCpf(cpfApenasDigitosUpdate)) {
                    throw new CpfAlreadyExistsException("CPF '" + formatCpfForDisplay(cpfApenasDigitosUpdate) + "' já cadastrado para outro aluno!");
                }
                aluno.setCpf(cpfApenasDigitosUpdate);
            }
        }

        // Nome
        if (alunoUpdateDTO.nome() != null && !alunoUpdateDTO.nome().isEmpty()) {
            aluno.setNome(alunoUpdateDTO.nome());
        }

        // Telefone
        if (alunoUpdateDTO.telefone() != null) {
            String telefoneApenasDigitosUpdate = alunoUpdateDTO.telefone().replaceAll("[^0-9]", "");
            if (!telefoneApenasDigitosUpdate.isEmpty() && !telefoneApenasDigitosUpdate.matches("\\d{10,11}")) {
                throw new IllegalArgumentException("Telefone fornecido para atualização não contém 10 ou 11 dígitos após limpeza.");
            }
            aluno.setTelefone(telefoneApenasDigitosUpdate.isEmpty() ? null : telefoneApenasDigitosUpdate);
        }


        Aluno updatedAluno = alunoRepository.save(aluno);
        logger.info("Aluno atualizado com ID: {}", updatedAluno.getId());
        return convertToAlunoResponseDTO(updatedAluno);
    }

    @Transactional
    @Override
    public void deleteAluno(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aluno não encontrado com ID: " + id);
        }
        alunoRepository.deleteById(id);
        logger.info("Aluno deletado com ID: {}", id);
    }

    // Método auxiliar para formatar CPF para mensagens de erro (opcional)
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