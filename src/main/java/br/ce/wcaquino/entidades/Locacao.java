package br.ce.wcaquino.entidades;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class Locacao {
    private Usuario usuario;
    private List<Filme> filmes;
    private Date dataLocacao;
    private Date dataRetorno;
    private Double valor;
}