package br.com.gymtime.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa a entidade Aluno no banco de dados.
 * Mapeia a tabela "alunos" e contém as informações cadastrais de um aluno.
 */
@Entity
@Table(name = "alunos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome não pode estar em branco.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String nome;

    @Email(message = "Formato de email inválido.")
    @NotBlank(message = "O email não pode estar em branco.")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 11) // Armazena apenas os 11 dígitos, se houver.
    private String telefone;

    @NotBlank(message = "O CPF não pode estar em branco (apenas números).")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    /**
     * Lista de treinos associados a este aluno.
     * - cascade = CascadeType.ALL: Operações de persistência (salvar, deletar) no Aluno são propagadas para seus Treinos.
     * - orphanRemoval = true: Se um Treino for removido desta lista, ele será deletado do banco de dados.
     * - fetch = FetchType.LAZY: Os treinos só são carregados do banco quando explicitamente acessados.
     */
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @ToString.Exclude // Exclui este campo do método toString() para evitar LazyInitializationException e recursão infinita.
    private List<Treino> treinos = new ArrayList<>();

    /**
     * Setter customizado para o campo 'telefone'.
     * Remove todos os caracteres não numéricos antes de atribuir o valor.
     * Se a string resultante for vazia, o campo é definido como nulo.
     * @param telefone O número de telefone a ser definido, possivelmente com máscara.
     */
    public void setTelefone(String telefone) {
        if (telefone != null) {
            String digitos = telefone.replaceAll("[^0-9]", "");
            this.telefone = digitos.isEmpty() ? null : digitos;
        } else {
            this.telefone = null;
        }
    }

    /**
     * Setter customizado para o campo 'cpf'.
     * Remove todos os caracteres não numéricos antes de atribuir o valor.
     * @param cpf O CPF a ser definido, possivelmente com máscara.
     */
    public void setCpf(String cpf) {
        if (cpf != null) {
            this.cpf = cpf.replaceAll("[^0-9]", "");
        } else {
            this.cpf = null;
        }
    }

    /**
     * Método auxiliar para adicionar um treino a este aluno, mantendo a consistência
     * do relacionamento bidirecional.
     * @param treino O treino a ser adicionado.
     */
    public void addTreino(Treino treino) {
        treinos.add(treino);
        treino.setAluno(this);
    }

    /**
     * Método auxiliar para remover um treino deste aluno, mantendo a consistência
     * do relacionamento bidirecional.
     * @param treino O treino a ser removido.
     */
    public void removeTreino(Treino treino) {
        treinos.remove(treino);
        treino.setAluno(null);
    }

    /**
     * Compara dois objetos Aluno com base em seus IDs.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aluno aluno = (Aluno) o;
        return id != null && id.equals(aluno.id);
    }

    /**
     * Gera um hash code baseado no ID do aluno.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Gera uma representação em String do objeto Aluno.
     * O campo 'treinos' é excluído pela anotação @ToString.Exclude.
     */
    @Override
    public String toString() {
        return "Aluno{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", cpf='" + cpf + '\'' +
                '}';
    }
}
