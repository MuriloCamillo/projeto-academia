package br.com.gymtime.webcontroller;

import br.com.gymtime.dto.AlunoDTO;
import br.com.gymtime.dto.TreinoDTO;
import br.com.gymtime.exception.ResourceNotFoundException; // Importe
import br.com.gymtime.service.AlunoService;
import br.com.gymtime.service.TreinoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web/alunos/{alunoId}/treinos")
public class TreinoWebController {

    private final TreinoService treinoService;
    private final AlunoService alunoService;

    public TreinoWebController(TreinoService treinoService, AlunoService alunoService) {
        this.treinoService = treinoService;
        this.alunoService = alunoService;
    }

    // Método auxiliar para verificar e adicionar aluno ao model
    private Optional<AlunoDTO> carregarAlunoParaModel(Long alunoId, Model model) {
        Optional<AlunoDTO> alunoOpt = alunoService.getAlunoById(alunoId);
        if (alunoOpt.isEmpty()) {
            // Adicionar mensagem de erro para a view ou redirect attributes se for redirecionar
            model.addAttribute("errorMessage", "Aluno com ID " + alunoId + " não encontrado.");
            return Optional.empty();
        }
        model.addAttribute("aluno", alunoOpt.get());
        return alunoOpt;
    }

    @GetMapping
    public String listarTreinosDoAluno(@PathVariable Long alunoId, Model model, RedirectAttributes redirectAttributes) {
        if (carregarAlunoParaModel(alunoId, model).isEmpty()) {
            // A mensagem de erro já foi adicionada ao model por carregarAlunoParaModel
            // Ou podemos redirecionar se preferir:
            redirectAttributes.addFlashAttribute("errorMessage", "Aluno com ID " + alunoId + " não encontrado.");
            return "redirect:/web/alunos";
        }
        List<TreinoDTO> treinos = treinoService.getTreinosByAlunoId(alunoId);
        model.addAttribute("treinos", treinos);
        return "treinos/lista-treinos";
    }

    @GetMapping("/novo")
    public String mostrarFormularioNovoTreino(@PathVariable Long alunoId, Model model, RedirectAttributes redirectAttributes) {
        Optional<AlunoDTO> alunoOpt = carregarAlunoParaModel(alunoId, model);
        if (alunoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aluno com ID " + alunoId + " não encontrado para adicionar treino.");
            return "redirect:/web/alunos";
        }
        model.addAttribute("treinoForm", new TreinoDTO.TreinoCreateDTO("", "", alunoId)); // "treinoForm" e passa alunoId
        model.addAttribute("pageTitle", "Novo Treino para " + alunoOpt.get().nome());
        // treinoId será null aqui, o que é usado no template form-treino.html para diferenciar
        return "treinos/form-treino";
    }

    @PostMapping("/criar") // Endpoint para criar treino
    public String criarTreino(@PathVariable Long alunoId,
                              @Valid @ModelAttribute("treinoForm") TreinoDTO.TreinoCreateDTO treinoCreateDTO,
                              BindingResult bindingResult,
                              Model model, RedirectAttributes redirectAttributes) {

        Optional<AlunoDTO> alunoOpt = carregarAlunoParaModel(alunoId, model);
        if (alunoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aluno com ID " + alunoId + " não encontrado.");
            return "redirect:/web/alunos";
        }
        model.addAttribute("pageTitle", "Novo Treino para " + alunoOpt.get().nome());

        // Validação adicional para garantir que o alunoId no DTO (se ainda existir) corresponde ao do path
        if (!treinoCreateDTO.alunoId().equals(alunoId)) {
            bindingResult.rejectValue("alunoId", "error.treinoCreateDTO", "Inconsistência no ID do aluno associado.");
        }

        if (bindingResult.hasErrors()) {
            // "treinoForm" já está no model devido ao @ModelAttribute
            return "treinos/form-treino";
        }
        try {
            treinoService.createTreino(treinoCreateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Treino cadastrado com sucesso!");
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao cadastrar treino: " + e.getMessage());
            // "treinoForm" já está no model
            return "treinos/form-treino";
        }
        return "redirect:/web/alunos/" + alunoId + "/treinos";
    }

    @GetMapping("/editar/{treinoId}")
    public String mostrarFormularioEditarTreino(@PathVariable Long alunoId, @PathVariable Long treinoId, Model model, RedirectAttributes redirectAttributes) {
        Optional<AlunoDTO> alunoOpt = carregarAlunoParaModel(alunoId, model);
        if (alunoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aluno com ID " + alunoId + " não encontrado.");
            return "redirect:/web/alunos";
        }

        Optional<TreinoDTO> treinoOpt = treinoService.getTreinoById(treinoId); // O seu getTreinoById não filtra por alunoId
        if (treinoOpt.isEmpty() || !treinoOpt.get().alunoId().equals(alunoId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Treino não encontrado ou não pertence a este aluno.");
            return "redirect:/web/alunos/" + alunoId + "/treinos";
        }

        TreinoDTO treinoDTO = treinoOpt.get();
        // Usar TreinoUpdateDTO para o formulário de edição
        model.addAttribute("treinoForm", new TreinoDTO.TreinoUpdateDTO(treinoDTO.nome(), treinoDTO.descricao()));
        model.addAttribute("treinoId", treinoId); // Para o action e lógica do template
        model.addAttribute("pageTitle", "Editar Treino de " + alunoOpt.get().nome());
        return "treinos/form-treino";
    }

    @PostMapping("/atualizar/{treinoId}") // Endpoint para atualizar treino
    public String atualizarTreino(@PathVariable Long alunoId, @PathVariable Long treinoId,
                                  @Valid @ModelAttribute("treinoForm") TreinoDTO.TreinoUpdateDTO treinoUpdateDTO,
                                  BindingResult bindingResult,
                                  Model model, RedirectAttributes redirectAttributes) {

        Optional<AlunoDTO> alunoOpt = carregarAlunoParaModel(alunoId, model);
        if (alunoOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Aluno com ID " + alunoId + " não encontrado.");
            return "redirect:/web/alunos";
        }
        model.addAttribute("pageTitle", "Editar Treino de " + alunoOpt.get().nome());
        model.addAttribute("treinoId", treinoId); // Para repopular form em caso de erro

        // Validação para garantir que o treino pertence ao aluno (se o service não fizer isso)
        Optional<TreinoDTO> treinoExistenteOpt = treinoService.getTreinoById(treinoId);
        if (treinoExistenteOpt.isEmpty() || !treinoExistenteOpt.get().alunoId().equals(alunoId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Treino inválido ou não pertence a este aluno.");
            return "redirect:/web/alunos/" + alunoId + "/treinos";
        }

        if (bindingResult.hasErrors()) {
            // "treinoForm" já está no model
            return "treinos/form-treino";
        }
        try {
            treinoService.updateTreino(treinoId, treinoUpdateDTO); // O service.updateTreino não valida o alunoId
            redirectAttributes.addFlashAttribute("successMessage", "Treino atualizado com sucesso!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/alunos/" + alunoId + "/treinos";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao atualizar treino: " + e.getMessage());
            // "treinoForm" já está no model
            return "treinos/form-treino";
        }
        return "redirect:/web/alunos/" + alunoId + "/treinos";
    }

    @GetMapping("/deletar/{treinoId}")
    public String deletarTreino(@PathVariable Long alunoId, @PathVariable Long treinoId, RedirectAttributes redirectAttributes) {
        // Adicionar verificação se o treino pertence ao aluno antes de deletar
        Optional<TreinoDTO> treinoOpt = treinoService.getTreinoById(treinoId);
        if (treinoOpt.isPresent() && treinoOpt.get().alunoId().equals(alunoId)) {
            try {
                treinoService.deleteTreino(treinoId);
                redirectAttributes.addFlashAttribute("successMessage", "Treino deletado com sucesso!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar treino: " + e.getMessage());
            }
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Treino não encontrado ou não pertence a este aluno.");
        }
        return "redirect:/web/alunos/" + alunoId + "/treinos";
    }
}