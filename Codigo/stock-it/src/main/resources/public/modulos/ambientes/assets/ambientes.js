const apiUrl = '';

//Vars globais
//Pega o ID do ambiente da URL 
const params = new URLSearchParams(window.location.search);
const ambienteId = parseInt(params.get('id'));
const campoBusca = document.getElementById('campo-busca');
let linhasTabela = [];

async function removerVencidos() {
    try {
        const response = await fetch(`${apiUrl}/ambientes/${ambienteId}`);
        const ambiente = await response.json();

        let algumRemovido = false;

        for (let i = ambiente.itens.length - 1; i >= 0; i--) {
            const item = ambiente.itens[i];
            if (!conferirValidade(formatarData(item.vencimento))) {
                await deletarAlimento(i, false);
                algumRemovido = true;
            }
        }

        if (algumRemovido) {
            Swal.fire({
                title: "Tudo Certo!",
                text: "Alimentos vencidos foram removidos com sucesso!",
                icon: "success"
            });
            carregarAlimentos();
        } else {
            Swal.fire({
                icon: "error",
                title: "Oops...",
                text: "Nenhum Alimento Vencido Encontrado!",
            });
        }

    } catch (error) {
        Swal.fire({
            icon: "error",
            title: "Oops...",
            text: "Erro ao remover alimentos vencidos!",
        });
    }
}

// Função para carregar o nome do ambiente
async function carregarAmbiente() {
    try {
        const response = await fetch(`${apiUrl}/ambientes/${ambienteId}`);
        const ambiente = await response.json();
        document.getElementById('nome-ambiente').textContent = ambiente.nome;
        document.title = `StockIT - ${ambiente.nome}`;
        const tipoAmbiente = ambiente.tipo;
    } catch (error) {
    }
}

// Função para carregar os alimentos do ambiente
async function carregarAlimentos() {
    try {
        const response = await fetch(`${apiUrl}/ambientes/${ambienteId}`);
        const ambiente = await response.json();
        const corpoTabela = document.getElementById('corpo-tabela-alimentos');
        corpoTabela.innerHTML = '';
        linhasTabela = [];

        ambiente.itens.forEach(async (item, index) => {
            const alimentoResponse = await fetch(`${apiUrl}/alimentos/${item.alimentoId}`);
            const alimento = await alimentoResponse.json();
            const validade = conferirValidade(formatarData(item.vencimento)) == true ? "" : "fa-solid fa-triangle-exclamation";
            const linha = document.createElement('tr');
            linha.innerHTML =
                `<td id="td-nome">${alimento.nome} <i  id = icone-vencimento class ="${validade}"><\i> </td>
                <td>${formatarData(item.cadastro)}</td>
                <td>${formatarData(item.vencimento)}</td>
                <td>${item.quantidade} </td>
                <td>
                    <button class="botao-secundario" onclick="openModal('modal-editar','form-editar', ${index} , ${ambienteId})">Editar</button>
                    <button class="botao-perigo" onclick = "openModal('modal-excluir', '',${index}, ${ambienteId})">Excluir</button>
                </td>
            `;
            if (validade) {
                const nome = linha.querySelector('td');
                nome.classList.add("alimento-vencido");
            }
            corpoTabela.appendChild(linha);
            linhasTabela.push(linha);

        });
    } catch (error) {
    }
}

//Função para conferir validade(false se vencido)
function conferirValidade(data) {
    const [dia, mes, ano] = data.split('/').map(Number);
    const dataValidade = new Date(ano, mes - 1, dia);
    const hoje = new Date();
    hoje.setHours(0, 0, 0, 0);
    return dataValidade >= hoje;
}

// Formata Data para o Padrão Brasileiro
function formatarData(dataIso) {
    if (!dataIso) return '–';
    const [ano, mes, dia] = dataIso.split('-');
    return `${dia}/${mes}/${ano}`;
}

//Função que normaliza texto, sem acento, espaços extras e maiuculas
function normalizarTexto(texto) {
    return texto
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLowerCase()
        .trim();
}

//Função para Filtrar busca a apartir de 3 letras com texto normalizado
function filtroBusca() {
    const textoBusca = normalizarTexto(campoBusca.value);
    const aviso = document.getElementById('mensagem-nenhum');
    let algumVisivel = false;

    if (textoBusca.length < 2) {
        linhasTabela.forEach(linha => linha.style.display = '');
        if (aviso) aviso.remove();
        return;
    }

    linhasTabela.forEach(linha => {
        const colunas = linha.querySelectorAll('td');
        const nomeCompleto = colunas[0]?.textContent || '';
        const [nome, tipo = ''] = normalizarTexto(nomeCompleto).split(' ');

        const visivel =
            nome.includes(textoBusca) ||
            tipo.includes(textoBusca);

        linha.style.display = visivel ? '' : 'none';
        if (visivel) algumVisivel = true;
    });


    if (!algumVisivel) {
        if (!aviso) {
            const msg = document.createElement('tr');
            msg.id = 'mensagem-nenhum';
            msg.innerHTML = `<td colspan="4" style="text-align:center; color:#999">Nenhum alimento encontrado 😔</td>`;
            document.getElementById('corpo-tabela-alimentos').appendChild(msg);
        }
    } else {
        if (aviso) aviso.remove();
    }
}

//Funções para Ordenação por Nome
let ordemNomeAsc = true;

function ordenarPorNome() {
    const corpo = document.getElementById('corpo-tabela-alimentos');
    const seta = document.getElementById('seta-atributo-nome')

    linhasTabela.sort((a, b) => {
        const nomeA = normalizarTexto(a.cells[0].textContent.split(' ')[0]);
        const nomeB = normalizarTexto(b.cells[0].textContent.split(' ')[0]);
        return nomeA.localeCompare(nomeB) * (ordemNomeAsc ? 1 : -1);
    });

    corpo.innerHTML = '';
    linhasTabela.forEach(linha => corpo.appendChild(linha));

    // Atualiza a seta
    seta.className = ordemNomeAsc ? 'fa-solid fa-caret-up' : 'fa-solid fa-caret-down';

    ordemNomeAsc = !ordemNomeAsc;
}

//Listener para chamada de ordenar nome
document.getElementById('atributo-nome').addEventListener('click', ordenarPorNome);

//Listener para input de busca com atraso para melhor UX
let temporizador;
campoBusca.addEventListener('input', () => {
    clearTimeout(temporizador);
    temporizador = setTimeout(filtroBusca, 300);
});

//Listener para os modais
function openModal(id, form_nome, index) {
    const modalElement = document.getElementById(id);
    const modal = new bootstrap.Modal(modalElement);
    modal.show();

    // Limpar o form quando o modal é fechado
    modalElement.addEventListener('hidden.bs.modal', () => {
        const form = document.getElementById(form_nome);
        if (form) form.reset();
    }, { once: true }); // garante que o listener será adicionado uma única vez

    const ambienteId = parseInt(new URLSearchParams(window.location.search).get('id'));

    // Cadastrar
    const btnCadastrar = document.getElementById('btn-adicionar-alimento');
    btnCadastrar.onclick = () => cadastrarAlimento();

    // Editar
    const btnEditar = document.getElementById('btn-editar-salvar');
    btnEditar.onclick = () => editarAlimento(index);

    // Excluir
    const btnExcluir = document.getElementById('btn-confirmar-exclusao');
    btnExcluir.onclick = () => deletarAlimento(index, true, true);



}

// Carrega as categorias de alimentos e preenche o <select> do modal de cadastro
async function carregarTiposModal() {
    const select = document.getElementById('select-cadastro');

    if (!select) {
        return;
    }

    try {
        const response = await fetch(`${apiUrl}/categorias_alimentos`);
        const categorias = await response.json();

        select.innerHTML = '<option selected disabled>Escolha uma categoria</option>';

        categorias.forEach((categoria) => {
            const option = document.createElement('option');
            option.value = categoria.id;
            option.textContent = categoria.nomeCategoria;
            select.appendChild(option);
        });

    } catch (error) {
    }
}

// Obtém e valida os dados do formulário de cadastro de alimento
function obterDadosCadastro() {
    const nome = document.getElementById('cadastro-nome').value.trim();
    const vencimento = document.getElementById('cadastro-validade').value;
    const quantidade = parseInt(document.getElementById('cadastro-quantidade').value);
    const idCategoria = parseInt(document.getElementById('select-cadastro').value);
    const cadastro = new Date().toISOString().split('T')[0];

    if (!nome || !vencimento || !quantidade || isNaN(idCategoria)) {
        alert("Preencha todos os campos obrigatórios.");
        return null;
    }

    return {
        nome,
        idCategoria,
        quantidade,
        vencimento,
        cadastro
    };
}

// Verifica se o alimento já existe no JSON; se não, cria um novo e retorna o ID
async function verificarOuCriarAlimento({ nome, idCategoria}) {
    try {
        const response = await fetch(`${apiUrl}/alimentos`);
        const alimentos = await response.json();

        // Normaliza para comparação 
        const nomeNormalizado = nome.trim().toLowerCase();
        //const tipoNormalizado = tipo.trim().toLowerCase();

        const alimentoExistente = alimentos.find(a => {
            const aNome = (a.nome || "").trim().toLowerCase();
            //const aTipo = (a.tipo || "").trim().toLowerCase();
            return aNome === nomeNormalizado //&& aTipo === tipoNormalizado;
        });

        if (alimentoExistente) {
            return alimentoExistente.id; // Retorna o ID se já existir
        } else {
            //return await criarAlimento(nome, tipo, categoria, imagem);
            return await criarAlimento(nome, idCategoria);
        }

    } catch (error) {
        throw error;
    }
}

// Cria um novo alimento no JSON e retorna o ID gerado
//async function criarAlimento(nome, tipo, categoria, imagem) {
async function criarAlimento(nome, idCategoria) {
    try {
        const response = await fetch(`${apiUrl}/alimentos`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            //body: JSON.stringify({ nome, tipo, imagem, categoria })
            body: JSON.stringify({ nome, idCategoria })
        });

        const novoAlimento = await response.json();
        return novoAlimento.id;
    } catch (error) {
    }
}

function formatarDataParaExtenso(dataISO) {
    const meses = [
        "jan.", "fev.", "mar.", "abr.", "mai.", "jun.",
        "jul.", "ago.", "set.", "out.", "nov.", "dez."
    ];

    const [ano, mes, dia] = dataISO.split("-");
    
    return `${meses[parseInt(mes)-1]} ${parseInt(dia)}, ${ano}`;
}


async function cadastrarAlimento() {
    const dados = obterDadosCadastro();
    if (!dados) return; // validar retorno nulo
    const { nome, idCategoria, quantidade, vencimento, cadastro } = dados;

    try {
        // Verifica ou cria o alimento e pega o ID
        const id = await verificarOuCriarAlimento({ nome, idCategoria });

        // Busca o ambiente atual
        const respAmbiente = await fetch(`${apiUrl}/ambientes/${ambienteId}`);
        if (!respAmbiente.ok) throw new Error('Erro ao carregar ambiente');
        const ambiente = await respAmbiente.json();

        // Monta o item com os campos esperados pelo resto do app (ISO strings para datas)
        const novoItem = {
            alimentoId: id,
            quantidade: quantidade,
            cadastro: cadastro,     // ISO: YYYY-MM-DD (obterDadosCadastro já fornece)
            vencimento: vencimento, // ISO: YYYY-MM-DD
            // outros campos opcionais podem ser adicionados conforme backend
        };

        // Adiciona ao array local e envia PATCH para atualizar o ambiente
        ambiente.itens = Array.isArray(ambiente.itens) ? ambiente.itens : [];
        ambiente.itens.push(novoItem);

        const patchRes = await fetch(`${apiUrl}/ambientes/${ambienteId}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ itens: ambiente.itens })
        });

        if (!patchRes.ok) {
            const txt = await patchRes.text().catch(() => '');
            throw new Error(`Erro ao cadastrar item: ${patchRes.status} ${txt}`);
        }

        Swal.fire({
            icon: "success",
            title: "Tudo Certo!",
            text: "Alimento Cadastrado com Sucesso!",
        });

        carregarAlimentos(); // Atualiza a lista de itens na tela

    } catch (error) {
        console.error("Erro ao cadastrar alimento:", error);
        Swal.fire({
            icon: "error",
            title: "Oops...",
            text: "Erro ao cadastrar alimento: " + (error.message || ''),
        });
    }
}


// Edita a quantidade e validade de um alimento em um ambiente
async function editarAlimento(index) {
    try {
        const response = await fetch(`${apiUrl}/itens_ambiente`);
        const itens = await response.json();

        const novaQuantidade = parseInt(document.getElementById('editar-quantidade').value);
        let novaValidade = document.getElementById('editar-validade').value;

        //Se campo quantidade não for preenchido
        if (!novaQuantidade) {
            alert("Preencha o campo de quantidade!");
            return;
        }

        // Encontra o item correto pelo índice
        let itemAtual = null;
        let itemIdParaEditar = null;
        let contador = 0;

        // Busca o ambiente para filtrar itens
        const ambienteResponse = await fetch(`${apiUrl}/ambientes/${ambienteId}`);
        const ambiente = await ambienteResponse.json();

        // Pega todos os itens deste ambiente
        for (let i = 0; i < itens.length; i++) {
            if (itens[i].idAmbiente === ambienteId) {
                if (contador === index) {
                    itemIdParaEditar = itens[i].idItemAmbiente;
                    itemAtual = itens[i];
                    break;
                }
                contador++;
            }
        }

        if (!itemAtual) {
            alert("Item não encontrado!");
            return;
        }

        // Se não mudar a validade, manter a original
        if (!novaValidade) {
            novaValidade = itemAtual.dataVencimento;
        } else {
            novaValidade = formatarDataParaExtenso(novaValidade);
        }

        // Atualiza apenas este item
        const itemAtualizado = {
            idItemAmbiente: itemAtual.idItemAmbiente,
            quantidade: novaQuantidade,
            dataCadastro: itemAtual.dataCadastro,
            dataVencimento: novaValidade,
            idAlimento: itemAtual.idAlimento,
            idAmbiente: itemAtual.idAmbiente
        };

        // Envia o PUT para atualizar o item específico
        const atualizar = await fetch(`${apiUrl}/itens_ambiente/${itemIdParaEditar}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(itemAtualizado)
        });

        if (!atualizar.ok) {
            throw new Error("Erro ao atualizar item");
        }

        carregarAlimentos();
        Swal.fire({
            icon: "success",
            title: "Tudo Certo!",
            text: "Alimento Atualizado com Sucesso!",
        });

    } catch (error) {
        console.error("Erro ao editar alimento:", error);
        Swal.fire({
            icon: "error",
            title: "Oops...",
            text: "Erro ao editar alimento: " + error.message,
        });
    }
}



// Deleta um alimento do ambiente pelo índice,
async function deletarAlimento(index, carregar, deletar) {
    try {
        const response = await fetch(`${apiUrl}/ambientes/${ambienteId}`);
        const ambiente = await response.json();

        ambiente.itens.splice(index, 1);
        await fetch(`${apiUrl}/ambientes/${ambienteId}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ itens: ambiente.itens })
        });

        if (carregar) {
            carregarAlimentos();
        }
        if (deletar) {
            Swal.fire({
                icon: "success",
                title: "Tudo Certo!",
                text: "Alimento foi removido!",
            });
        }

    } catch (error) {
        console.error("Erro ao deletar alimento:", error);
        Swal.fire({
            icon: "error",
            title: "Oops...",
            text: "Erro ao deletar alimento!",
        });
    }
}

// Preenche o select com os ambientes disponíveis, exceto o atual, para mover alimentos
async function preencherSelectMover() {
    const response = await fetch(`${apiUrl}/ambientes`)
    const ambientes = await response.json();
    const selectMover = document.getElementById("select-mover-alimentos");
    let i = 1;
    ambientes.forEach(ambiente => {
        if (i != ambienteId) {
            selectMover.innerHTML += `
            <option value="${i}">${ambiente.nome}</option>
            `
        }
        i++;
    });
}

// Preenche os checkboxes com os alimentos do ambiente atual, para selecionar os que serão movidos
async function preencherCheckMover() {
    const response = await fetch(`${apiUrl}/ambientes/${ambienteId}`)
    const ambienteAtual = await response.json();

    let alimentos = await fetch(`${apiUrl}/alimentos`)
    alimentos = await alimentos.json();

    const checkMover = document.getElementById("check-mover-alimentos");
    checkMover.innerHTML = ` `
    let i = 0;
    ambienteAtual.itens.forEach((item, index) => {
        let alimento = alimentos.find(alimento => alimento.id == item.alimentoId);
        checkMover.innerHTML += `
        <div class="form-check">
        <input class="form-check-input" id="mover-check${i}" type="checkbox"  value="${i}" >
        <label class="form-check-label" for=mover-check${i++}">${alimento.nome}</label>
        </div>
        `
    });
}

async function moverAlimentos() {
    const selecionados = Array.from(
        document.querySelectorAll('#check-mover-alimentos input[type="checkbox"]:checked')
    ).map(checkbox => parseInt(checkbox.value));

    const novoAmbienteId = parseInt(document.getElementById('select-mover-alimentos').value);

    try {
        const responseAtual = await fetch(`${apiUrl}/ambientes/${ambienteId}`);
        const ambienteAtual = await responseAtual.json();

        const responseNovo = await fetch(`${apiUrl}/ambientes/${novoAmbienteId}`);
        const novoAmbiente = await responseNovo.json();

        const itensSelecionados = selecionados.map(index => ambienteAtual.itens[index]);

        novoAmbiente.itens.push(...itensSelecionados);

        selecionados.sort((a, b) => b - a).forEach(index => {
            ambienteAtual.itens.splice(index, 1);
        });

        await fetch(`${apiUrl}/ambientes/${ambienteId}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ itens: ambienteAtual.itens })
        });

        await fetch(`${apiUrl}/ambientes/${novoAmbienteId}`, {
            method: 'PATCH',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ itens: novoAmbiente.itens })
        });

        Swal.fire({
                title: "Tudo Certo!",
                text: "Alimentos foram movidos com sucesso!",
                icon: "success"
            });
        carregarAlimentos();

    } catch (error) {
                Swal.fire({
                title: "Opss..!",
                text: "Erro ao mover alimentos!",
                icon: "error"
            });
    }
}

//Listener para Página Carregada
document.addEventListener('DOMContentLoaded', async () => {
    await carregarAmbiente();
    await carregarAlimentos();
    await ordenarPorNome();
    carregarTiposModal();

});
