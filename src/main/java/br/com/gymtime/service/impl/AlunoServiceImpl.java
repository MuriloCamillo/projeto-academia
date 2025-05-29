package br.com.gymtime.service.impl;

import br.com.gymtime.dto.AlunoDTO;
import br.com.gymtime.dto.TreinoDTO;
import br.com.gymtime.exception.ResourceNotFoundException;
import br.com.gymtime.exception.EmailAlreadyExistsException;
import br.com.gymtime.model.Aluno;
import br.com.gymtime.repository.AlunoRepository;
import br.com.gymtime.service.AlunoService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlunoServiceImpl implements AlunoService {

    private final AlunoRepository alunoRepository;

    @Autowired
    public AlunoServiceImpl(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
    }

    // Método de conversão Entidade para DTO
    private AlunoDTO convertToAlunoDTO(Aluno aluno) {
        if (aluno == null) return null;
        List<TreinoDTO> treinoDTOs = aluno.getTreinos() != null ?
                aluno.getTreinos().stream()
                        .map(treino -> new TreinoDTO(
                                treino.getId(),
                                treino.getNome(),
                                treino.getDescricao(),
                                treino.getDataCriacao(),
                                treino.getDataAtualizacao(),
                                treino.getAluno().getId())) // Evita carregar Aluno completo aqui
                        .collect(Collectors.toList()) :
                List.of(); // Lista vazia se não houver treinos

        return new AlunoDTO(
                aluno.getId(),
                aluno.getNome(),
                aluno.getEmail(),
                aluno.getTelefone(),
                treinoDTOs
        );
    }


    @Transactional
    @Override
    public AlunoDTO.AlunoCreateDTO createAluno(AlunoDTO.AlunoCreateDTO alunoCreateDTO) {
        if (alunoRepository.existsByEmail(alunoCreateDTO.email())) {
            throw new EmailAlreadyExistsException("Email '" + alunoCreateDTO.email() + "' já cadastrado.");
        }
        Aluno aluno = new Aluno();
        BeanUtils.copyProperties(alunoCreateDTO, aluno); // Copia propriedades com nomes iguais
        Aluno savedAluno = alunoRepository.save(aluno);
        // Retornar o DTO de criação ou um DTO completo, dependendo da necessidade
        return new AlunoDTO.AlunoCreateDTO(savedAluno.getNome(), savedAluno.getEmail(), savedAluno.getTelefone());
    }

    @Transactional(readOnly = true)
    @Override
    public List<AlunoDTO> getAllAlunos() {
        return alunoRepository.findAll().stream()
                .map(this::convertToAlunoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AlunoDTO> getAlunoById(Long id) {
        return alunoRepository.findById(id)
                .map(this::convertToAlunoDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<AlunoDTO> getAlunoByEmail(String email) {
        return alunoRepository.findByEmail(email)
                .map(this::convertToAlunoDTO);
    }

    @Transactional
    @Override
    public AlunoDTO updateAluno(Long id, AlunoDTO.AlunoUpdateDTO alunoUpdateDTO) {
        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + id));

        // Verifica se o novo email (se fornecido) já existe para outro aluno
        if (alunoUpdateDTO.email() != null && !alunoUpdateDTO.email().equals(aluno.getEmail()) && alunoRepository.existsByEmail(alunoUpdateDTO.email())) {
            throw new EmailAlreadyExistsException("Email '" + alunoUpdateDTO.email() + "' já cadastrado para outro aluno.");
        }

        // Atualiza apenas os campos não nulos do DTO
        if (alunoUpdateDTO.nome() != null) {
            aluno.setNome(alunoUpdateDTO.nome());
        }
        if (alunoUpdateDTO.email() != null) {
            aluno.setEmail(alunoUpdateDTO.email());
        }
        if (alunoUpdateDTO.telefone() != null) {
            aluno.setTelefone(alunoUpdateDTO.telefone());
        }

        Aluno updatedAluno = alunoRepository.save(aluno);
        return convertToAlunoDTO(updatedAluno);
    }

    @Transactional
    @Override
    public void deleteAluno(Long id) {
        if (!alunoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Aluno não encontrado com ID: " + id);
        }
        // A cascade ALL no relacionamento com Treino cuidará de remover os treinos associados
        alunoRepository.deleteById(id);
    }
}