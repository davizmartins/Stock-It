package model;

// Mapeando entidade CategoriaAlimento
public class CategoriaAlimento {
    private Integer id;                 
    private String nomeCategoria;       

    public CategoriaAlimento() {}

    public CategoriaAlimento(Integer id, String nomeCategoria) {
        this.id = id;
        this.nomeCategoria = nomeCategoria;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }

    // Sobrescrevendo equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CategoriaAlimento)) return false;
        CategoriaAlimento that = (CategoriaAlimento) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    // Representação textual
    @Override
    public String toString() {
        return "CategoriaAlimento{id=" + id + ", nomeCategoria='" + nomeCategoria + "'}";
    }
}
