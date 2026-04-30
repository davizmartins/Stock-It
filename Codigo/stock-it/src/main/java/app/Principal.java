package app;

import static spark.Spark.*;
import service.ListaDeCompraService;
import service.ItemListaCompraService;
import service.ItemAmbienteService;
import dao.ListaDeCompraDAO;
import dao.ItemListaCompraDAO;
import dao.ItemAmbienteDAO;
import service.AmbienteService;
import dao.AmbienteDAO;
import dao.AlimentoDAO;
import service.AlimentoService;
import dao.CategoriaAlimentoDAO;
import service.CategoriaAlimentoService;
import service.NotaFiscalOcrService;
import service.AzureOcrService;
import dao.UsuarioDAO;
import service.UsuarioService;
import model.Usuario;
import com.google.gson.Gson;

public class Principal {

    private static ListaDeCompraService listaService;
    private static ItemListaCompraService itemListaService;
    private static ItemAmbienteService itemAmbienteService;
    private static AmbienteService ambienteService;
    private static AlimentoService alimentoService;
    private static CategoriaAlimentoService categoriaService;
    private static NotaFiscalOcrService notaFiscalOcrService;
    private static UsuarioService usuarioService;
    private static Gson gson = new Gson();

    public static void main(String[] args) {
        
        String portStr = System.getenv("PORT");
        int porta = 4567; // default para rodar local

        if (portStr != null) {
            try {
                porta = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                System.out.println("PORT inválida (" + portStr + "), usando 4567.");
            }
        }

        port(porta);

        staticFiles.location("/public");

        // Instancia DAOs e Services
        ListaDeCompraDAO listaDAO = new ListaDeCompraDAO();
        ItemListaCompraDAO itemDAO = new ItemListaCompraDAO();
        ItemAmbienteDAO itemAmbienteDAO = new ItemAmbienteDAO();
        AmbienteDAO ambienteDAO = new AmbienteDAO();
        AlimentoDAO alimentoDAO = new AlimentoDAO();
        CategoriaAlimentoDAO categoriaAlimentoDAO = new CategoriaAlimentoDAO();

        ambienteDAO.conectar();
        listaDAO.conectar();
        itemDAO.conectar();
        itemAmbienteDAO.conectar();
        alimentoDAO.conectar();
        categoriaAlimentoDAO.conectar();

        ambienteService = new AmbienteService(ambienteDAO, itemAmbienteDAO);
        listaService = new ListaDeCompraService(listaDAO, itemDAO);
        itemListaService = new ItemListaCompraService(itemDAO);
        itemAmbienteService = new ItemAmbienteService(itemAmbienteDAO);
        alimentoService = new AlimentoService(alimentoDAO);
        categoriaService = new CategoriaAlimentoService(categoriaAlimentoDAO);

        // === Serviço de OCR (Azure) ===
        AzureOcrService azureOcrService = new AzureOcrService();
        notaFiscalOcrService = new NotaFiscalOcrService(azureOcrService, alimentoDAO, itemAmbienteDAO);

        // instancia Usuario DAO / Service
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        usuarioService = new UsuarioService(usuarioDAO);

        // -------- Rotas principais --------
        get("/", (req, res) -> {
            res.redirect("/listas");
            return null;
        });

        // --- Lista de Compra ---
        get("/lista_compra", (req, res) -> listaService.getAll(req, res));
        get("/lista_compra/:id", (req, res) -> listaService.get(req, res));
        get("/lista_compra/:id/itens", (req, res) -> listaService.getWithItems(req, res));
        post("/lista_compra", (req, res) -> listaService.add(req, res));
        put("/lista_compra/:id", (req, res) -> listaService.update(req, res));
        delete("/lista_compra/:id", (req, res) -> listaService.remove(req, res));

        // --- Itens ---
        get("/itens_lista_compra", (req, res) -> itemListaService.getAll(req, res));
        get("/itens_lista_compra/:id", (req, res) -> itemListaService.get(req, res));
        get("/itens_lista_compra/lista/:idLista", (req, res) -> itemListaService.getByLista(req, res));
        post("/itens_lista_compra", (req, res) -> itemListaService.add(req, res));
        put("/itens_lista_compra/:id", (req, res) -> itemListaService.update(req, res));
        delete("/itens_lista_compra/:id", (req, res) -> itemListaService.remove(req, res));
        patch("/itens_lista_compra/:id/status", (req, res) -> itemListaService.updateStatus(req, res));

        // --- Itens de ambiente ---
        get("/itens_ambiente", (req, res) -> itemAmbienteService.getAll(req, res));
        get("/itens_ambiente/:id", (req, res) -> itemAmbienteService.get(req, res));
        post("/itens_ambiente", (req, res) -> itemAmbienteService.add(req, res));
        put("/itens_ambiente/:id", (req, res) -> itemAmbienteService.update(req, res));
        delete("/itens_ambiente/:id", (req, res) -> itemAmbienteService.remove(req, res));

        // --- Ambientes ---
        get("/ambientes", (req, res) -> ambienteService.getAll(req, res));
        get("/ambientes/:id", (req, res) -> ambienteService.get(req, res));
        post("/ambientes", (req, res) -> ambienteService.add(req, res));
        put("/ambientes/:id", (req, res) -> ambienteService.update(req, res));
        patch("/ambientes/:id", (req, res) -> ambienteService.patchItems(req, res));
        delete("/ambientes/:id", (req, res) -> ambienteService.remove(req, res));

        // --- Alimentos ---
        get("/alimentos", (req, res) -> alimentoService.getAll(req, res));
        get("/alimentos/:id", (req, res) -> alimentoService.get(req, res));
        post("/alimentos", (req, res) -> alimentoService.add(req, res));
        put("/alimentos/:id", (req, res) -> alimentoService.update(req, res));
        delete("/alimentos/:id", (req, res) -> alimentoService.remove(req, res));

        // --- Categorias de Alimentos ---
        get("/categorias_alimentos", (req, res) -> categoriaService.getAll(req, res));
        get("/categorias_alimentos/:id", (req, res) -> categoriaService.get(req, res));
        post("/categorias_alimentos", (req, res) -> categoriaService.add(req, res));
        put("/categorias_alimentos/:id", (req, res) -> categoriaService.update(req, res));
        delete("/categorias_alimentos/:id", (req, res) -> categoriaService.remove(req, res));

        // ---Ocr da Azure ---
        post("/notas/ocr", (req, res) -> notaFiscalOcrService.processarUploadNota(req, res));
        post("/notas/importar", (req, res) -> notaFiscalOcrService.importarItensParaAmbiente(req, res));

        // --- Usuários ---
        post("/usuarios", (req, res) -> usuarioService.add(req, res)); 
        post("/login", (req, res) -> usuarioService.login(req, res));
        put("/usuarios/:id/senha", (req, res) -> usuarioService.updatePassword(req, res)); 
        delete("/usuarios/:id", (req, res) -> usuarioService.remove(req, res)); 
    }
}