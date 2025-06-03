package br.com.gymtime.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "exercicios")
public class Exercicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do exercício não pode estar em branco.")
    @Size(max = 150, message = "O nome do exercício deve ter no máximo 150 caracteres.")
    @Column(nullable = false, length = 150)
    private String nomeExercicio;

    @Size(max = 100, message = "Séries e repetições devem ter no máximo 100 caracteres.")
    @Column(length = 100)
    private String seriesRepeticoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_id", nullable = false)
    @JsonBackReference("treino-exercicios") // Nomeado para consistência
    private Treino treino;

    public Exercicio() {}

    public Exercicio(String nomeExercicio, String seriesRepeticoes) {
        this.nomeExercicio = nomeExercicio;
        this.seriesRepeticoes = seriesRepeticoes;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNomeExercicio() { return nomeExercicio; }
    public void setNomeExercicio(String nomeExercicio) { this.nomeExercicio = nomeExercicio; }
    public String getSeriesRepeticoes() { return seriesRepeticoes; }
    public void setSeriesRepeticoes(String seriesRepeticoes) { this.seriesRepeticoes = seriesRepeticoes; }
    public Treino getTreino() { return treino; }
    public void setTreino(Treino treino) { this.treino = treino; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercicio exercicio = (Exercicio) o;
        return Objects.equals(id, exercicio.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}