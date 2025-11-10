package com.arsal.Extrator.de.Diarias;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.arsal.Extrator.de.Diarias.exception.PdfLeituraException;
import com.arsal.Extrator.de.Diarias.model.DadosPortaria;
import com.arsal.Extrator.de.Diarias.service.ExcelService; // 1. IMPORTA O NOVO SERVIÇO
import com.arsal.Extrator.de.Diarias.service.PdfService;

@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class})
public class ExtratorDeDiariasApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ExtratorDeDiariasApplication.class, args);
    }

    // 2. PEDE AO SPRING PARA INJETAR OS DOIS SERVIÇOS
    @Autowired
    private PdfService pdfService;
    
    @Autowired
    private ExcelService excelService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==================================================");
        System.out.println("🚀 INICIANDO AUTOMAÇÃO COMPLETA 🚀");
        System.out.println("==================================================");

        // Nossos arquivos
        String caminhoPdf = "/Users/andrelessa/Desktop/Arsal.pdf"; // Seu PDF na Mesa
        String caminhoExcel = "Controle_Diarias.xlsx"; // Sua planilha na RAIZ DO PROJETO

        try {
            // --- ETAPA 1: LER E PROCESSAR O PDF ---
            System.out.println("Lendo o PDF...");
            String textoExtraido = pdfService.extrairTextoDePdf(caminhoPdf);
            DadosPortaria ficha = pdfService.processarTexto(textoExtraido);
            System.out.println("✅ PDF processado com sucesso!");

            // --- ETAPA 2: ESCREVER NO EXCEL ---
            System.out.println("Gravando no Excel...");
            excelService.adicionarLinha(caminhoExcel, ficha);

            // Se chegamos aqui, deu tudo certo!
            System.out.println("\n--- 🎉 SUCESSO! 🎉 ---");
            System.out.println("Dados extraídos e salvos na planilha!");
            System.out.println("---------------------------------");

        } catch (PdfLeituraException e) {
            // Pega erros do PDF OU do Excel
            System.out.println("==================================================");
            System.out.println("❌ FALHA NA AUTOMAÇÃO ❌");
            System.out.println("Motivo: " + e.getMessage());
            System.out.println("==================================================");
        } catch (Exception e) {
            // Pega qualquer outro erro inesperado
            System.out.println("==================================================");
            System.out.println("❌ FALHA INESPERADA NO PROCESSAMENTO ❌");
            System.out.println("Motivo: " + e.getMessage());
            e.printStackTrace(); // Imprime o erro completo
            System.out.println("==================================================");
        }

        System.out.println("==================================================");
        System.out.println("🏁 AUTOMAÇÃO CONCLUÍDA 🏁");
        System.out.println("==================================================");
    }
}