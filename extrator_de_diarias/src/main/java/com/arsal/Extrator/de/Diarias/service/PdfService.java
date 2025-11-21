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

    /**
     * Lê o conteúdo textual de um arquivo PDF.
     */
    public String extrairTextoDePdf(String caminhoDoArquivo) {
        System.out.println("📄 Lendo PDF: " + caminhoDoArquivo);
        try (PDDocument documento = PDDocument.load(new File(caminhoDoArquivo))) {
            PDFTextStripper stripper = new PDFTextStripper();
            String texto = stripper.getText(documento);
            System.out.println("✅ Leitura concluída.");
            return texto;
        } catch (IOException e) {
            System.err.println("❌ Erro ao ler PDF: " + e.getMessage());
            throw new PdfLeituraException("Falha ao ler o arquivo PDF: " + caminhoDoArquivo, e);
        }
    }

    /**
     * Processa o texto extraído do PDF e retorna os dados estruturados.
     */
    public DadosPortaria processarTexto(String textoBruto) {
        System.out.println("🔍 Iniciando processamento do texto...");

        DadosPortaria dados = new DadosPortaria();

        dados.setCpf(extrair(textoBruto, "CPF:\\s*([\\d.-]+)"));
        dados.setBeneficiario(extrair(textoBruto, "NOME:\\s*(.*?)\\s*CPF:"));
        dados.setMatricula(extrair(textoBruto, "MATR[ÍI]CULA:\\s*(\\d+-\\d|\\d+)"));
        dados.setCargo(extrair(textoBruto, "CARGO/FUNÇÃO:\\s*(.*?)\\s*(IDENTIDADE|LOTAÇÃO):"));
        dados.setLotacao(extrair(textoBruto, "LOTAÇÃO:\\s*(.*?)\\s*(BANCO|MEIO DE TRANSPORTE):"));
        dados.setDestino(extrair(textoBruto, "TRECHOS:\\s*(.*?)\\s*DATA DE SA[IÍ]DA"));
        dados.setDataInicio(extrair(textoBruto, "DATA DE SA[IÍ]DA:\\s*(\\d{2}/\\d{2}/\\d{4})"));
        dados.setDataFim(extrair(textoBruto, "DATA DE VOLTA:\\s*(\\d{2}/\\d{2}/\\d{4})"));
        dados.setNumeroDiarias(extrair(textoBruto, "QUANTIDADE DE DI[ÁA]RIAS:\\s*(\\d+(\\.\\d+)?)"));
        dados.setValor(extrair(textoBruto, "VALOR GLOBAL DAS DI[ÁA]RIAS.*?R\\$\\s*([\\d.,]+)"));
        dados.setFinalidadeViagem(extrair(textoBruto, "JUSTIFICATIVA E FINALIDADE DA VIAGEM\\s*(.*?)\\s*(PARTE II|JUSTIFICATIVA PARA O AFASTAMENTO)"));
        dados.setNumeroPortaria(extrair(textoBruto, "código verificador\\s*(\\d+)"));
        dados.setDataPublicacaoPortaria(extrair(textoBruto, "Diretora-Presidente.*?em\\s*(\\d{2}/\\d{2}/\\d{4})"));
        dados.setNumeroProcesso(extrair(textoBruto, "(E:\\d+\\.\\d+/\\d{4})"));

        System.out.println("✅ Processamento concluído.");
        return dados;
    }

    /**
     * Extrai um dado do texto usando regex.
     */
    private String extrair(String texto, String regex) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(texto);
        if (matcher.find()) {
            return matcher.group(1).trim().replaceAll("\\s+", " ");
        }
        System.out.println("⚠️ Não encontrou: " + regex);
        return "";
    }
}
