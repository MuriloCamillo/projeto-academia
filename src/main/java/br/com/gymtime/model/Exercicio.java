package br.com.gymtime.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

/**
 * Representa a entidade Exercicio no banco de dados.
 * Cada exercício é um componente de um Treino.
 */
@Entity
@Table(name = "exercicios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    /**
     * O Treino ao qual este exercício pertence.
     * - fetch = FetchType.LAZY: O treino associado só é carregado do banco quando for explicitamente acessado.
     * - @JsonBackReference: Evita a serialização recursiva infinita entre Treino e Exercicio.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_id", nullable = false)
    @JsonBackReference("treino-exercicios")
    @ToString.Exclude // Exclui este campo do método toString() para evitar recursão e LazyInitializationException.
    private Treino treino;

    /**
     * Construtor customizado para facilitar a criação de um novo exercício
     * a partir de um DTO, que é o formato esperado pelo TreinoServiceImpl.
     * @param nomeExercicio O nome do exercício.
     * @param seriesRepeticoes A descrição das séries e repetições.
     */
    public Exercicio(String nomeExercicio, String seriesRepeticoes) {
        this.nomeExercicio = nomeExercicio;
        this.seriesRepeticoes = seriesRepeticoes;
    }


    /**
     * Compara dois objetos Exercicio com base em seus IDs.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercicio exercicio = (Exercicio) o;
        return id != null && id.equals(exercicio.id);
    }

    /**
     * Gera um hash code baseado no ID do exercício.
     */
    @Override
    public int hashCode() {
        // Usa uma constante para objetos ainda não persistidos (id null).
        return getClass().hashCode();
    }
}
