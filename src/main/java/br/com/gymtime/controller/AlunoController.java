package br.com.gymtime.controller;

import br.com.gymtime.dto.AlunoCreateDTO;
import br.com.gymtime.dto.AlunoResponseDTO;
import br.com.gymtime.dto.AlunoUpdateDTO;
import br.com.gymtime.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Controller REST para gerenciar as operações relacionadas a Alunos.
 * Fornece endpoints para criar, listar, buscar, atualizar e deletar alunos.
 */
@RestController
@RequestMapping("/api/v1/alunos")
@Tag(name = "Alunos", description = "Endpoints para o gerenciamento de alunos")
public class AlunoController {

    private final AlunoService alunoService;

    /**
     * Construtor para injeção de dependência do AlunoService.
     * @param alunoService O serviço que contém a lógica de negócio para alunos.
     */
    @Autowired
    public AlunoController(final AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    /**
     * Cria um novo aluno no sistema.
     * @param alunoCreateDTO DTO contendo os dados para a criação do novo aluno.
     * @return Um ResponseEntity com status 201 (Created), o location do novo recurso no header
     * e o DTO do aluno criado no corpo da resposta.
     */
    @Operation(summary = "Cria um novo aluno",
            description = "Registra um novo aluno no banco de dados. O email e o CPF devem ser únicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aluno criado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlunoResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Requisição inválida devido a erros de validação nos dados de entrada", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito: o Email ou CPF informado já está cadastrado no sistema", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AlunoResponseDTO> createAluno(@Valid @RequestBody AlunoCreateDTO alunoCreateDTO) {
        AlunoResponseDTO novoAluno = alunoService.createAluno(alunoCreateDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(novoAluno.id()).toUri();
        return ResponseEntity.created(location).body(novoAluno);
    }

    /**
     * Retorna uma lista com todos os alunos cadastrados.
     * @return Um ResponseEntity com status 200 (OK) e a lista de alunos no corpo da resposta.
     */
    @Operation(summary = "Lista todos os alunos",
            description = "Recupera uma lista de todos os alunos cadastrados no sistema, incluindo seus treinos associados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alunos recuperada com sucesso",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = AlunoResponseDTO.class))) })
    })
    @GetMapping
    public ResponseEntity<List<AlunoResponseDTO>> getAllAlunos() {
        List<AlunoResponseDTO> alunos = alunoService.getAllAlunos();
        return ResponseEntity.ok(alunos);
    }

    /**
     * Busca um aluno específico pelo seu ID.
     * @param id O ID único do aluno a ser buscado.
     * @return Um ResponseEntity com status 200 (OK) e o aluno encontrado, ou 404 (Not Found) se o aluno não existir.
     */
    @Operation(summary = "Busca um aluno pelo ID",
            description = "Recupera os detalhes de um aluno específico com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno encontrado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlunoResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado para o ID informado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> getAlunoById(
            @Parameter(description = "ID do aluno a ser buscado", required = true, example = "1") @PathVariable Long id) {
        return alunoService.getAlunoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Busca um aluno específico pelo seu endereço de e-mail.
     * @param email O e-mail único do aluno a ser buscado.
     * @return Um ResponseEntity com status 200 (OK) e o aluno encontrado, ou 404 (Not Found) se o aluno não existir.
     */
    @Operation(summary = "Busca um aluno pelo Email",
            description = "Recupera os detalhes de um aluno específico com base no seu endereço de email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno encontrado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlunoResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado para o email informado", content = @Content)
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<AlunoResponseDTO> getAlunoByEmail(
            @Parameter(description = "Email do aluno a ser buscado", required = true, example = "joao.silva@email.com") @PathVariable String email) {
        return alunoService.getAlunoByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Atualiza os dados de um aluno existente.
     * @param id O ID do aluno a ser atualizado.
     * @param alunoUpdateDTO DTO com os dados a serem atualizados. Campos não fornecidos não serão alterados.
     * @return Um ResponseEntity com status 200 (OK) e o DTO do aluno com os dados atualizados.
     */
    @Operation(summary = "Atualiza um aluno existente",
            description = "Modifica os dados de um aluno já cadastrado. O email e o CPF, se alterados, devem ser únicos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlunoResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Requisição inválida devido a erros de validação", content = @Content),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado para o ID informado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Conflito: o novo Email ou CPF já está em uso por outro aluno", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> updateAluno(
            @Parameter(description = "ID do aluno a ser atualizado", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody AlunoUpdateDTO alunoUpdateDTO) {
        AlunoResponseDTO alunoAtualizado = alunoService.updateAluno(id, alunoUpdateDTO);
        return ResponseEntity.ok(alunoAtualizado);
    }

    /**
     * Deleta um aluno do sistema.
     * O delete é em cascata e removerá também todos os treinos associados a este aluno.
     * @param id O ID do aluno a ser deletado.
     * @return Um ResponseEntity com status 204 (No Content) indicando sucesso na operação.
     */
    @Operation(summary = "Deleta um aluno pelo ID",
            description = "Remove permanentemente um aluno e todos os seus dados associados (treinos e exercícios) do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Aluno deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado para o ID informado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAluno(
            @Parameter(description = "ID do aluno a ser deletado", required = true, example = "1") @PathVariable Long id) {
        alunoService.deleteAluno(id);
        return ResponseEntity.noContent().build();
    }
}
