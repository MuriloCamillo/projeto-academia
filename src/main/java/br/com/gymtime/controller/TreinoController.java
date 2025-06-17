package br.com.gymtime.controller;

import br.com.gymtime.dto.TreinoCreateDTO;
import br.com.gymtime.dto.TreinoResponseDTO;
import br.com.gymtime.dto.TreinoUpdateDTO;
import br.com.gymtime.service.TreinoService;
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
 * Controller REST para gerenciar as operações relacionadas a Treinos.
 * Fornece endpoints para criar, listar, buscar, atualizar e deletar treinos.
 */
@RestController
@RequestMapping("/api/v1") // Path base comum para os endpoints
@Tag(name = "Treinos", description = "Endpoints para o gerenciamento de treinos dos alunos")
public class TreinoController {

    private final TreinoService treinoService;

    /**
     * Construtor para injeção de dependência do TreinoService.
     * @param treinoService O serviço que contém a lógica de negócio para treinos.
     */
    @Autowired
    public TreinoController(final TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    /**
     * Cria um novo treino e o associa a um aluno existente.
     * @param treinoCreateDTO DTO contendo os dados do treino e o ID do aluno a ser associado.
     * @return Um ResponseEntity com status 201 (Created), a URI do novo recurso e o treino criado no corpo.
     */
    @Operation(summary = "Cria um novo treino para um aluno",
            description = "Registra um novo treino, incluindo seus exercícios, e o associa a um aluno existente através do 'alunoId' fornecido no corpo da requisição.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Treino criado e associado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TreinoResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Requisição inválida devido a erros de validação nos dados de entrada", content = @Content),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado para o 'alunoId' informado na requisição", content = @Content)
    })
    @PostMapping("/treinos")
    public ResponseEntity<TreinoResponseDTO> createTreino(@Valid @RequestBody TreinoCreateDTO treinoCreateDTO) {
        TreinoResponseDTO novoTreino = treinoService.createTreino(treinoCreateDTO);
        // Monta a URI do novo treino criado para retornar no header "Location"
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/v1/treinos/{id}")
                .buildAndExpand(novoTreino.id()).toUri();
        return ResponseEntity.created(location).body(novoTreino);
    }

    /**
     * Lista todos os treinos de um aluno específico.
     * @param alunoId O ID do aluno cujos treinos serão listados.
     * @return Um ResponseEntity com status 200 (OK) e a lista de treinos do aluno.
     */
    @Operation(summary = "Lista todos os treinos de um aluno específico",
            description = "Recupera uma lista de todos os treinos associados a um aluno, identificado pelo seu ID na URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de treinos recuperada com sucesso",
                    content = { @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = TreinoResponseDTO.class))) }),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado para o ID informado", content = @Content)
    })
    @GetMapping("/alunos/{alunoId}/treinos")
    public ResponseEntity<List<TreinoResponseDTO>> getTreinosByAlunoId(
            @Parameter(description = "ID do aluno para buscar os treinos", required = true, example = "1") @PathVariable Long alunoId) {
        List<TreinoResponseDTO> treinos = treinoService.getTreinosByAlunoId(alunoId);
        return ResponseEntity.ok(treinos);
    }

    /**
     * Busca um treino específico pelo seu ID.
     * @param id O ID do treino a ser buscado.
     * @return Um ResponseEntity com status 200 (OK) e o treino encontrado, ou 404 (Not Found) se não existir.
     */
    @Operation(summary = "Busca um treino pelo seu ID",
            description = "Recupera os detalhes de um treino específico com base no seu ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Treino encontrado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TreinoResponseDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Treino não encontrado para o ID informado", content = @Content)
    })
    @GetMapping("/treinos/{id}")
    public ResponseEntity<TreinoResponseDTO> getTreinoById(
            @Parameter(description = "ID do treino a ser buscado", required = true, example = "1") @PathVariable Long id) {
        return treinoService.getTreinoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Atualiza os dados de um treino existente.
     * A atualização de exercícios substitui a lista de exercícios antiga pela nova lista fornecida.
     * @param id O ID do treino a ser atualizado.
     * @param treinoUpdateDTO DTO com os dados a serem atualizados.
     * @return Um ResponseEntity com status 200 (OK) e o treino com os dados atualizados.
     */
    @Operation(summary = "Atualiza um treino existente",
            description = "Modifica o nome, a descrição e a lista de exercícios de um treino já cadastrado. A lista de exercícios é totalmente substituída.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Treino atualizado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TreinoResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Requisição inválida devido a erros de validação", content = @Content),
            @ApiResponse(responseCode = "404", description = "Treino não encontrado para o ID informado", content = @Content)
    })
    @PutMapping("/treinos/{id}")
    public ResponseEntity<TreinoResponseDTO> updateTreino(
            @Parameter(description = "ID do treino a ser atualizado", required = true, example = "1") @PathVariable Long id,
            @Valid @RequestBody TreinoUpdateDTO treinoUpdateDTO) {
        TreinoResponseDTO treinoAtualizado = treinoService.updateTreino(id, treinoUpdateDTO);
        return ResponseEntity.ok(treinoAtualizado);
    }

    /**
     * Deleta um treino do sistema.
     * O delete remove o treino e, em cascata, todos os seus exercícios associados.
     * @param id O ID do treino a ser deletado.
     * @return Um ResponseEntity com status 204 (No Content) indicando sucesso.
     */
    @Operation(summary = "Deleta um treino pelo ID",
            description = "Remove permanentemente um treino e todos os seus exercícios associados do sistema.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Treino deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Treino não encontrado para o ID informado", content = @Content)
    })
    @DeleteMapping("/treinos/{id}")
    public ResponseEntity<Void> deleteTreino(
            @Parameter(description = "ID do treino a ser deletado", required = true, example = "1") @PathVariable Long id) {
        treinoService.deleteTreino(id);
        return ResponseEntity.noContent().build();
    }
}
