const apiUrl = "";
const container = document.getElementById('listadeCompras');

let listaSelecionada = null;
let itemSelecionado = null;

// Funções de Modal
function abrirModal(id) {
    document.getElementById(id).classList.add('ativo');
}

function fecharModal(id) {
    document.getElementById(id).classList.remove('ativo');
}

// Carregar Dados
async function carregarDados() {
    container.innerHTML = '';
    try {
        const listaResponse = await fetch(`${apiUrl}/lista_compra`);
        if (!listaResponse.ok) throw new Error(`Erro ao carregar listas: ${listaResponse.status}`);
        let listas = await listaResponse.json();
        
        const alimentosResponse = await fetch(`${apiUrl}/alimentos`);
        if (!alimentosResponse.ok) {
            throw new Error(`Erro ao carregar alimentos: ${alimentosResponse.status}`);
        }
        const alimentos = await alimentosResponse.json();

        // Carregar itens do endpoint separado
        const itensResponse = await fetch(`${apiUrl}/itens_lista_compra`);
        if (!itensResponse.ok) throw new Error(`Erro ao carregar itens: ${itensResponse.status}`);
        const itens = await itensResponse.json();

        // Associar itens às listas corretas
        listas = listas.map(lista => {
            const itensLista = Array.isArray(itens) 
                ? itens.filter(item => item.idLista === lista.id)
                : [];
            
            // Normalizar a estrutura dos itens
            const itensNormalizados = itensLista.map(item => ({
                id: item.id,
                alimentoId: item.idAlimento,
                quantidade: item.quantidade,
                comprado: item.status === 'C'
            }));
            
            return { ...lista, itens: itensNormalizados };
        });

        if (!Array.isArray(alimentos)) {
            alimentos = [];
        }

        listas.forEach(lista => {
            const listaSection = document.createElement('section');
            listaSection.classList.add('lista-compra');

            // Header com título e botão de menu
            const headerDiv = document.createElement('div');
            headerDiv.classList.add('lista-header');

            const titulo = document.createElement('h2');
            titulo.textContent = lista.nome;
            headerDiv.appendChild(titulo);

            const menuBtn = document.createElement('button');
            menuBtn.className = 'btn-menu-lista';
            menuBtn.innerHTML = '<i class="fa-solid fa-ellipsis-vertical"></i>';
            menuBtn.onclick = (e) => abrirMenuLista(e, lista);
            headerDiv.appendChild(menuBtn);

            listaSection.appendChild(headerDiv);

            if (!lista.itens || lista.itens.length === 0) {
                const vazio = document.createElement('p');
                vazio.textContent = 'Nenhum item nesta lista';
                listaSection.appendChild(vazio);
            } else {
                lista.itens.forEach(item => {
                    const alimento = alimentos.find(a => a.id === item.alimentoId);
                    if (!alimento) {
                        return;
                    }

                    const itemDiv = document.createElement('div');
                    itemDiv.classList.add('item-lista');

                    const checkbox = document.createElement('input');
                    checkbox.type = 'checkbox';
                    checkbox.classList.add('item-checkbox');
                    checkbox.checked = item.comprado || false;

                    const infoDiv = document.createElement('div');
                    infoDiv.classList.add('item-info');

                    const nome = document.createElement('h3');
                    nome.textContent = `${alimento.nome}`;

                    const quantidade = document.createElement('p');
                    quantidade.textContent = `Quantidade: ${item.quantidade}`;

                    infoDiv.appendChild(nome);
                    infoDiv.appendChild(quantidade);

                    const botoes = document.createElement('div');
                    botoes.classList.add('botoes-item');

                    const btnEditar = document.createElement('button');
                    btnEditar.textContent = 'Editar';
                    btnEditar.className = 'botao botao-salvar';
                    btnEditar.onclick = () => {
                        listaSelecionada = lista;
                        itemSelecionado = item;
                        document.getElementById('editar-quantidade').value = item.quantidade;
                        abrirModal('modal-editar');
                    };

                    const btnExcluir = document.createElement('button');
                    btnExcluir.textContent = 'Excluir';
                    btnExcluir.className = 'botao botao-excluir';
                    btnExcluir.onclick = () => {
                        listaSelecionada = lista;
                        itemSelecionado = item;
                        document.getElementById('excluir-nome').textContent = alimento.nome;
                        abrirModal('modal-excluir');
                    };

                    botoes.appendChild(btnEditar);
                    botoes.appendChild(btnExcluir);

                    itemDiv.appendChild(checkbox);
                    itemDiv.appendChild(infoDiv);
                    itemDiv.appendChild(botoes);

                    listaSection.appendChild(itemDiv);
                });
            }

            container.appendChild(listaSection);
        });

    } catch (error) {
        console.error('❌ Erro ao carregar dados:', error);
        container.innerHTML = '<p style="color: red; padding: 20px;">Erro: ' + error.message + '</p>';
    }
}

// Abrir Menu Dropdown da Lista
function abrirMenuLista(event, lista) {
    event.stopPropagation();
    
    // Fechar menu anterior se existir
    const menuAberto = document.querySelector('.menu-lista.ativo');
    if (menuAberto) menuAberto.remove();
    
    const menu = document.createElement('div');
    menu.classList.add('menu-lista', 'ativo');
    menu.innerHTML = `
        <button onclick="abrirModalEditarLista(${lista.id})" class="menu-item">
            <i class="fa-solid fa-pen"></i> Editar
        </button>
        <button onclick="abrirModalExcluirLista(${lista.id})" class="menu-item menu-excluir">
            <i class="fa-solid fa-trash-can"></i> Excluir
        </button>
    `;
    
    const headerDiv = event.target.closest('.lista-header');
    headerDiv.appendChild(menu);
    
    // Fechar menu ao clicar em outro lugar
    document.addEventListener('click', function fecharMenu(e) {
        if (!e.target.closest('.menu-lista') && !e.target.closest('.btn-menu-lista')) {
            menu.remove();
            document.removeEventListener('click', fecharMenu);
        }
    });
}

// Carregar Listas no Select
async function carregarListasNoSelect() {
    const listas = await (await fetch(`${apiUrl}/lista_compra`)).json();
    const select = document.getElementById('cadastro-lista');
    select.innerHTML = '<option disabled selected>Selecione uma Lista</option>';
    listas.forEach(lista => {
        const option = document.createElement('option');
        option.value = lista.id;
        option.textContent = lista.nome;
        select.appendChild(option);
    });
}

// Adicionar Item
async function adicionarItem() {
    const nome = document.getElementById('cadastro-nome').value.trim();
    const quantidade = parseInt(document.getElementById('cadastro-quantidade').value);
    const listaIdSelecionada = document.getElementById('cadastro-lista').value;
    const nomeNovaLista = document.getElementById('nova-lista').value.trim();

    if (!nome || isNaN(quantidade) || quantidade <= 0) {
        alert('Preencha todos os campos obrigatórios!');
        return;
    }

    try {
        const alimentosRes = await fetch(`${apiUrl}/alimentos`);
        if (!alimentosRes.ok) throw new Error(`Erro ao carregar alimentos: ${alimentosRes.status}`);
        const alimentos = await alimentosRes.json();
        
        let alimento = alimentos.find(a => a.nome === nome);

        if (!alimento) {
            const resp = await fetch(`${apiUrl}/alimentos`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nome })
            });
            if (!resp.ok) throw new Error(`Erro ao criar alimento: ${resp.status}`);
            alimento = await resp.json();
        }

        let listaId = listaIdSelecionada ? parseInt(listaIdSelecionada) : null;

        // Se criar nova lista
        if (nomeNovaLista) {
            const novaListaRes = await fetch(`${apiUrl}/lista_compra`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ nome: nomeNovaLista })
            });
            if (!novaListaRes.ok) throw new Error(`Erro ao criar lista: ${novaListaRes.status}`);
            const listaCriada = await novaListaRes.json();
            listaId = listaCriada.id;
        } else {
            if (!listaId) {
                alert('Selecione uma lista existente ou informe o nome da nova lista.');
                return;
            }
        }

        // Adicionar item ao endpoint correto com estrutura correta
        const novoItem = {
            idAlimento: alimento.id,
            idLista: listaId,
            quantidade: quantidade,
            status: 'P'
        };

        const itemRes = await fetch(`${apiUrl}/itens_lista_compra`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(novoItem)
        });

        if (!itemRes.ok) {
            const errorText = await itemRes.text();
            throw new Error(`Erro ao adicionar item: ${itemRes.status}`);
        }

        alert('Item adicionado com sucesso!');
        carregarDados();
        carregarListasNoSelect();
        fecharModal('modal-cadastro');
        document.getElementById('cadastro-nome').value = '';
        document.getElementById('cadastro-quantidade').value = '1';
        document.getElementById('nova-lista').value = '';
    } catch (error) {
        console.error('Erro ao adicionar item:', error);
        alert('Erro: ' + error.message);
    }
}

// Salvar Edição
async function salvarEdicao() {
    const novaQuantidade = parseInt(document.getElementById('editar-quantidade').value);
    if (!novaQuantidade || novaQuantidade <= 0) {
        alert('Informe uma quantidade válida');
        return;
    }

    try {
        await fetch(`${apiUrl}/itens_lista_compra/${itemSelecionado.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                idLista: listaSelecionada.id, 
                idAlimento: itemSelecionado.alimentoId, 
                quantidade: novaQuantidade, 
                status: itemSelecionado.comprado ? 'C' : 'P'
            })
        });

        carregarDados();
        fecharModal('modal-editar');
        alert('Item atualizado com sucesso!');
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao editar item');
    }
}

// Confirmar Exclusão
async function confirmarExclusao() {
    try {
        await fetch(`${apiUrl}/itens_lista_compra/${itemSelecionado.id}`, {
            method: 'DELETE'
        });

        carregarDados();
        fecharModal('modal-excluir');
        alert('Item removido com sucesso!');
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao excluir item');
    }
}

// Abrir Modal para Criar Nova Lista
function abrirModalCriarLista() {
    document.getElementById('nova-lista-nome').value = '';
    const today = new Date().toISOString().split('T')[0];
    document.getElementById('nova-lista-data').value = today;
    abrirModal('modal-criar-lista');
}

// Criar Nova Lista
async function criarNovaLista() {
    const nomeLista = document.getElementById('nova-lista-nome').value.trim();

    if (!nomeLista) {
        alert('Digite um nome para a nova lista!');
        return;
    }

    try {
        const dataCompraVal = document.getElementById('nova-lista-data').value || null;
        const novaLista = {
            nome: nomeLista,
            dataCompra: dataCompraVal
        };

        const response = await fetch(`${apiUrl}/lista_compra`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(novaLista)
        });

        if (response.ok) {
            carregarDados();
            carregarListasNoSelect();
            fecharModal('modal-criar-lista');
            document.getElementById('nova-lista-nome').value = '';
            alert('Lista criada com sucesso!');
        } else {
            alert('Erro ao criar lista');
        }
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao criar lista');
    }
}

// Abrir Modal para Editar Nome da Lista
async function abrirModalEditarLista(listaId) {
    try {
        const lista = await (await fetch(`${apiUrl}/lista_compra/${listaId}`)).json();
        listaSelecionada = lista;
        document.getElementById('editar-nome-lista').value = lista.nome;
        document.getElementById('editar-data-lista').value = lista.dataCompra || '';
        abrirModal('modal-editar-lista');
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao carregar dados da lista');
    }
}

// Salvar Alterações do Nome da Lista
async function salvarEditarLista() {
    const novoNome = document.getElementById('editar-nome-lista').value.trim();
    
    if (!novoNome) {
        alert('Digite um nome para a lista!');
        return;
    }

    try {
        const novaDataCompra = document.getElementById('editar-data-lista').value || null;
        await fetch(`${apiUrl}/lista_compra/${listaSelecionada.id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ nome: novoNome, dataCompra: novaDataCompra })
        });

        carregarDados();
        carregarListasNoSelect();
        fecharModal('modal-editar-lista');
        alert('Nome da lista atualizado!');
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao editar lista');
    }
}

// Abrir Modal para Excluir Lista
async function abrirModalExcluirLista(listaId) {
    try {
        const lista = await (await fetch(`${apiUrl}/lista_compra/${listaId}`)).json();
        listaSelecionada = lista;
        document.getElementById('excluir-lista-nome').textContent = lista.nome;
        
        const itens = await (await fetch(`${apiUrl}/itens_lista_compra`)).json();
        const itensLista = itens.filter(item => item.idLista === lista.id).length;
        document.getElementById('excluir-lista-itens').textContent = itensLista;
        
        abrirModal('modal-excluir-lista');
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao carregar dados da lista');
    }
}

// Confirmar Exclusão da Lista
async function confirmarExclusaoLista() {
    try {
        await fetch(`${apiUrl}/lista_compra/${listaSelecionada.id}`, {
            method: 'DELETE'
        });

        carregarDados();
        carregarListasNoSelect();
        fecharModal('modal-excluir-lista');
        alert('Lista excluída com sucesso!');
    } catch (error) {
        console.error('Erro:', error);
        alert('Erro ao excluir lista');
    }
}

// Filtro de Pesquisa
document.getElementById('texto-pesquisa').addEventListener('input', function () {
    const termo = this.value.trim().toLowerCase();

    const listas = document.querySelectorAll('.lista-compra');

    listas.forEach(lista => {
        const tituloLista = lista.querySelector('h2').textContent.toLowerCase();
        const itens = lista.querySelectorAll('.item-lista');

        let listaVisivel = false;

        itens.forEach(item => {
            const nomeProduto = item.querySelector('.item-info h3')?.textContent.toLowerCase() || '';

            const corresponde = nomeProduto.includes(termo) || tituloLista.includes(termo);

            if (corresponde) {
                item.style.display = 'flex';
                listaVisivel = true;
            } else {
                item.style.display = 'none';
            }
        });

        lista.style.display = listaVisivel ? 'block' : 'none';
    });
});

// Inicialização
window.onload = () => {
    carregarDados();
    carregarListasNoSelect();
};