package com.arsal.Extrator.de.Diarias;

// Imports para pastas e arquivos
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration; // 1. <<< IMPORT NOVO!

import com.arsal.Extrator.de.Diarias.exception.PdfLeituraException;
import com.arsal.Extrator.de.Diarias.model.DadosPortaria;
import com.arsal.Extrator.de.Diarias.service.ExcelService;
import com.arsal.Extrator.de.Diarias.service.PdfService;

@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class})
public class ExtratorDeDiariasApplication implements CommandLineRunner {

    /**
     * 2. <<< METODO MAIN ATUALIZADO (CORRIGE O ERRO DA PORTA 8080)
     * Diz ao Spring para NÃO iniciar um servidor web (Tomcat).
     * O robô vai rodar e desligar 100%.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ExtratorDeDiariasApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE); // <-- A MÁGICA
        app.run(args);
    }

    // Nossos dois "trabalhadores"
    @Autowired
    private PdfService pdfService;
    
    @Autowired
    private ExcelService excelService;

    /**
     * Este é o método principal que roda a automação.
     */
    @Override
    public void run(String... args) throws Exception {
        System.out.println("==================================================");
        System.out.println("🚀 INICIANDO AUTOMAÇÃO EM LOTE 🚀");
        System.out.println("==================================================");

        // --- CAMINHOS DE TRABALHO (Fixo em C: para Windows) ---
        String caminhoBase = "C:/Extrator de Diárias ARSAL";
        System.out.println("Usando a pasta base: " + caminhoBase);

        String pastaEntrada = caminhoBase + "/PDFs_PARA_PROCESSAR";
        String pastaSaida = caminhoBase + "/PDFs_PROCESSADOS";
        String caminhoExcel = caminhoBase + "/Controle_Diarias.xlsx";
        // --- Fim dos caminhos ---

        
        // --- ---------------------------------- ---
        // --- PASSO DE DIAGNÓSTICO ---
        // --- ---------------------------------- ---
        System.out.println("\n--- DIAGNÓSTICO INICIAL ---");
        File diretorioBase = new File(caminhoBase);
        
        if (!diretorioBase.exists()) {
            System.err.println("❌ ERRO DE DIAGNÓSTICO: A pasta base 'C:/Extrator de Diárias ARSAL' NÃO EXISTE.");
            System.err.println("Verifique o nome da pasta na raiz do C:");
            return; // Sair
        }
        
        System.out.println("O que o Java VÊ dentro de '" + caminhoBase + "':");
        File[] listaDeArquivosBase = diretorioBase.listFiles();
        
        if (listaDeArquivosBase == null) {
            System.err.println("❌ ERRO DE DIAGNÓSTICO: A pasta base existe, mas o Java não tem PERMISSÃO para ler o conteúdo dela.");
            return; // Sair
        }
        
        if (listaDeArquivosBase.length == 0) {
            System.out.println("    (A pasta base está vazia)");
        } else {
            for (File f : listaDeArquivosBase) {
                System.out.println("    -> " + f.getName());
            }
        }
        System.out.println("--- FIM DO DIAGNÓSTICO ---\n");
        // --- ---------------------------------- ---

        
        // --- CÓDIGO DE PROCESSAMENTO ---
        File diretorioEntrada = new File(pastaEntrada);
        
        if (!diretorioEntrada.exists() || !diretorioEntrada.isDirectory()) {
             System.err.println("❌ ERRO GRAVE: A pasta 'PDFs_PARA_PROCESSAR' não foi encontrada em:");
             System.err.println(pastaEntrada);
             System.err.println("Verifique se o nome da pasta no DIAGNÓSTICO acima bate EXATAMENTE.");
             return; 
        }

        File[] listaDePdfs = diretorioEntrada.listFiles((dir, nome) -> nome.toLowerCase().endsWith(".pdf"));

        if (listaDePdfs.length == 0) {
            System.out.println("✅ Nenhum PDF encontrado para processar.");
        } else {
            System.out.println("Encontrados " + listaDePdfs.length + " PDFs para processar...");
        }

        int arquivosProcessados = 0;
        
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
                System.err.println("O arquivo NÃO será movido.");
            } catch (Exception e) {
                System.err.println("❌ FALHA INESPERADA: " + arquivoPdf.getName());
                System.err.println("Motivo: " + e.getMessage());
                e.printStackTrace();
                System.err.println("O arquivo NÃO será movido.");
            }
        }

        System.out.println("\n==================================================");
        System.out.println("🏁 AUTOMAÇÃO CONCLUÍDA 🏁");
        System.out.println("Total de arquivos processados com sucesso: " + arquivosProcessados);
        System.out.println("==================================================");
    }
}