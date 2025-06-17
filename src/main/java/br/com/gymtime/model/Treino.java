package br.com.gymtime.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa a entidade Treino no banco de dados.
 * Um Treino é associado a um Aluno e contém uma lista de Exercícios.
 */
@Entity
@Table(name = "treinos")
@Getter
@Setter
@NoArgsConstructor
public class Treino {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDate dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;

    /**
     * O Aluno ao qual este treino pertence.
     * - fetch = FetchType.LAZY: O aluno associado só é carregado quando explicitamente acessado.
     * - @JsonBackReference: Evita a serialização recursiva infinita entre Aluno e Treino.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    @JsonBackReference("aluno-treinos")
    @ToString.Exclude
    private Aluno aluno;

    /**
     * A lista de exercícios que compõem este treino.
     * - cascade = CascadeType.ALL: Operações no Treino são propagadas para seus Exercicios.
     * - orphanRemoval = true: Se um Exercicio for removido desta lista, ele é deletado do banco.
     * - @JsonManagedReference: Lado "pai" da relação, será serializado normalmente.
     */
    @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("treino-exercicios")
    @ToString.Exclude
    private List<Exercicio> exercicios = new ArrayList<>();

    /**
     * Construtor para facilitar a criação de um treino com seus dados essenciais.
     * @param nome O nome do treino.
     * @param descricao A descrição do treino.
     * @param aluno O aluno associado ao treino.
     */
    public Treino(String nome, String descricao, Aluno aluno) {
        this.nome = nome;
        this.descricao = descricao;
        this.aluno = aluno;
    }

    /**
     * Callback do JPA executado antes da primeira persistência do objeto.
     * Define as datas de criação e atualização iniciais.
     */
    @PrePersist
    protected void onCreate() {
        this.dataCriacao = LocalDate.now();
        this.dataAtualizacao = LocalDate.now();
    }

    /**
     * Callback do JPA executado antes de uma operação de atualização no banco.
     * Atualiza a data da última modificação.
     */
    @PreUpdate
    protected void onUpdate() {
        this.dataAtualizacao = LocalDate.now();
    }

    /**
     * Método auxiliar para adicionar um exercício a este treino, mantendo a
     * consistência do relacionamento bidirecional.
     * @param exercicio O exercício a ser adicionado.
     */
    public void addExercicio(Exercicio exercicio) {
        this.exercicios.add(exercicio);
        exercicio.setTreino(this);
    }

    /**
     * Método auxiliar para remover um exercício deste treino, mantendo a
     * consistência do relacionamento bidirecional.
     * @param exercicio O exercício a ser removido.
     */
    public void removeExercicio(Exercicio exercicio) {
        this.exercicios.remove(exercicio);
        exercicio.setTreino(null);
    }

    /**
     * Compara dois objetos Treino com base em seus IDs.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Treino treino = (Treino) o;
        return id != null && id.equals(treino.id);
    }

    /**
     * Gera um hash code baseado no ID do treino.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
