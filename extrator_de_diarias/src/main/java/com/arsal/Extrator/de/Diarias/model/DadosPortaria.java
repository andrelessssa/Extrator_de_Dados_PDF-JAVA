package com.arsal.Extrator.de.Diarias.model;

import lombok.Data;

@Data
public class DadosPortaria {

    private String numeroProcesso;
    private String beneficiario;
    private String cpf;
    private String matricula;
    private String cargo;
    private String destino;
    private String lotacao;
    private String dataInicio;
    private String dataFim;
    private String finalidadeViagem;
    private String tipoFinalidade; 
    private String valor; 
    private String numeroDiarias;
    private String cpof;
    private String dataPublicacaoPortaria;
    private String numeroPortaria;
    private String pagamento;
    private String dataPrestacaoContas;
    private String situacaoPrestacaoContas;
    private String observacao;
    
}
