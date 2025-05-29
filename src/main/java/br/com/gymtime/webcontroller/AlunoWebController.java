package br.com.gymtime.webcontroller;

import br.com.gymtime.dto.AlunoDTO;
import br.com.gymtime.exception.ResourceNotFoundException; // Certifique-se que este import existe
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
        List<AlunoDTO> alunos = alunoService.getAllAlunos();
        model.addAttribute("alunos", alunos);
        return "alunos/lista-alunos";
    }

    // Mostrar Formulário para NOVO Aluno
    @GetMapping("/novo")
    public String mostrarFormularioNovoAluno(Model model) {
        model.addAttribute("alunoForm", new AlunoDTO.AlunoCreateDTO("", "", "")); // Usar "alunoForm"
        model.addAttribute("pageTitle", "Cadastrar Novo Aluno");
        // Não precisamos de alunoId aqui para o th:object
        System.out.println(">>> AlunoWebController: GET /novo - Preparando formulário de criação.");
        return "alunos/form-aluno";
    }

    // Mostrar Formulário para EDITAR Aluno
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarAluno(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<AlunoDTO> alunoOpt = alunoService.getAlunoById(id);
        if (alunoOpt.isPresent()) {
            AlunoDTO alunoDTO = alunoOpt.get();
            // Criar um AlunoUpdateDTO com os dados atuais para popular o formulário
            AlunoDTO.AlunoUpdateDTO alunoUpdateData = new AlunoDTO.AlunoUpdateDTO(
                    alunoDTO.nome(),
                    alunoDTO.email(),
                    alunoDTO.telefone()
            );
            model.addAttribute("alunoForm", alunoUpdateData); // Usar "alunoForm"
            model.addAttribute("alunoId", id); // Necessário para o action do form e lógica do template
            model.addAttribute("pageTitle", "Editar Aluno");
            System.out.println(">>> AlunoWebController: GET /editar/" + id + " - Preparando formulário de edição.");
            return "alunos/form-aluno";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Aluno não encontrado com ID: " + id);
            return "redirect:/web/alunos";
        }
    }

    // Salvar NOVO Aluno
    @PostMapping("/criar") // Novo endpoint para criação
    public String criarAluno(@Valid @ModelAttribute("alunoForm") AlunoDTO.AlunoCreateDTO alunoCreateDTO,
                             BindingResult bindingResult, // Resultado da validação para alunoCreateDTO
                             Model model,
                             RedirectAttributes redirectAttributes) {

        System.out.println(">>> AlunoWebController: POST /criar - Tentando criar aluno.");
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Cadastrar Novo Aluno");
            // O objeto "alunoForm" com erros já está no model por causa do @ModelAttribute
            System.out.println(">>> AlunoWebController: POST /criar - Erros de validação encontrados.");
            return "alunos/form-aluno"; // Volta para o formulário
        }
        try {
            alunoService.createAluno(alunoCreateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Aluno cadastrado com sucesso!");
            System.out.println(">>> AlunoWebController: POST /criar - Aluno criado com sucesso.");
        } catch (Exception e) { // Idealmente, capture exceções mais específicas (ex: EmailAlreadyExistsException)
            model.addAttribute("pageTitle", "Cadastrar Novo Aluno");
            model.addAttribute("errorMessage", "Erro ao cadastrar aluno: " + e.getMessage());
            // Adicionar o alunoForm de volta ao model para repopular o form em caso de erro não relacionado à validação
            model.addAttribute("alunoForm", alunoCreateDTO);
            System.err.println(">>> AlunoWebController: POST /criar - Exceção: " + e.getMessage());
            return "alunos/form-aluno";
        }
        return "redirect:/web/alunos";
    }

    // ATUALIZAR Aluno Existente
    @PostMapping("/atualizar/{id}") // Novo endpoint para atualização
    public String atualizarAluno(@PathVariable("id") Long id,
                                 @Valid @ModelAttribute("alunoForm") AlunoDTO.AlunoUpdateDTO alunoUpdateDTO,
                                 BindingResult bindingResult, // Resultado da validação para alunoUpdateDTO
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        System.out.println(">>> AlunoWebController: POST /atualizar/" + id + " - Tentando atualizar aluno.");
        model.addAttribute("alunoId", id); // Necessário para repopular o form em caso de erro
        model.addAttribute("pageTitle", "Editar Aluno");

        if (bindingResult.hasErrors()) {
            // O objeto "alunoForm" com erros já está no model
            System.out.println(">>> AlunoWebController: POST /atualizar/" + id + " - Erros de validação.");
            return "alunos/form-aluno";
        }
        try {
            alunoService.updateAluno(id, alunoUpdateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Aluno atualizado com sucesso!");
            System.out.println(">>> AlunoWebController: POST /atualizar/" + id + " - Aluno atualizado com sucesso.");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/web/alunos"; // Se não encontrou, volta pra lista
        }
        catch (Exception e) { // Idealmente, capture exceções mais específicas
            model.addAttribute("errorMessage", "Erro ao atualizar aluno: " + e.getMessage());
            // Adicionar o alunoForm de volta ao model para repopular o form
            model.addAttribute("alunoForm", alunoUpdateDTO);
            System.err.println(">>> AlunoWebController: POST /atualizar/" + id + " - Exceção: " + e.getMessage());
            return "alunos/form-aluno";
        }
        return "redirect:/web/alunos";
    }

    // Deletar Aluno (mantém como estava, pois não envolve o form-backing object da mesma forma)
    @GetMapping("/deletar/{id}")
    public String deletarAluno(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            alunoService.deleteAluno(id);
            redirectAttributes.addFlashAttribute("successMessage", "Aluno deletado com sucesso!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar: Aluno não encontrado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar aluno: " + e.getMessage());
        }
        return "redirect:/web/alunos";
    }
}