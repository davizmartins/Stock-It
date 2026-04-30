package model;

//Mapeando entidade ItemListaDeCompra
public class ItemListaCompra {
    private Integer id;          
    private Integer quantidade;  
    private char status; 
    private Integer idAlimento;  
    private Integer idLista;    

    public ItemListaCompra() {}

    public ItemListaCompra(Integer id, Integer quantidade, char status, Integer idAlimento, Integer idLista) {
        this.id = id;
        this.quantidade = quantidade;
        this.status = status;
        this.idAlimento = idAlimento;
        this.idLista = idLista;
    }

    // Getters/Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public char getStatus() { return status; }
    public void setStatus(char status) { this.status = status; }

    public Integer getIdAlimento() { return idAlimento; }
    public void setIdAlimento(Integer idAlimento) { this.idAlimento = idAlimento; }

    public Integer getIdLista() { return idLista; }
    public void setIdLista(Integer idLista) { this.idLista = idLista; }

    // Sobrescrevendo método padrão
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemListaCompra)) return false;
        ItemListaCompra that = (ItemListaCompra) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    @Override
    public String toString() {
        return "ItemListaCompra{id=" + id + ", qtd=" + quantidade + ", status=" + status +
                ", idAlimento=" + idAlimento + ", idLista=" + idLista + "}";
    }
}
