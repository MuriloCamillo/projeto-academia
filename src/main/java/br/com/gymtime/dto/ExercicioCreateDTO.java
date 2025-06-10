package br.com.gymtime.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects; // Para equals e hashCode

public class ExercicioCreateDTO {

        @NotBlank(message = "O nome do exercício não pode estar em branco.")
        @Size(max = 150, message = "Nome do exercício muito longo (máx 150 caracteres).")
        private String nomeExercicio;

        @Size(max = 100, message = "Séries/repetições muito longas (máx 100 caracteres).")
        private String seriesRepeticoes;

        // Construtor público sem argumentos (necessário para Spring Data Binding e frameworks)
        public ExercicioCreateDTO() {
        }

        // Construtor com todos os campos (opcional, mas útil)
        public ExercicioCreateDTO(String nomeExercicio, String seriesRepeticoes) {
                this.nomeExercicio = nomeExercicio;
                this.seriesRepeticoes = seriesRepeticoes;
        }

        // Getters e Setters
        public String getNomeExercicio() {
                return nomeExercicio;
        }

        public void setNomeExercicio(String nomeExercicio) {
                this.nomeExercicio = nomeExercicio;
        }

        public String getSeriesRepeticoes() {
                return seriesRepeticoes;
        }

        public void setSeriesRepeticoes(String seriesRepeticoes) {
                this.seriesRepeticoes = seriesRepeticoes;
        }

        // Opcional: equals, hashCode, toString para DTOs de classe
        @Override
        public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                ExercicioCreateDTO that = (ExercicioCreateDTO) o;
                return Objects.equals(nomeExercicio, that.nomeExercicio) &&
                        Objects.equals(seriesRepeticoes, that.seriesRepeticoes);
        }

        @Override
        public int hashCode() {
                return Objects.hash(nomeExercicio, seriesRepeticoes);
        }

        @Override
        public String toString() {
                return "ExercicioCreateDTO{" +
                        "nomeExercicio='" + nomeExercicio + '\'' +
                        ", seriesRepeticoes='" + seriesRepeticoes + '\'' +
                        '}';
        }
}