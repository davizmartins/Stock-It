package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

//Mapeando entidade ListaDeCompra
public class ListaDeCompra {
    private Integer id;            
    private String nome;           
    private LocalDate dataCompra;  
    private List<ItemListaCompra> itens;

    public ListaDeCompra() {
        this.itens = new ArrayList<>();
    }

    public ListaDeCompra(Integer id, String nome, LocalDate dataCompra) {
        this.id = id;
        this.nome = nome;
        this.dataCompra = dataCompra;
        this.itens = new ArrayList<>();
    }

    // Getters/Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public LocalDate getDataCompra() { return dataCompra; }
    public void setDataCompra(LocalDate dataCompra) { this.dataCompra = dataCompra; }

    public List<ItemListaCompra> getItens() { return itens; }
    public void setItens(List<ItemListaCompra> itens) { this.itens = itens; }

    // Sobrescrevendo método padrão
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListaDeCompra)) return false;
        ListaDeCompra that = (ListaDeCompra) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return (id == null) ? 0 : id.hashCode();
    }

    @Override
    public String toString() {
        return "ListaDeCompra{id=" + id + ", nome='" + nome + "', dataCompra=" + dataCompra + "}";
    }
}
