package model;

import java.sql.Date;

public class ItemAmbiente {
    private int idItemAmbiente;
    private int quantidade;
    private Date dataCadastro;
    private Date dataVencimento;
    private int idAlimento;
    private int idAmbiente;

    // Construtores
    public ItemAmbiente() {}

    public ItemAmbiente(int idItemAmbiente, int quantidade, Date dataCadastro, Date dataVencimento, int idAlimento, int idAmbiente) {
        this.idItemAmbiente = idItemAmbiente;
        this.quantidade = quantidade;
        this.dataCadastro = dataCadastro;
        this.dataVencimento = dataVencimento;
        this.idAlimento = idAlimento;
        this.idAmbiente = idAmbiente;
    }

    // Getters e Setters
    public int getIdItemAmbiente() {
        return idItemAmbiente;
    }

    public void setIdItemAmbiente(int idItemAmbiente) {
        this.idItemAmbiente = idItemAmbiente;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    
    public void setDataCadastro(Object dataCadastro) {
        if (dataCadastro instanceof String) {
            try {
                this.dataCadastro = Date.valueOf((String) dataCadastro);
            } catch (Exception e) {
                System.err.println("Formato inválido em dataCadastro: " + dataCadastro);
                this.dataCadastro = null;
            }
        } else if (dataCadastro instanceof Date) {
            this.dataCadastro = (Date) dataCadastro;
        } else {
            this.dataCadastro = null;
        }
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    
    public void setDataVencimento(Object dataVencimento) {
        if (dataVencimento instanceof String) {
            try {
                this.dataVencimento = Date.valueOf((String) dataVencimento);
            } catch (Exception e) {
                System.err.println("Formato inválido em dataVencimento: " + dataVencimento);
                this.dataVencimento = null;
            }
        } else if (dataVencimento instanceof Date) {
            this.dataVencimento = (Date) dataVencimento;
        } else {
            this.dataVencimento = null;
        }
    }

    public int getIdAlimento() {
        return idAlimento;
    }

    public void setIdAlimento(int idAlimento) {
        this.idAlimento = idAlimento;
    }

    public int getIdAmbiente() {
        return idAmbiente;
    }

    public void setIdAmbiente(int idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Override
    public String toString() {
        return "ItemAmbiente{" +
                "idItemAmbiente=" + idItemAmbiente +
                ", quantidade=" + quantidade +
                ", dataCadastro=" + dataCadastro +
                ", dataVencimento=" + dataVencimento +
                ", idAlimento=" + idAlimento +
                ", idAmbiente=" + idAmbiente +
                '}';
    }
}