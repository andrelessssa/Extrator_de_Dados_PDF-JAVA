package com.arsal.Extrator.de.Diarias.service;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import com.arsal.Extrator.de.Diarias.exception.PdfLeituraException;
import com.arsal.Extrator.de.Diarias.model.DadosPortaria;

@Service
public class PdfService {

   
    public String extrairTextoDePdf(String caminhoDoArquivo) {
        
        System.out.println("Iniciando leitura do PDF em: " + caminhoDoArquivo);

        try (PDDocument documento = PDDocument.load(new File(caminhoDoArquivo))) {

            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(documento);

            System.out.println("Leitura do PDF concluída com sucesso.");
            return texto;

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo PDF: " + e.getMessage());
            throw new PdfLeituraException("Falha ao ler o arquivo PDF: " + caminhoDoArquivo, e);
        }
    }

   
    public DadosPortaria processarTexto(String textoBruto){
        System.out.println("Iniciando processamento do texto...");
        DadosPortaria dados = new DadosPortaria();

        // --- DEFINIÇÃO DAS NOSSAS "ISCAS" (REGEX) ---
        String regexCpf = "CPF:\\s*([0-9]{3}\\.[0-9]{3}\\.[0-9]{3}-[0-9]{2})";
        String regexNome = "NOME:\\s*(.*?)CPF:";
        String regexProcesso = "SEI\\s*(E:\\d+\\.\\d+\\/\\d{4})";
        String regexMatricula = "MATRÍCULA:\\s*(\\d+-\\d)";
        String regexCargo = "CARGO/FUNÇÃO:\\s*(.*?)MATRÍCULA:";
        String regexLotacao = "LOTAÇÃO:\\s*(.*?)BANCO:";
        String regexTrechos = "TRECHOS:\\s*(.*?)DATA"; 
        String regexDataSaida = "DATA\\s+DE\\s+SAÍDA:\\s*(\\d{2}\\/\\d{2}\\/\\d{4})";
        String regexDataVolta = "DATA\\s+DE\\s+VOLTA:\\s*(\\d{2}\\/\\d{2}\\/\\d{4})";
        String regexQtdDiarias = "QUANTIDADE\\s+DE\\s+DIÁRIAS:\\s*(\\d+)";

        String regexValor = "MOEDA\\s+CORRENTE:\\s*R\\$\\s*([0-9.,]+)";
        String regexFinalidade = "JUSTIFICATIVA\\s+E\\s+FINALIDADE\\s+DA\\s+VIAGEM\\s+(.*?)PARTE\\s+II";
        String regexNumPortaria = "código\\s+verificador\\s*(\\d+)";
        String regexDataPub = "Diretora-Presidente\\s+em\\s*(\\d{2}\\/\\d{2}\\/\\d{4})";

        // --- "PESCANDO" OS DADOS ---
        dados.setCpf(extrairDado(textoBruto, regexCpf));
        dados.setBeneficiario(extrairDado(textoBruto, regexNome));
        dados.setNumeroProcesso(extrairDado(textoBruto, regexProcesso));
        dados.setMatricula(extrairDado(textoBruto, regexMatricula));
        dados.setCargo(extrairDado(textoBruto, regexCargo));
        dados.setLotacao(extrairDado(textoBruto, regexLotacao));
        dados.setDestino(extrairDado(textoBruto, regexTrechos));
        dados.setDataInicio(extrairDado(textoBruto, regexDataSaida));
        dados.setDataFim(extrairDado(textoBruto, regexDataVolta));
        dados.setNumeroDiarias(extrairDado(textoBruto, regexQtdDiarias));
        dados.setValor(extrairDado(textoBruto, regexValor));
        dados.setFinalidadeViagem(extrairDado(textoBruto, regexFinalidade));
        dados.setNumeroPortaria(extrairDado(textoBruto, regexNumPortaria));
        dados.setDataPublicacaoPortaria(extrairDado(textoBruto, regexDataPub));

        System.out.println("Processamento concluído.");
        return dados;
    }

    
    private String extrairDado(String textoBruto, String regex) {
       
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(textoBruto);   

        if (matcher.find()) {
            
         
            String dadoCapturado = matcher.group(1); 
            
          
            if (dadoCapturado == null) {
                System.out.println("Atenção: Regex encontrou padrão, mas o grupo capturado é NULO para: " + regex);
                return null; // Retornamos nulo de forma segura
            }
            
            
            String dadoLimpo = dadoCapturado.trim().replaceAll("\\s+", " ");
            return dadoLimpo;
        }

        // 5. Se não achou nada, retorna null
        System.out.println("Atenção: Regex não encontrou padrão para: " + regex);
        return null;
    }
    
}