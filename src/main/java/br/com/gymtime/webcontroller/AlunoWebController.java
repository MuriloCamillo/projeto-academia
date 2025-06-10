package br.com.gymtime.webcontroller;

import br.com.gymtime.dto.AlunoCreateDTO;
import br.com.gymtime.dto.AlunoResponseDTO;
import br.com.gymtime.dto.AlunoUpdateDTO;
import br.com.gymtime.exception.CpfAlreadyExistsException;
import br.com.gymtime.exception.EmailAlreadyExistsException;
import br.com.gymtime.exception.ResourceNotFoundException;
import br.com.gymtime.service.AlunoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web/alunos")
public class AlunoWebController {

    private final AlunoService alunoService;

    @Autowired
    public AlunoWebController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @GetMapping
    public String listarAlunos(Model model) {
        List<AlunoResponseDTO> alunos = alunoService.getAllAlunos();
        model.addAttribute("alunos", alunos);
        return "alunos/lista-alunos";
    }

    @GetMapping("/novo")
    public String mostrarFormularioNovoAluno(Model model) {
        model.addAttribute("alunoForm", new AlunoCreateDTO("", "", "", "")); // nome, email, telefone, cpf
        model.addAttribute("pageTitle", "Cadastrar Novo Aluno");
        return "alunos/form-aluno";
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarAluno(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<AlunoResponseDTO> alunoOpt = alunoService.getAlunoById(id);
        if (alunoOpt.isPresent()) {
            AlunoResponseDTO alunoExistente = alunoOpt.get();
            AlunoUpdateDTO alunoUpdateData = new AlunoUpdateDTO(
                    alunoExistente.nome(),
                    alunoExistente.email(),
                    alunoExistente.telefone(),
                    alunoExistente.cpf()
            );
            model.addAttribute("alunoForm", alunoUpdateData);
            model.addAttribute("alunoId", id);
            model.addAttribute("pageTitle", "Editar Aluno");
            return "alunos/form-aluno";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Aluno não encontrado com ID: " + id);
            return "redirect:/web/alunos";
        }
    }

    @PostMapping("/criar")
    public String criarAluno(@Valid @ModelAttribute("alunoForm") AlunoCreateDTO alunoCreateDTO,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        model.addAttribute("pageTitle", "Cadastrar Novo Aluno"); // Para repopular em caso de erro

        if (bindingResult.hasErrors()) {
            return "alunos/form-aluno";
        }
        try {
            alunoService.createAluno(alunoCreateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Aluno cadastrado com sucesso!");
        } catch (EmailAlreadyExistsException e) {
            bindingResult.rejectValue("email", "error.alunoForm", e.getMessage());
            return "alunos/form-aluno";
        } catch (CpfAlreadyExistsException e) {
            bindingResult.rejectValue("cpf", "error.alunoForm", e.getMessage());
            return "alunos/form-aluno";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro inesperado ao cadastrar aluno: " + e.getMessage());
            e.printStackTrace();
            return "alunos/form-aluno";
        }
        return "redirect:/web/alunos";
    }

    @PostMapping("/atualizar/{id}")
    public String atualizarAluno(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("alunoForm") AlunoUpdateDTO alunoUpdateDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        model.addAttribute("alunoId", id);
        model.addAttribute("pageTitle", "Editar Aluno");

        if (bindingResult.hasErrors()) {
            return "alunos/form-aluno";
        }
        try {
            alunoService.updateAluno(id, alunoUpdateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Aluno atualizado com sucesso!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/alunos";
        } catch (EmailAlreadyExistsException e) {
            bindingResult.rejectValue("email", "error.alunoForm", e.getMessage());
            return "alunos/form-aluno";
        } catch (CpfAlreadyExistsException e) {
            bindingResult.rejectValue("cpf", "error.alunoForm", e.getMessage());
            return "alunos/form-aluno";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro inesperado ao atualizar aluno: " + e.getMessage());
            e.printStackTrace();
            return "alunos/form-aluno";
        }
        return "redirect:/web/alunos";
    }

    @GetMapping("/deletar/{id}")
    public String deletarAluno(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            alunoService.deleteAluno(id);
            redirectAttributes.addFlashAttribute("successMessage", "Aluno deletado com sucesso!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar aluno. Pode haver treinos associados.");
            e.printStackTrace();
        }
        return "redirect:/web/alunos";
    }
}