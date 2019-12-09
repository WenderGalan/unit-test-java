package br.ce.wcaquino.entidades;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Usuario {
    private String nome;
}