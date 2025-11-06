package com.arsal.Extrator.de.Diarias;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.arsal.Extrator.de.Diarias.exception.PdfLeituraException;
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

        // O caminho para o seu PDF (mantenha o que funcionou)
        String caminhoParaMeuPdf = "/Users/andrelessa/Desktop/Arsal.pdf"; 

        // 👇👇 MUDANÇA PRINCIPAL AQUI 👇👇
        try {
            // 1. TENTAMOS executar o código perigoso
            String textoExtraido = pdfService.extrairTextoDePdf(caminhoParaMeuPdf);

            // Se chegar aqui, deu tudo certo!
            System.out.println("\n--- TEXTO EXTRAÍDO DO PDF ---");
            System.out.println(textoExtraido);
            System.out.println("---------------------------------");

        } catch (PdfLeituraException e) {
            // 2. SE "pegarmos" nosso erro customizado, executamos isso:
            System.out.println("==================================================");
            System.out.println("❌ FALHA NO PROCESSAMENTO ❌");
            System.out.println("Motivo: " + e.getMessage()); // Mostra a mensagem amigável
            System.out.println("==================================================");
            // Opcional: imprimir o erro original para debug
            // e.printStackTrace(); 
        }

        System.out.println("==================================================");
        System.out.println("🏁 TESTE CONCLUÍDO 🏁");
        System.out.println("==================================================");
    }
}
