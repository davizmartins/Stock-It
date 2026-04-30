package model;

public class ItemNotaOcr {

    private String descricao;
    private Integer quantidade;
    private String dataVencimento; 
    
    
    public ItemNotaOcr() {
    }
    
    public ItemNotaOcr(String descricao, Integer quantidade) {
        this.descricao = descricao;
        this.quantidade = quantidade;
    }
    
    public String getDataVencimento() {
        return dataVencimento;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public void setDataVencimento(String dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    @Override
    public String toString() {
        return "ItemNotaOcr{" +
                "descricao='" + descricao + '\'' +
                ", quantidade=" + quantidade +
                '}';
    }
}
