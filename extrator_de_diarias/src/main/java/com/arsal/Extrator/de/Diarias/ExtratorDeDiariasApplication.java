package com.arsal.Extrator.de.Diarias;

// 👇👇 NOVOS IMPORTS PARA LER PASTAS E MOVER ARQUIVOS 👇👇
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.arsal.Extrator.de.Diarias.exception.PdfLeituraException;
import com.arsal.Extrator.de.Diarias.model.DadosPortaria;
import com.arsal.Extrator.de.Diarias.service.ExcelService;
import com.arsal.Extrator.de.Diarias.service.PdfService;

@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class})
public class ExtratorDeDiariasApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ExtratorDeDiariasApplication.class, args);
    }

    @Autowired
    private PdfService pdfService;
    
    @Autowired
    private ExcelService excelService;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("==================================================");
        System.out.println("🚀 INICIANDO AUTOMAÇÃO EM LOTE 🚀");
        System.out.println("==================================================");

        // --- CAMINHOS DE TRABALHO (Use o caminho absoluto) ---
        // (Copie o caminho absoluto do seu projeto aqui)
        String caminhoBaseProjeto = "/Users/andrelessa/Documents/GitHub/Extrator_de_Dados_PDF-JAVA";

        String pastaEntrada = caminhoBaseProjeto + "/PDFs_PARA_PROCESSAR";
        String pastaSaida = caminhoBaseProjeto + "/PDFs_PROCESSADOS";
        String caminhoExcel = caminhoBaseProjeto + "/Controle_Diarias.xlsx";
        // --- Fim dos caminhos ---

        File diretorioEntrada = new File(pastaEntrada);
        File[] listaDePdfs = diretorioEntrada.listFiles((dir, nome) -> nome.toLowerCase().endsWith(".pdf"));

        if (listaDePdfs == null) {
            System.err.println("❌ ERRO: A pasta 'PDFs_PARA_PROCESSAR' não foi encontrada ou não é uma pasta.");
            return; // Encerra o programa
        }

        if (listaDePdfs.length == 0) {
            System.out.println("✅ Nenhum PDF encontrado para processar.");
        } else {
            System.out.println("Encontrados " + listaDePdfs.length + " PDFs para processar...");
        }

        int arquivosProcessados = 0;
        // --- LOOP PRINCIPAL (PARA CADA PDF NA PASTA) ---
        for (File arquivoPdf : listaDePdfs) {
            String caminhoPdfAtual = arquivoPdf.getAbsolutePath();
            System.out.println("\n--- Processando arquivo: " + arquivoPdf.getName() + " ---");

            try {
                // ETAPA 1: LER E PROCESSAR O PDF
                String textoExtraido = pdfService.extrairTextoDePdf(caminhoPdfAtual);
                DadosPortaria ficha = pdfService.processarTexto(textoExtraido);
                System.out.println("✅ PDF processado!");

                // ETAPA 2: ESCREVER NO EXCEL
                excelService.adicionarLinha(caminhoExcel, ficha);
                System.out.println("✅ Dados gravados no Excel!");

                // ETAPA 3: MOVER O ARQUIVO PROCESSADO
                String caminhoDestino = pastaSaida + "/" + arquivoPdf.getName();
                Files.move(Paths.get(caminhoPdfAtual), Paths.get(caminhoDestino), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ Arquivo movido para a pasta 'PDFs_PROCESSADOS'.");

                arquivosProcessados++;

            } catch (PdfLeituraException e) {
                System.err.println("❌ FALHA AO PROCESSAR ARQUIVO: " + arquivoPdf.getName());
                System.err.println("Motivo: " + e.getMessage());
                System.err.println("O arquivo NÃO será movido. Verifique o PDF e tente novamente.");
            } catch (Exception e) {
                System.err.println("❌ FALHA INESPERADA: " + arquivoPdf.getName());
                System.err.println("Motivo: " + e.getMessage());
                e.printStackTrace();
                System.err.println("O arquivo NÃO será movido.");
            }
        }
        // --- FIM DO LOOP ---

        System.out.println("\n==================================================");
        System.out.println("🏁 AUTOMAÇÃO CONCLUÍDA 🏁");
        System.out.println("Total de arquivos processados com sucesso: " + arquivosProcessados);
        System.out.println("==================================================");
    }
}