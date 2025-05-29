package br.com.gymtime.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "treinos")
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do treino não pode estar em branco.")
    @Size(min = 3, max = 100, message = "O nome do treino deve ter entre 3 e 100 caracteres.")
    @Column(nullable = false, length = 100)
    private String nome;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres.")
    @Column(length = 500)
    private String descricao;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDate dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;

    // Relacionamento: Muitos Treinos pertencem a um Aluno
    @ManyToOne(fetch = FetchType.LAZY) // LAZY é geralmente preferível para performance
    @JoinColumn(name = "aluno_id", nullable = false) // Define a chave estrangeira
    @JsonBackReference // Evita recursão infinita na serialização JSON com Aluno
    private Aluno aluno;

    // Métodos de ciclo de vida JPA para popular dataCriacao e dataAtualizacao
    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDate.now();
        dataAtualizacao = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDate.now();
    }

    // Construtores
    public Treino() {
    }

    public Treino(String nome, String descricao, Aluno aluno) {
        this.nome = nome;
        this.descricao = descricao;
        this.aluno = aluno;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDate dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    // Equals e HashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Treino treino = (Treino) o;
        return Objects.equals(id, treino.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Treino{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", dataCriacao=" + dataCriacao +
                ", dataAtualizacao=" + dataAtualizacao +
                ", alunoId=" + (aluno != null ? aluno.getId() : null) +
                '}';
    }
}