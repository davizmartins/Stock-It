# Documentação do Projeto
# 🍏 StockIT – Uma solução para gestão de alimentos

Aplicação web para **organizar estoques de alimentos** (residenciais e comerciais), reduzindo desperdício por meio do controle de validade, quantidade e ambientes (geladeira, despensa, freezer etc.).  
O projeto evoluiu de um protótipo 100% front-end para uma solução **full stack com backend em Java Spark, banco PostgreSQL e OCR para leitura automática de notas fiscais**.

## 👥 Equipe

- [Arthur Henrique Tristão Pinto](https://github.com/arthurhtp)  
- [Davi Rafael de Oliveira Gurgel Martins](https://github.com/davi080107)  
- [Gabriel Pereira Couto Rodrigues](https://github.com/GabrielPereira-PUC)  

---

## 🧩 Visão Geral da Solução

O StockIT permite:

- Cadastro de **ambientes** (geladeira, despensa, freezer, etc.);
- Gerenciamento de **alimentos** (nome, tipo, categoria, validade, quantidade, dono);
- **Alertas** para produtos próximos do vencimento;
- Criação e manutenção de **listas de compras**;
- Sugestão de uso de alimentos prestes a vencer como receitas;
- **Leitura automática de notas fiscais via OCR**, preenchendo/atualizando itens no estoque.

---

## 🏗️ Arquitetura Atual

A solução foi organizada em camadas:

1. **Front-end Web**
   - HTML5, CSS3, JavaScript
   - Interface responsiva com telas de:
     - Home (ambientes, alertas, listas, receitas)
     - Ambientes e alimentos
     - Lista de compras
     - Login e cadastro de usuários

2. **Back-end – API REST**
   - **Java + Spark Framework**
   - Implementação de endpoints para:
     - Autenticação de usuário
     - CRUD de ambientes, alimentos e listas de compra
     - Consulta de itens próximos da validade

3. **Banco de Dados**
   - **PostgreSQL**
   - Tabelas principais (conceitual):
     - `ambientes`
     - `alimentos`
     - `itens_ambiente`
     - `listas_compra` / `itens_lista`

4. **Módulo Inteligente – OCR de Nota Fiscal**
   - Serviço responsável por:
     - Ler a imagem/PDF da nota fiscal;
     - Extrair **itens, quantidade e informações relevantes**;
     - Sugerir ou preencher automaticamente o estoque no sistema.

5. **Ferramentas de desenvolvimento**
   - **Eclipse** – desenvolvimento do backend em Java;
   - **VS Code** – desenvolvimento do front-end;
   - GitHub – versionamento e hospedagem do front-end;

---
## ⚙️ Tecnologias Utilizadas

**Front-end**
- HTML5, CSS3, JavaScript
- Bootstrap 5
- Google Fonts, Font Awesome

**Back-end**
- Java 17+
- Spark Framework (API REST)
- PostgreSQL

**Ferramentas**
- Eclipse (backend)
- VS Code (frontend)
- Git, GitHub
- Miro (Design Thinking)
- Figma (wireframes e protótipo interativo)
- Discord (comunicação da equipe)

---

## 📚 Documentação Complementar

Alguns artefatos de documentação estão disponíveis na pasta [`/files`](./files) do repositório:

- **Design Thinking e Descoberta**
  - Matriz CSD  
  - Mapa de Stakeholders  
  - Entrevistas qualitativas com diferentes perfis (dona de bar, cafeteria, restaurante, dona de casa etc.)

- **Personas e Proposta de Valor**  
  - Personas para cliente empresa, pessoa física e gastronomia

- **Design de Interface**
  - Wireframes das principais telas  
  - Protótipo interativo no Figma

