package br.com.gymtime.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    @NotNull(message = "A data de criação é obrigatória.")
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDate dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    @JsonBackReference("aluno-treinos")
    private Aluno aluno;

    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("treino-exercicios")
    private List<Exercicio> exercicios = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDate.now();
        this.dataAtualizacao = LocalDate.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDate.now();
    }

    public Treino() {}

    public Treino(String nome, String descricao, Aluno aluno) {
        this.nome = nome;
        this.descricao = descricao;
        this.aluno = aluno;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDate getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDate dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }
    public List<Exercicio> getExercicios() { return exercicios; }
    public void setExercicios(List<Exercicio> exercicios) { this.exercicios = exercicios; }

    public void addExercicio(Exercicio exercicio) {
        this.exercicios.add(exercicio);
        exercicio.setTreino(this);
    }

    public void removeExercicio(Exercicio exercicio) {
        this.exercicios.remove(exercicio);
        exercicio.setTreino(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Treino treino = (Treino) o;
        return Objects.equals(id, treino.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}