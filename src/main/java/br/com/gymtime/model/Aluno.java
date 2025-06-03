package br.com.gymtime.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    // Telefone é opcional. Se preenchido, deve ter 10 ou 11 dígitos.
    // O pattern ^([0-9]{10,11})?$ permite string vazia OU 10-11 dígitos.
    // O @Size não é mais necessário aqui se o pattern já define o comprimento e permite vazio.
    // Se você quiser manter o @Size, ele precisaria ser condicional ou o campo ser null.
    // Para simplificar, vamos confiar no pattern e no setter que converte "" para null.
    @Pattern(regexp = "^([0-9]{10,11})?$", message = "Telefone deve conter 10 ou 11 números, ou ser deixado em branco.")
    @Column(length = 11) // Suficiente para 11 dígitos
    private String telefone;

    @NotBlank(message = "O CPF não pode estar em branco (apenas números).")
    @Size(min = 11, max = 11, message = "CPF deve ter 11 dígitos (apenas números).") // Garante que são exatamente 11
    @Pattern(regexp = "^[0-9]{11}$", message = "CPF deve conter exatamente 11 números.")
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Treino> treinos = new ArrayList<>();

    public Aluno() {
    }

    public Aluno(String nome, String email, String telefone, String cpf) {
        this.nome = nome;
        this.email = email;
        this.setTelefone(telefone); // Usa o setter para limpar e possivelmente anular
        this.setCpf(cpf);           // Usa o setter para limpar
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) {
        if (telefone != null) {
            String digitos = telefone.replaceAll("[^0-9]", "");
            this.telefone = digitos.isEmpty() ? null : digitos; // Converte string vazia para null
        } else {
            this.telefone = null;
        }
    }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) {
        if (cpf != null) {
            this.cpf = cpf.replaceAll("[^0-9]", "");
            // Não converter CPF para null se vazio, pois é @NotBlank
        } else {
            this.cpf = null; // Permitir null antes da validação @NotBlank pegar
        }
    }

    public List<Treino> getTreinos() { return treinos; }
    public void setTreinos(List<Treino> treinos) { this.treinos = treinos; }

    public void addTreino(Treino treino) {
        treinos.add(treino);
        treino.setAluno(this);
    }

    public void removeTreino(Treino treino) {
        treinos.remove(treino);
        treino.setAluno(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aluno aluno = (Aluno) o;
        return Objects.equals(id, aluno.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

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
