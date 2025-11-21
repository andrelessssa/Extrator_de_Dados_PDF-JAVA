package com.arsal.Extrator.de.Diarias.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.arsal.Extrator.de.Diarias.exception.PdfLeituraException;
import com.arsal.Extrator.de.Diarias.model.DadosPortaria;

@Service
public class ExcelService {

    private static final int INDICE_LINHA_CABECALHO = 2; // Onde os títulos estão (Linha 3)

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;

    public void abrirPlanilha(String caminhoArquivoExcel) {
        System.out.println("📊 Abrindo planilha: " + caminhoArquivoExcel);
        try (FileInputStream fis = new FileInputStream(new File(caminhoArquivoExcel))) {
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheetAt(0);
        } catch (IOException e) {
            System.err.println("❌ Erro ao abrir Excel: " + e.getMessage());
            throw new PdfLeituraException("Falha ao abrir o arquivo Excel: " + caminhoArquivoExcel, e);
        }
    }

    public void adicionarLinha(DadosPortaria dados) {
        if (sheet == null) {
            System.err.println("⚠️ Planilha não carregada. Pulando adição.");
            return;
        }

        int indiceProximaLinha = encontrarProximaLinhaVazia();
        Row proximaLinha = sheet.createRow(indiceProximaLinha);
        System.out.println("➕ Adicionando na linha: " + indiceProximaLinha);

        proximaLinha.createCell(0).setCellValue(dados.getNumeroProcesso());
        proximaLinha.createCell(1).setCellValue(dados.getBeneficiario());
        proximaLinha.createCell(2).setCellValue(dados.getCpf());
        proximaLinha.createCell(3).setCellValue(dados.getMatricula());
        proximaLinha.createCell(4).setCellValue(dados.getCargo());
        proximaLinha.createCell(5).setCellValue(dados.getDestino());
        proximaLinha.createCell(6).setCellValue(dados.getLotacao());
        proximaLinha.createCell(7).setCellValue(dados.getDataInicio());
        proximaLinha.createCell(8).setCellValue(dados.getDataFim());
        proximaLinha.createCell(9).setCellValue(dados.getFinalidadeViagem());
        proximaLinha.createCell(11).setCellValue(dados.getValor());
        proximaLinha.createCell(12).setCellValue(dados.getNumeroDiarias());
        proximaLinha.createCell(14).setCellValue(dados.getDataPublicacaoPortaria());
        proximaLinha.createCell(15).setCellValue(dados.getNumeroPortaria());
    }

    private int encontrarProximaLinhaVazia() {
        int linha = sheet.getFirstRowNum();
        while (linha <= sheet.getLastRowNum()) {
            Row row = sheet.getRow(linha);
            if (row == null || row.getCell(0) == null || row.getCell(0).getStringCellValue().trim().isEmpty()) {
                return linha;
            }
            linha++;
        }
        return linha; // Se todas estiverem preenchidas, adiciona no final
    }

    public void salvarEFecharPlanilha(String caminhoArquivoExcel) {
        if (workbook != null) {
            System.out.println("💾 Salvando alterações...");
            try (FileOutputStream fos = new FileOutputStream(new File(caminhoArquivoExcel))) {
                workbook.write(fos);
                workbook.close();
                System.out.println("✅ Planilha salva com sucesso!");
            } catch (IOException e) {
                System.err.println("❌ Erro ao salvar Excel: " + e.getMessage());
                throw new PdfLeituraException("Falha ao salvar o arquivo Excel: " + caminhoArquivoExcel, e);
            }
        } else {
            System.err.println("⚠️ Nenhum workbook carregado. Nada foi salvo.");
        }
    }
}
