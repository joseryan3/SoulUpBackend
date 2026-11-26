package br.com.senai.SoulUp.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class SoulUp {

    @Id
    private Long id;

    // outros atributos

    private int matricula;
    private String nome;
    private String curso;
    public SoulUp(int matricula, String nome, String curso) {
        this.matricula = matricula;
        this.nome = nome;
        this.curso = curso;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public int getMatricula() {
        return matricula;
    }
    public String getNome() {
        return nome;
    }
    public String getCurso() {
        return curso;
    }
}