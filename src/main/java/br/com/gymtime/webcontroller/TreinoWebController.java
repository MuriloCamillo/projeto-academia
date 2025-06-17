package br.com.gymtime.webcontroller;

import br.com.gymtime.dto.AlunoResponseDTO;
import br.com.gymtime.dto.ExercicioCreateDTO;
import br.com.gymtime.dto.TreinoCreateDTO;
import br.com.gymtime.dto.TreinoResponseDTO;
import br.com.gymtime.dto.TreinoUpdateDTO;
import br.com.gymtime.service.AlunoService;
import br.com.gymtime.service.TreinoService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller para gerenciar as interações web (Thymeleaf) relacionadas a Treinos.
 * Todos os endpoints são relativos a um aluno específico.
 */
@Controller
@RequestMapping("/web/alunos/{alunoId}/treinos")
@RequiredArgsConstructor
@Slf4j
public class TreinoWebController {

    private final TreinoService treinoService;
    private final AlunoService alunoService;

    /**
     * Exibe a lista de treinos para um aluno específico.
     * @param alunoId O ID do aluno dono dos treinos.
     * @param model O Model para adicionar atributos para a view.
     * @param redirectAttributes Atributos para passar mensagens em caso de redirecionamento.
     * @return O nome do template da lista de treinos ou um redirecionamento se o aluno não for encontrado.
     */
    @GetMapping
    public String listarTreinosDoAluno(@PathVariable Long alunoId, Model model, RedirectAttributes redirectAttributes) {
        log.info("Listando treinos para o aluno ID: {}", alunoId);

        Optional<AlunoResponseDTO> alunoOpt = carregarAlunoParaModel(alunoId, model);
        if (alunoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aluno com ID " + alunoId + " não encontrado.");
            return "redirect:/web/alunos";
        }

        List<TreinoResponseDTO> treinos = treinoService.getTreinosByAlunoId(alunoId);
        model.addAttribute("treinos", treinos);
        return "treinos/lista-treinos";
    }

    /**
     * Exibe o formulário para criar um novo treino para um aluno.
     * @param alunoId O ID do aluno para o qual o treino será criado.
     * @param model O Model para adicionar o objeto do formulário e outros atributos.
     * @return O nome do template do formulário de treino.
     */
    @GetMapping("/novo")
    public String mostrarFormularioNovoTreino(@PathVariable Long alunoId, Model model, RedirectAttributes redirectAttributes) {
        log.info("Exibindo formulário de novo treino para o aluno ID: {}", alunoId);
        Optional<AlunoResponseDTO> alunoOpt = carregarAlunoParaModel(alunoId, model);
        if (alunoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aluno com ID " + alunoId + " não encontrado.");
            return "redirect:/web/alunos";
        }

        // Prepara o DTO para o formulário
        model.addAttribute("treinoForm", new TreinoCreateDTO("", "", alunoId, new ArrayList<>()));
        model.addAttribute("pageTitle", "Novo Treino para " + alunoOpt.get().nome());
        model.addAttribute("treinoId", null); // Indica que é um novo treino
        return "treinos/form-treino";
    }

    /**
     * Processa a submissão do formulário de criação de treino.
     * @param alunoId O ID do aluno associado.
     * @param treinoCreateDTO O objeto do formulário validado.
     * @param bindingResult O resultado da validação.
     * @param model O Model para repopular o formulário em caso de erro.
     * @param redirectAttributes Atributos para mensagens de sucesso/erro.
     * @return Um redirecionamento para a lista de treinos em caso de sucesso, ou a página do formulário em caso de erro.
     */
    @PostMapping("/criar")
    public String criarTreino(@PathVariable Long alunoId,
                              @Valid @ModelAttribute("treinoForm") TreinoCreateDTO treinoCreateDTO,
                              BindingResult bindingResult,
                              Model model, RedirectAttributes redirectAttributes) {
        log.info("Processando criação de treino para o aluno ID: {}", alunoId);

        Optional<AlunoResponseDTO> alunoOpt = carregarAlunoParaModel(alunoId, model);
        if (alunoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aluno com ID " + alunoId + " não encontrado.");
            return "redirect:/web/alunos";
        }

        if (bindingResult.hasErrors()) {
            log.warn("Erros de validação ao criar treino: {}", bindingResult.getAllErrors());
            model.addAttribute("pageTitle", "Novo Treino para " + alunoOpt.get().nome());
            model.addAttribute("treinoId", null);
            return "treinos/form-treino";
        }

        try {
            treinoService.createTreino(treinoCreateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Treino cadastrado com sucesso!");
            return "redirect:/web/alunos/" + alunoId + "/treinos";
        } catch (Exception e) {
            log.error("Erro ao cadastrar treino para o aluno ID: {}.", alunoId, e);
            model.addAttribute("errorMessageGlobal", "Erro ao cadastrar treino: " + e.getMessage());
            model.addAttribute("pageTitle", "Novo Treino para " + alunoOpt.get().nome());
            return "treinos/form-treino";
        }
    }

    /**
     * Exibe o formulário de edição para um treino existente.
     * @param alunoId O ID do aluno.
     * @param treinoId O ID do treino a ser editado.
     * @param model O Model para popular o formulário.
     * @param redirectAttributes Atributos para mensagens de erro.
     * @return O nome do template do formulário ou um redirecionamento se o treino não for encontrado.
     */
    @GetMapping("/editar/{treinoId}")
    public String mostrarFormularioEditarTreino(@PathVariable Long alunoId, @PathVariable Long treinoId, Model model, RedirectAttributes redirectAttributes) {
        log.info("Exibindo formulário de edição para o treino ID: {} do aluno ID: {}", treinoId, alunoId);

        Optional<AlunoResponseDTO> alunoOpt = carregarAlunoParaModel(alunoId, model);
        if (alunoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aluno com ID " + alunoId + " não encontrado.");
            return "redirect:/web/alunos";
        }

        return treinoService.getTreinoByIdAndAlunoId(treinoId, alunoId)
                .map(treino -> {
                    List<ExercicioCreateDTO> exerciciosParaForm = treino.exercicios().stream()
                            .map(ex -> new ExercicioCreateDTO(ex.nomeExercicio(), ex.seriesRepeticoes()))
                            .collect(Collectors.toList());

                    model.addAttribute("treinoForm", new TreinoUpdateDTO(treino.nome(), treino.descricao(), exerciciosParaForm));
                    model.addAttribute("treinoId", treinoId);
                    model.addAttribute("pageTitle", "Editar Treino de " + alunoOpt.get().nome());
                    return "treinos/form-treino";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Treino não encontrado ou não pertence a este aluno.");
                    return "redirect:/web/alunos/" + alunoId + "/treinos";
                });
    }

    /**
     * Processa a submissão do formulário de atualização de treino.
     * @param alunoId O ID do aluno.
     * @param treinoId O ID do treino sendo atualizado.
     * @param treinoUpdateDTO O objeto do formulário com os dados.
     * @param bindingResult O resultado da validação.
     * @param model O Model para repopular a página em caso de erro.
     * @param redirectAttributes Atributos para mensagens.
     * @return Um redirecionamento em caso de sucesso, ou a página do formulário em caso de erro.
     */
    @PostMapping("/atualizar/{treinoId}")
    public String atualizarTreino(@PathVariable Long alunoId, @PathVariable Long treinoId,
                                  @Valid @ModelAttribute("treinoForm") TreinoUpdateDTO treinoUpdateDTO,
                                  BindingResult bindingResult,
                                  Model model, RedirectAttributes redirectAttributes) {
        log.info("Processando atualização do treino ID: {} para o aluno ID: {}", treinoId, alunoId);

        Optional<AlunoResponseDTO> alunoOpt = carregarAlunoParaModel(alunoId, model);
        if (alunoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aluno com ID " + alunoId + " não encontrado.");
            return "redirect:/web/alunos";
        }

        if (bindingResult.hasErrors()) {
            log.warn("Erros de validação ao atualizar treino: {}", bindingResult.getAllErrors());
            model.addAttribute("pageTitle", "Editar Treino de " + alunoOpt.get().nome());
            model.addAttribute("treinoId", treinoId);
            return "treinos/form-treino";
        }

        try {
            treinoService.updateTreino(treinoId, treinoUpdateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Treino atualizado com sucesso!");
            return "redirect:/web/alunos/" + alunoId + "/treinos";
        } catch (Exception e) {
            log.error("Erro ao atualizar o treino ID: {}.", treinoId, e);
            model.addAttribute("errorMessageGlobal", "Erro ao atualizar treino: " + e.getMessage());
            model.addAttribute("pageTitle", "Editar Treino de " + alunoOpt.get().nome());
            model.addAttribute("treinoId", treinoId);
            return "treinos/form-treino";
        }
    }

    /**
     * Processa a requisição para deletar um treino.
     * @param alunoId O ID do aluno.
     * @param treinoId O ID do treino a ser deletado.
     * @param redirectAttributes Atributos para mensagens.
     * @return Um redirecionamento para a lista de treinos.
     */
    @GetMapping("/deletar/{treinoId}")
    public String deletarTreino(@PathVariable Long alunoId, @PathVariable Long treinoId, RedirectAttributes redirectAttributes) {
        log.info("Processando deleção do treino ID: {} do aluno ID: {}", treinoId, alunoId);
        try {
            // Garante que só se pode deletar um treino que pertence ao aluno da URL.
            treinoService.getTreinoByIdAndAlunoId(treinoId, alunoId)
                    .orElseThrow(() -> new SecurityException("Tentativa de deletar treino que não pertence ao aluno."));

            treinoService.deleteTreino(treinoId);
            redirectAttributes.addFlashAttribute("successMessage", "Treino deletado com sucesso!");
        } catch (SecurityException e) {
            log.warn("Tentativa de acesso indevido: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Operação não permitida.");
        } catch (Exception e) {
            log.error("Erro ao deletar o treino ID: {}", treinoId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar treino.");
        }
        return "redirect:/web/alunos/" + alunoId + "/treinos";
    }

    /**
     * Gera e retorna a ficha de treino em formato PDF.
     * @param alunoId O ID do aluno.
     * @param treinoId O ID do treino a ser impresso.
     * @return Um ResponseEntity contendo os bytes do PDF ou um status de erro.
     */
    @GetMapping("/imprimir-pdf/{treinoId}")
    public ResponseEntity<byte[]> imprimirTreinoPdf(@PathVariable Long alunoId, @PathVariable Long treinoId) {
        log.info("Gerando PDF para o treino ID: {} do aluno ID: {}", treinoId, alunoId);

        Optional<AlunoResponseDTO> alunoOpt = alunoService.getAlunoById(alunoId);
        Optional<TreinoResponseDTO> treinoOpt = treinoService.getTreinoByIdAndAlunoId(treinoId, alunoId);

        if (alunoOpt.isEmpty() || treinoOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            byte[] pdfBytes = generatePdfBytes(alunoOpt.get(), treinoOpt.get());

            HttpHeaders headers = new HttpHeaders();
            String filename = "treino_" + alunoOpt.get().nome().replace(" ", "_") + ".pdf";
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            log.error("Erro ao gerar o PDF para o treino ID: {}", treinoId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Carrega os dados de um aluno e os adiciona ao Model para uso na view.
     * @param alunoId O ID do aluno a ser carregado.
     * @param model O Model para adicionar o atributo "aluno".
     * @return Um Optional contendo o AlunoResponseDTO se encontrado, ou vazio caso contrário.
     */
    private Optional<AlunoResponseDTO> carregarAlunoParaModel(Long alunoId, Model model) {
        Optional<AlunoResponseDTO> alunoOpt = alunoService.getAlunoById(alunoId);
        alunoOpt.ifPresent(aluno -> model.addAttribute("aluno", aluno));
        return alunoOpt;
    }

    /**
     * Gera os bytes de um arquivo PDF para uma ficha de treino.
     * @param aluno O DTO do aluno.
     * @param treino O DTO do treino.
     * @return Um array de bytes contendo o PDF.
     * @throws DocumentException Se ocorrer um erro durante a criação do documento.
     */
    private byte[] generatePdfBytes(AlunoResponseDTO aluno, TreinoResponseDTO treino) throws DocumentException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Definição de fontes
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
            Font tableHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font tableBodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            // Conteúdo do PDF
            document.add(new Paragraph("Ficha de Treino - GymTime", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Aluno: " + aluno.nome(), normalFont));
            document.add(new Paragraph("Treino: " + treino.nome(), normalFont));
            if (treino.descricao() != null && !treino.descricao().isBlank()) {
                document.add(new Paragraph("Descrição: " + treino.descricao(), normalFont));
            }
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Exercícios", headerFont));
            document.add(new Paragraph(" "));

            // Tabela de exercícios
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3f, 1.5f});

            table.addCell(new PdfPCell(new Phrase("Exercício", tableHeaderFont)));
            table.addCell(new PdfPCell(new Phrase("Séries/Repetições", tableHeaderFont)));

            treino.exercicios().forEach(ex -> {
                table.addCell(new Phrase(ex.nomeExercicio(), tableBodyFont));
                table.addCell(new Phrase(ex.seriesRepeticoes() != null ? ex.seriesRepeticoes() : "-", tableBodyFont));
            });

            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (IOException e) {
            throw new DocumentException(e);
        }
    }
}
