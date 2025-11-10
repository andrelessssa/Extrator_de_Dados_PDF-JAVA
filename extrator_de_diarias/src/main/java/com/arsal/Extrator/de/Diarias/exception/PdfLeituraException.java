package com.arsal.Extrator.de.Diarias.exception;

public class PdfLeituraException extends RuntimeException {

   
    public PdfLeituraException(String mensagem) {
        super(mensagem);
    }

   
    public PdfLeituraException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
    
}
