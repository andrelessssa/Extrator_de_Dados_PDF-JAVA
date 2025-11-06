package com.arsal.Extrator.de.Diarias.service;

import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import com.arsal.Extrator.de.Diarias.exception.PdfLeituraException; 

@Service
public class PdfService {

    public String extrairTextoDePdf(String caminhoDoArquivo) {
        
        System.out.println("Iniciando leitura do PDF em: " + caminhoDoArquivo);

        // 2. USAMOS O "try-with-resources" (mais moderno, sem .close())
        try (PDDocument documento = PDDocument.load(new File(caminhoDoArquivo))) {

            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(documento);

            System.out.println("Leitura do PDF concluída com sucesso.");
            return texto;

        } catch (IOException e) {
            // 3. MUDANÇA PRINCIPAL!
            // Em vez de retornar 'null', LANÇAMOS nossa exceção.
            System.err.println("Erro ao ler o arquivo PDF: " + e.getMessage());
            throw new PdfLeituraException("Falha ao ler o arquivo PDF: " + caminhoDoArquivo, e);
            // (Note que NÃO existe mais "return null;")
        }
    }
    
}