package br.com.gymtime.controller;

import br.com.gymtime.dto.AlunoDTO;
import br.com.gymtime.service.AlunoService;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/alunos") // Define o path base para os endpoints de alunos
@Tag(name = "Alunos", description = "API para gerenciamento de alunos") // Swagger Tag
public class AlunoController {

    private final AlunoService alunoService;

    @Autowired
    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @Operation(summary = "Cria um novo aluno")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Aluno criado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlunoDTO.AlunoCreateDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado", content = @Content)
    })
    @PostMapping
    public ResponseEntity<AlunoDTO.AlunoCreateDTO> createAluno(@Valid @RequestBody AlunoDTO.AlunoCreateDTO alunoCreateDTO) {
        AlunoDTO.AlunoCreateDTO novoAluno = alunoService.createAluno(alunoCreateDTO);
        // Para retornar a URI do recurso criado no header Location (boa prática REST)
        // É preciso modificar o service para retornar o Aluno com ID para montar a URI, ou o DTO completo.
        // Por simplicidade, retornaremos apenas o DTO de criação no corpo.
        // Se o createAluno retornasse um AlunoDTO com ID:
        // URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        // .path("/{id}")
        // .buildAndExpand(novoAluno.id()) // Supondo que AlunoCreateDTO agora tem id ou você retorna AlunoDTO
        // .toUri();
        // return ResponseEntity.created(location).body(novoAluno);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoAluno);
    }

    @Operation(summary = "Lista todos os alunos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de alunos recuperada",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlunoDTO.class)) })
    })
    @GetMapping
    public ResponseEntity<List<AlunoDTO>> getAllAlunos() {
        List<AlunoDTO> alunos = alunoService.getAllAlunos();
        return ResponseEntity.ok(alunos);
    }

    @Operation(summary = "Busca um aluno pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlunoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<AlunoDTO> getAlunoById(
            @Parameter(description = "ID do aluno a ser buscado") @PathVariable Long id) {
        return alunoService.getAlunoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Busca um aluno pelo Email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno encontrado",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlunoDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado", content = @Content)
    })
    @GetMapping("/email/{email}")
    public ResponseEntity<AlunoDTO> getAlunoByEmail(
            @Parameter(description = "Email do aluno a ser buscado") @PathVariable String email) {
        return alunoService.getAlunoByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Atualiza um aluno existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Aluno atualizado com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AlunoDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email já cadastrado para outro aluno", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<AlunoDTO> updateAluno(
            @Parameter(description = "ID do aluno a ser atualizado") @PathVariable Long id,
            @Valid @RequestBody AlunoDTO.AlunoUpdateDTO alunoUpdateDTO) {
        AlunoDTO alunoAtualizado = alunoService.updateAluno(id, alunoUpdateDTO);
        return ResponseEntity.ok(alunoAtualizado);
    }

    @Operation(summary = "Deleta um aluno pelo ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Aluno deletado com sucesso", content = @Content),
            @ApiResponse(responseCode = "404", description = "Aluno não encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAluno(
            @Parameter(description = "ID do aluno a ser deletado") @PathVariable Long id) {
        alunoService.deleteAluno(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}