package br.com.tutorialandroid.splashscreen.telaDeCorrida;

import java.util.List;

/**
 * Created by Renato Almeida on 24/07/2017.
 */

public class Historico {

    private int id;
    private String nome;
    private int preco;
    private String descricao;

    //COSNTRUTORES


    public Historico(int id, String nome, int preco, String descricao) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
    }


    //Setters e Getters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getPreco() {
        return preco;
    }

    public void setPreco(int preco) {
        this.preco = preco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
