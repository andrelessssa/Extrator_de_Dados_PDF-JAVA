package com.arsal.Extrator.de.Diarias.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.arsal.Extrator.de.Diarias.exception.PdfLeituraException;
import com.arsal.Extrator.de.Diarias.model.DadosPortaria;

@Service
public class ExcelService {

    // Com base na sua foto, o cabeçalho está na Linha 3 (que é índice 2)
    private static final int INDICE_LINHA_CABECALHO = 2;

    public void adicionarLinha(String caminhoArquivoExcel, DadosPortaria dados) {
        
        System.out.println("Iniciando gravação no Excel: " + caminhoArquivoExcel);

        try {
            // 1. ABRE O ARQUIVO EXISTENTE PARA LEITURA
            FileInputStream fis = new FileInputStream(new File(caminhoArquivoExcel));
            XSSFWorkbook workbook = new XSSFWorkbook(fis);
            XSSFSheet sheet = workbook.getSheetAt(0);

            // --- 👇👇 LÓGICA ATUALIZADA PARA ACHAR A PRÓXIMA LINHA VAZIA 👇👇 ---
            
            Row proximaLinha;
            // Começa a procurar a partir da linha *abaixo* do cabeçalho (Linha 4, índice 3)
            int indiceProximaLinha = INDICE_LINHA_CABECALHO + 1; 

            while (true) {
                Row linhaAtual = sheet.getRow(indiceProximaLinha);

                // Caso 1: A linha NÃO EXISTE FISICAMENTE (ex: o formatado acabou)
                // Este é o nosso local! Criamos a linha.
                if (linhaAtual == null) {
                    proximaLinha = sheet.createRow(indiceProximaLinha);
                    System.out.println("Encontrada linha nova (não existia) no índice: " + indiceProximaLinha);
                    break; 
                }

                // Caso 2: A linha EXISTE, mas está VAZIA (vamos checar a Célula A)
                Cell cellA = linhaAtual.getCell(0); // Pega a primeira célula (Nº Processo)
                
                if (cellA == null || cellA.getCellType() == CellType.BLANK) {
                    // A linha existe, mas está vazia. É o nosso local!
                    // NÃO criamos uma linha nova, REUTILIZAMOS a linha formatada.
                    proximaLinha = linhaAtual; 
                    System.out.println("Encontrada linha formatada vazia no índice: " + indiceProximaLinha);
                    break;
                }
                
                // Caso 3: A linha existe E a Célula A está preenchida.
                // Continuamos procurando a próxima...
                indiceProximaLinha++;
            }
            
            // --- FIM DA NOVA LÓGICA ---

            // Fecha o leitor (MUITO IMPORTANTE fazer isso ANTES de salvar)
            fis.close();

            // 6. PREENCHE AS CÉLULAS da linha que encontramos
            // (Usamos .createCell() - ele cria a célula se não existir ou sobrescreve se existir)
            
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
            // proximaLinha.createCell(10).setCellValue(dados.getTipoFinalidade()); // (Está null por enquanto)
            proximaLinha.createCell(11).setCellValue(dados.getValor());
            proximaLinha.createCell(12).setCellValue(dados.getNumeroDiarias());
            // proximaLinha.createCell(13).setCellValue(dados.getCpof()); // (Está null por enquanto)
            proximaLinha.createCell(14).setCellValue(dados.getDataPublicacaoPortaria());
            proximaLinha.createCell(15).setCellValue(dados.getNumeroPortaria());
            // ... (adicionar as outras colunas de "Pagamento", etc. se elas existirem na planilha)


            // 7. ABRE O ARQUIVO PARA ESCRITA (PARA SALVAR)
            FileOutputStream fos = new FileOutputStream(new File(caminhoArquivoExcel));
            
            // 8. ESCREVE AS MUDANÇAS E SALVA
            workbook.write(fos);
            
            // 9. FECHA TUDO
            workbook.close();
            fos.close();

            System.out.println("✅ Linha adicionada ao Excel com sucesso!");

        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo Excel: " + e.getMessage());
            throw new PdfLeituraException("Falha ao escrever no arquivo Excel: " + caminhoArquivoExcel, e);
        }
    }
}