package br.com.gymtime.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "alunos")
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

    @Size(max = 15, message = "O telefone deve ter no máximo 15 caracteres.")
    @Column(length = 15)
    private String telefone;

    // Relacionamento: Um Aluno pode ter muitos Treinos
    // cascade = CascadeType.ALL: Operações no Aluno (salvar, deletar) são propagadas para Treinos associados.
    // orphanRemoval = true: Se um Treino é removido da lista de treinos do Aluno, ele é deletado do banco.
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Treino> treinos = new ArrayList<>();

    // Construtores
    public Aluno() {
    }

    public Aluno(String nome, String email, String telefone) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<Treino> getTreinos() {
        return treinos;
    }

    public void setTreinos(List<Treino> treinos) {
        this.treinos = treinos;
    }

    // Método para adicionar treino de forma consistente
    public void addTreino(Treino treino) {
        treinos.add(treino);
        treino.setAluno(this);
    }

    public void removeTreino(Treino treino) {
        treinos.remove(treino);
        treino.setAluno(null);
    }

    // Equals e HashCode (importante para coleções e JPA)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aluno aluno = (Aluno) o;
        return Objects.equals(id, aluno.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Aluno{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                '}';
    }
}