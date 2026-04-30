# StockIT

O projeto StockIT tem como objetivo facilitar o gerenciamento doméstico de alimentos por meio de uma plataforma web intuitiva. A aplicação permite ao usuário visualizar e organizar os alimentos armazenados em diferentes ambientes da casa, como geladeira, freezer ou despensa, promovendo o controle de validade, quantidades e sugestões de organização.

A proposta visa reduzir o desperdício alimentar ao notificar o vencimento dos produtos, auxiliar na criação de listas de compras. sugerir receitas com alimentos quase vencendo e oferecer uma experiência prática e visual, contribuindo com a economia doméstica e o consumo consciente.

## Alunos integrantes da equipe

Arthur Henrique Tristão Pinto

Davi Rafael de Oliveira Gurgel Martins

Gabriel Pereira Couto Rodrigues

## Professores responsáveis

Carolina Stephanie Jerônimo de Almeida

Wladmir Cardoso Brandao

# 🚀 Instruções de Utilização

### 0. Rodando a Aplicação(Se não for utilizar a versão hospedada)
1. Clone o repositório 
2. Abra a pasta de código e rode o arquivo Principal.java
3. Espere conectar com o banco de dados(logs no terminal avisam a conexão)

### 1. 📥 Acesso à Plataforma
1. Abra seu navegador (Chrome, Edge, Firefox).  
2. Acesse a URL da aplicação:  
   **http://localhost:4567** *(ou a [URL](https://plmg-cc-ti2-2025-2-g03-stockit-production.up.railway.app/modulos/home-page/home.html) de produção).*  
3. Faça login com seu usuário e senha.  
4. Caso não possua conta, utilize o fluxo de cadastro.

---

### 2. 🏠 Visão Geral do Sistema
Após o login, você pode:

- Visualizar ambientes de armazenamento (Geladeira, Freezer, Despensa…)
- Gerenciar o estoque de alimentos
- Criar listas de compras
- Acessar receitas baseadas em alimentos próximos do vencimento
- Utilizar o Sistema Inteligente (OCR) para cadastrar alimentos por foto/nota fiscal

---

### 3. 🗂 Cadastro de Ambientes
1. Acesse o menu **Ambientes**.  
2. Clique em **Adicionar Ambiente**.  
3. Preencha os campos:   
4. Clique em **Salvar**.

---

### 4. 🍎 Cadastro Manual de Alimentos
1. Abra o ambiente desejado (ex.: **Geladeira**).  
2. Clique em **Adicionar Alimento**.  
3. Preencha:  
   - Nome do alimento  
   - Quantidade  
   - Data de vencimento  
   - Categoria 
4. Clique em **Salvar**.

---

### 5. 🤖 Cadastro Inteligente por Foto/Nota Fiscal (OCR – Azure)
Essa funcionalidade utiliza **Azure Document Intelligence (OCR)** para automatizar o cadastro de itens.

**Como usar:**
1. Clique no botão **Cadastro por Foto**.  
2. Envie uma imagem ou PDF da nota fiscal.  
3. O sistema processará o arquivo e exibirá os itens detectados pela IA em um modal de revisão:

   - Checkbox para selecionar itens  
   - Nome do alimento (editável)  
   - Quantidade (editável)  
   - Data de vencimento (editável)  
   - Botão para excluir itens individualmente  

4. Escolha o ambiente onde os itens serão cadastrados.  
5. Clique em **Confirmar**.

**O que acontece internamente:**

- A IA extrai nome/descrição, quantidade e possíveis datas da nota fiscal.  
- O backend valida e identifica cada alimento.  
- Cria o alimento caso não exista.  
- Registra cada item como **ItemAmbiente**, já vinculado ao ambiente escolhido.

---

### 6. 📦 Gerenciamento do Estoque
Dentro de um ambiente, o sistema exibe:

- Nome  
- Quantidade  
- Data de vencimento  

Você pode:

- Editar  
- Excluir  
- Adicionar

---

### 7. 🛒 Listas de Compra
1. Acesse o menu **Listas de Compra**.  
2. Clique em **Criar Lista**.  
3. Adicione itens: 
4. Salve a lista.

---

### 8. 🍽 Receitas Baseadas no Vencimento
Acesse o menu **Receitas** para visualizar sugestões baseadas em:

- Alimentos próximos do vencimento  

Ideal para reduzir desperdício e planejar refeições.

---

### 9. 👍 Boas Práticas de Uso

- Revise datas de vencimento regularmente.  
- Utilize o **cadastro por foto** após compras grandes.  
- Antes de confirmar itens lidos pela IA, ajuste nome/quantidade caso necessário.  
- Consulte periodicamente itens próximos do vencimento e utilize as receitas sugeridas.
