# Extrator_de_Dados_PDF

# 🚀 Extrator de Diárias ARSAL

### 📖 Sobre o Projeto
Este projeto é uma ferramenta de automação desenvolvida para a **ARSAL (Agência Reguladora de Serviços Públicos do Estado de Alagoas)**.
O objetivo é otimizar o setor de TI e Administrativo, automatizando a extração de dados de portarias de diárias (arquivos PDF) e consolidando essas informações em planilhas Excel (.xlsx) para controle e relatórios.

### ⚙️ Funcionalidades
- [x] Leitura de arquivos PDF padronizados (Portarias de Diárias).
- [x] Extração inteligente de campos específicos (ex: Beneficiário, Processo, Datas, Valores).
- [x] Geração automática de planilha Excel com os dados extraídos.

### 🛠️ Tecnologias Utilizadas
* **Java** (Versão 17+)
* **Spring Boot** (Framework base)
* **Maven** (Gerenciamento de dependências)
* **Apache PDFBox** (Leitura de PDFs)
* **Apache POI** (Geração de arquivos Excel)

### 📋 Campos Extraídos
A aplicação busca identificar e extrair os seguintes dados das portarias:
* Nº do Processo e Nº da Portaria
* Dados do Beneficiário (Nome, CPF, Matrícula, Cargo, Lotação)
* Detalhes da Viagem (Destino, Data Início/Fim, Finalidade)
* Valores (Nº de Diárias, Valor Total R$)
* Outros (CPOF, Data de Publicação)

---
Desenvolvido por **André** como parte de iniciativas de modernização e automação de processos na ARSAL.
