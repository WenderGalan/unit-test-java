package br.ce.wcaquino.entidades;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Filme {
    private String nome;
    private Integer estoque;
    private Double precoLocacao;
}