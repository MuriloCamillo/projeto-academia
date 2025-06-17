package br.com.gymtime.webcontroller;

import br.com.gymtime.dto.AlunoCreateDTO;
import br.com.gymtime.dto.AlunoResponseDTO;
import br.com.gymtime.dto.AlunoUpdateDTO;
import br.com.gymtime.exception.CpfAlreadyExistsException;
import br.com.gymtime.exception.EmailAlreadyExistsException;
import br.com.gymtime.exception.ResourceNotFoundException;
import br.com.gymtime.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controller para gerenciar as interações web (Thymeleaf) relacionadas a Alunos.
 * Lida com a exibição de páginas HTML, submissão de formulários e navegação.
 */
@Controller
@RequestMapping("/web/alunos")
@RequiredArgsConstructor
@Slf4j
public class AlunoWebController {

    private final AlunoService alunoService;

    /**
     * Exibe a página com a lista de todos os alunos cadastrados.
     * @param model O Model para adicionar atributos que serão acessíveis na view.
     * @return O nome do template Thymeleaf a ser renderizado ("alunos/lista-alunos").
     */
    @GetMapping
    public String listarAlunos(Model model) {
        List<AlunoResponseDTO> alunos = alunoService.getAllAlunos();
        model.addAttribute("alunos", alunos);
        return "alunos/lista-alunos";
    }

    /**
     * Exibe o formulário para a criação de um novo aluno.
     * @param model O Model para adicionar o objeto do formulário e o título da página.
     * @return O nome do template do formulário ("alunos/form-aluno").
     */
    @GetMapping("/novo")
    public String mostrarFormularioNovoAluno(Model model) {
        model.addAttribute("alunoForm", new AlunoCreateDTO("", "", "", ""));
        model.addAttribute("pageTitle", "Cadastrar Novo Aluno");
        return "alunos/form-aluno";
    }

    /**
     * Exibe o formulário de edição para um aluno existente.
     * @param id O ID do aluno a ser editado.
     * @param model O Model para popular o formulário com os dados do aluno.
     * @param redirectAttributes Atributos para passar mensagens em caso de redirecionamento.
     * @return O nome do template do formulário ou um redirecionamento para a lista se o aluno não for encontrado.
     */
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditarAluno(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<AlunoResponseDTO> alunoOpt = alunoService.getAlunoById(id);
        if (alunoOpt.isPresent()) {
            AlunoResponseDTO alunoExistente = alunoOpt.get();
            // Converte o DTO de resposta para um DTO de atualização para preencher o formulário
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

    /**
     * Processa a submissão do formulário de criação de aluno.
     * @param alunoCreateDTO O objeto do formulário com os dados do aluno, validado.
     * @param bindingResult Contém o resultado da validação.
     * @param model O Model para repopular a página em caso de erro.
     * @param redirectAttributes Atributos para enviar mensagem de sucesso após o redirecionamento.
     * @return Um redirecionamento para a lista de alunos em caso de sucesso, ou a página do formulário em caso de erro.
     */
    @PostMapping("/criar")
    public String criarAluno(@Valid @ModelAttribute("alunoForm") AlunoCreateDTO alunoCreateDTO,
                             BindingResult bindingResult,
                             Model model,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Cadastrar Novo Aluno");
            return "alunos/form-aluno";
        }
        try {
            alunoService.createAluno(alunoCreateDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Aluno cadastrado com sucesso!");
        } catch (EmailAlreadyExistsException e) {
            bindingResult.rejectValue("email", "error.alunoForm", e.getMessage());
            model.addAttribute("pageTitle", "Cadastrar Novo Aluno");
            return "alunos/form-aluno";
        } catch (CpfAlreadyExistsException e) {
            bindingResult.rejectValue("cpf", "error.alunoForm", e.getMessage());
            model.addAttribute("pageTitle", "Cadastrar Novo Aluno");
            return "alunos/form-aluno";
        } catch (Exception e) {
            log.error("Erro inesperado ao cadastrar aluno.", e);
            model.addAttribute("errorMessageGlobal", "Erro inesperado ao cadastrar aluno.");
            model.addAttribute("pageTitle", "Cadastrar Novo Aluno");
            return "alunos/form-aluno";
        }
        return "redirect:/web/alunos";
    }

    /**
     * Processa a submissão do formulário de atualização de aluno.
     * @param id O ID do aluno sendo atualizado.
     * @param alunoUpdateDTO O objeto do formulário com os dados do aluno.
     * @param bindingResult Contém o resultado da validação.
     * @param model O Model para repopular a página em caso de erro.
     * @param redirectAttributes Atributos para enviar mensagens após o redirecionamento.
     * @return Um redirecionamento para a lista de alunos em caso de sucesso, ou a página do formulário em caso de erro.
     */
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
            log.error("Erro inesperado ao atualizar aluno ID: {}", id, e);
            model.addAttribute("errorMessageGlobal", "Erro inesperado ao atualizar aluno.");
            return "alunos/form-aluno";
        }
        return "redirect:/web/alunos";
    }

    /**
     * Processa a requisição para deletar um aluno.
     * @param id O ID do aluno a ser deletado.
     * @param redirectAttributes Atributos para enviar mensagem de sucesso/erro após o redirecionamento.
     * @return Um redirecionamento para a lista de alunos.
     */
    @GetMapping("/deletar/{id}")
    public String deletarAluno(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            alunoService.deleteAluno(id);
            redirectAttributes.addFlashAttribute("successMessage", "Aluno deletado com sucesso!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            // Este erro pode ocorrer se houver restrições de banco de dados não tratadas pela cascata.
            log.error("Erro ao tentar deletar o aluno ID: {}. Causa: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao deletar aluno. Verifique se há dados associados não previstos.");
        }
        return "redirect:/web/alunos";
    }
}
