package com.arsal.Extrator.de.Diarias;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.arsal.Extrator.de.Diarias.exception.PdfLeituraException;
import com.arsal.Extrator.de.Diarias.model.DadosPortaria;
import com.arsal.Extrator.de.Diarias.service.PdfService;

@SpringBootApplication (exclude = {DataSourceAutoConfiguration.class})
public class ExtratorDeDiariasApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ExtratorDeDiariasApplication.class, args);
	}

	@Autowired
	private PdfService pdfService;

	@Override
    public void run(String... args) throws Exception {
        System.out.println("==================================================");
        System.out.println("🚀 INICIANDO TESTE DE LEITURA DO PDF 🚀");
        System.out.println("==================================================");

        String caminhoParaMeuPdf = "/Users/andrelessa/Desktop/Arsal.pdf"; 

        try {
            // 1. extrair o texto bruto
            String textoExtraido = pdfService.extrairTextoDePdf(caminhoParaMeuPdf);
            System.out.println("✅ Texto extraído com sucesso.");

            
            DadosPortaria ficha = pdfService.processarTexto(textoExtraido);

            
            System.out.println("\n--- 📊 DADOS EXTRAÍDOS 📊 ---");
            System.out.println("Nome: " + ficha.getBeneficiario());
            System.out.println("CPF: " + ficha.getCpf());
            System.out.println("Nº Processo: " + ficha.getNumeroProcesso());
            System.out.println("Matrícula: " + ficha.getMatricula());
            System.out.println("Cargo: " + ficha.getCargo());
            System.out.println("Lotação: " + ficha.getLotacao());
            System.out.println("---------------------------------");
            System.out.println("Destino (Trechos): " + ficha.getDestino());
            System.out.println("Data Saída: " + ficha.getDataInicio());
            System.out.println("Data Volta: " + ficha.getDataFim());
            System.out.println("Qtd. Diárias: " + ficha.getNumeroDiarias());

            System.out.println("Valor (R$): " + ficha.getValor());
            System.out.println("Finalidade: " + ficha.getFinalidadeViagem());
            System.out.println("Nº Portaria: " + ficha.getNumeroPortaria());
            System.out.println("Data Publicação: " + ficha.getDataPublicacaoPortaria());

            System.out.println("---------------------------------");


        } catch (PdfLeituraException e) {
            // Se falhar a LEITURA, cai aqui
            System.out.println("==================================================");
            System.out.println("❌ FALHA NA LEITURA DO PDF ❌");
            System.out.println("Motivo: " + e.getMessage());
            System.out.println("==================================================");
        } catch (Exception e) {
            // Se falhar o PROCESSAMENTO, cai aqui
            System.out.println("==================================================");
            System.out.println("❌ FALHA NO PROCESSAMENTO DO TEXTO ❌");
            System.out.println("Motivo: " + e.getMessage());
            e.printStackTrace(); // Imprime o erro completo do processamento
            System.out.println("==================================================");
        }

        System.out.println("==================================================");
        System.out.println("🏁 TESTE CONCLUÍDO 🏁");
        System.out.println("==================================================");
    }
}
