package com.arsal.Extrator.de.Diarias;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.arsal.Extrator.de.Diarias.exception.PdfLeituraException;
import com.arsal.Extrator.de.Diarias.model.DadosPortaria;
import com.arsal.Extrator.de.Diarias.service.ExcelService;
import com.arsal.Extrator.de.Diarias.service.PdfService;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ExtratorDeDiariasApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ExtratorDeDiariasApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
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

        String caminhoBase = "C:/Extrator de Diárias ARSAL";
        String pastaEntrada = caminhoBase + "/PDFs_PARA_PROCESSAR";
        String pastaSaida = caminhoBase + "/PDFs_PROCESSADOS";
        String caminhoExcel = caminhoBase + "/Controle_Diarias.xlsx";

        File diretorioBase = new File(caminhoBase);
        if (!diretorioBase.exists()) {
            System.err.println("❌ A pasta base não existe: " + caminhoBase);
            return;
        }

        File[] listaDeArquivosBase = diretorioBase.listFiles();
        if (listaDeArquivosBase == null) {
            System.err.println("❌ Sem permissão para ler a pasta base.");
            return;
        }

        File diretorioEntrada = new File(pastaEntrada);
        if (!diretorioEntrada.exists() || !diretorioEntrada.isDirectory()) {
            System.err.println("❌ Pasta de entrada não encontrada: " + pastaEntrada);
            return;
        }

        File[] listaDePdfs = diretorioEntrada.listFiles((dir, nome) -> nome.toLowerCase().endsWith(".pdf"));
        if (listaDePdfs.length == 0) {
            System.out.println("✅ Nenhum PDF para processar.");
            return;
        }

        int arquivosProcessados = 0;

        try {
            excelService.abrirPlanilha(caminhoExcel);
            System.out.println("✅ Planilha carregada.");

            for (File arquivoPdf : listaDePdfs) {
                String caminhoPdfAtual = arquivoPdf.getAbsolutePath();
                System.out.println("\n--- Processando: " + arquivoPdf.getName() + " ---");

                try {
                    String textoExtraido = pdfService.extrairTextoDePdf(caminhoPdfAtual);
                    DadosPortaria ficha = pdfService.processarTexto(textoExtraido);
                    excelService.adicionarLinha(ficha);
                    System.out.println("✅ Dados adicionados.");

                    String caminhoDestino = pastaSaida + "/" + arquivoPdf.getName();
                    Files.move(Paths.get(caminhoPdfAtual), Paths.get(caminhoDestino), StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("✅ PDF movido para 'PDFs_PROCESSADOS'.");
                    arquivosProcessados++;

                } catch (Exception e) {
                    System.err.println("❌ Erro ao processar: " + arquivoPdf.getName());
                    System.err.println("Motivo: " + e.getMessage());
                }
            }

        } catch (PdfLeituraException e) {
            System.err.println("\n❌ ERRO AO ABRIR PLANILHA");
            System.err.println("Verifique se o Excel está fechado.");
            System.err.println("Detalhe: " + e.getMessage());

        } finally {
            excelService.salvarEFecharPlanilha(caminhoExcel);
        }

        System.out.println("\n==================================================");
        System.out.println("🏁 AUTOMAÇÃO CONCLUÍDA");
        System.out.println("Total de arquivos processados: " + arquivosProcessados);
        System.out.println("==================================================");
    }
}
