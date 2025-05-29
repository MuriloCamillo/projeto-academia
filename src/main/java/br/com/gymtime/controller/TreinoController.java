package br.com.gymtime.controller;

import br.com.gymtime.dto.TreinoDTO;
import br.com.gymtime.service.TreinoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
// import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/treinos")
@Tag(name = "Treinos", description = "API para gerenciamento de treinos dos alunos")
public class TreinoController {

    private final TreinoService treinoService;

    @Autowired
    public TreinoController(TreinoService treinoService) {
        this.treinoService = treinoService;
    }

    @Operation(summary = "Cria um novo treino para um aluno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Treino criado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TreinoDTO.class)) }), // Retornando TreinoDTO completo
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado para associar ao treino", content = @Content)
    })
    @PostMapping
    public ResponseEntity<TreinoDTO> createTreino(@Valid @RequestBody TreinoDTO.TreinoCreateDTO treinoCreateDTO) {
        TreinoDTO novoTreino = treinoService.createTreino(treinoCreateDTO);
        // URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        //         .path("/{id}")
        //         .buildAndExpand(novoTreino.id())
        //         .toUri();
        // return ResponseEntity.created(location).body(novoTreino);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoTreino);
    }

    @Operation(summary = "Lista todos os treinos de um aluno específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de treinos recuperada",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TreinoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado", content = @Content)
    })
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<TreinoDTO>> getTreinosByAlunoId(
            @Parameter(description = "ID do aluno cujos treinos serão listados") @PathVariable Long alunoId) {
        List<TreinoDTO> treinos = treinoService.getTreinosByAlunoId(alunoId);
        return ResponseEntity.ok(treinos);
    }

    @Operation(summary = "Busca um treino pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Treino encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TreinoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Treino não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<TreinoDTO> getTreinoById(
            @Parameter(description = "ID do treino a ser buscado") @PathVariable Long id) {
        return treinoService.getTreinoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Atualiza um treino existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Treino atualizado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TreinoDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Treino não encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<TreinoDTO> updateTreino(
            @Parameter(description = "ID do treino a ser atualizado")
            @PathVariable Long id,
            @Valid @RequestBody TreinoDTO.TreinoUpdateDTO treinoUpdateDTO) {
        TreinoDTO treinoAtualizado = treinoService.updateTreino(id, treinoUpdateDTO);
        return ResponseEntity.ok(treinoAtualizado);
    }

    @Operation(summary = "Deleta um treino pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Treino deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Treino não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTreino(
            @Parameter(description = "ID do treino a ser deletado") @PathVariable Long id) {
        treinoService.deleteTreino(id);
        return ResponseEntity.noContent().build();
    }
}