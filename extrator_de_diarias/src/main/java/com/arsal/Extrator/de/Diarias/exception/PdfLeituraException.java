package com.arsal.Extrator.de.Diarias.exception;

public class PdfLeituraException extends RuntimeException {

    /**
     * Construtor que aceita uma mensagem de erro amigável.
     * @param mensagem A mensagem explicando o que deu errado.
     */
    public PdfLeituraException(String mensagem) {
        super(mensagem);
    }

    /**
     * Construtor que aceita a mensagem E a "causa" raiz do erro.
     * Isso é útil para sabermos o erro original (ex: a IOException).
     * @param mensagem A mensagem explicando o que deu errado.
     * @param causa A exceção original (ex: FileNotFoundException).
     */
    public PdfLeituraException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
    
}
