package model;

public class Alimento {
    private Integer id;
    private String nome;
    private Integer idCategoria;

    public Alimento() {}

    public Alimento(Integer id, String nome, Integer idCategoria) {
        this.id = id;
        this.nome = nome;
        this.idCategoria = idCategoria;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Integer idCategoria) { this.idCategoria = idCategoria; }

    @Override
    public String toString() {
        return "Alimento{id=" + id + ", nome='" + nome + "', idCategoria=" + idCategoria + "}";
    }
}
