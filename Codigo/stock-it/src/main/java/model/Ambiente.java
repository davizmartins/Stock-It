package model;

// Mapeando entidade Ambiente
public class Ambiente {
    private Integer id;                
    private String nomeAmbiente;       
    private char tipoAmbiente;         

    public Ambiente() {}

    public Ambiente(Integer id, String nomeAmbiente, char tipoAmbiente) {
        this.id = id;
        this.nomeAmbiente = nomeAmbiente;
        this.tipoAmbiente = tipoAmbiente;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNomeAmbiente() { return nomeAmbiente; }
    public void setNomeAmbiente(String nomeAmbiente) { this.nomeAmbiente = nomeAmbiente; }

    public char getTipoAmbiente() { return tipoAmbiente; }
    public void setTipoAmbiente(char tipoAmbiente) { this.tipoAmbiente = tipoAmbiente; }

    // Sobrescrevendo equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ambiente)) return false;
        Ambiente that = (Ambiente) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    // Representação textual
    @Override
    public String toString() {
        return "Ambiente{id=" + id + ", nomeAmbiente='" + nomeAmbiente + '\'' + ", tipoAmbiente=" + tipoAmbiente + '}';
    }
}

