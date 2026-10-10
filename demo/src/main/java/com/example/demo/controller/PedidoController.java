package com.example.demo.controller;

import com.example.demo.entity.ItemPedido;
import com.example.demo.entity.Pedido;
import com.example.demo.repository.ClienteRepository;
import com.example.demo.repository.FuncionarioRepository;
import com.example.demo.repository.PedidoRepository;
import com.example.demo.repository.ProdutoRepository;
import com.example.demo.repository.ServicoRepository;
import com.example.demo.service.PedidoService;
import java.time.LocalDateTime;
import java.util.Locale;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ProdutoRepository produtoRepository;
    private final ServicoRepository servicoRepository;
    private final PedidoService pedidoService;

    public PedidoController(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            FuncionarioRepository funcionarioRepository,
            ProdutoRepository produtoRepository,
            ServicoRepository servicoRepository,
            PedidoService pedidoService) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.produtoRepository = produtoRepository;
        this.servicoRepository = servicoRepository;
        this.pedidoService = pedidoService;
    }

    @GetMapping
    public String listar(Model model) {
        prepararModelo(model, new PedidoForm());
        return "pedidos";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute("pedidoForm") PedidoForm pedidoForm, Model model) {
        try {
            if (pedidoForm.getClienteId() == null) {
                throw new IllegalArgumentException("Selecione um cliente.");
            }
            if (pedidoForm.getResponsavelId() == null) {
                throw new IllegalArgumentException("Selecione o funcionário responsável.");
            }

            Pedido pedido = new Pedido();
            pedido.setNumero(pedidoForm.getNumero());
            pedido.setCliente(clienteRepository.findById(pedidoForm.getClienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado.")));
            pedido.setResponsavel(funcionarioRepository.findById(pedidoForm.getResponsavelId())
                    .orElseThrow(() -> new IllegalArgumentException("Funcionário não encontrado.")));
            pedido.setData(LocalDateTime.now());
            pedido.setStatus("ABERTO");

            if (pedidoForm.getItens() != null) {
                for (PedidoForm.ItemForm itemForm : pedidoForm.getItens()) {
                    if (itemForm == null) {
                        throw new IllegalArgumentException("Os dados do item são inválidos.");
                    }

                    if (itemForm.getTipo() == null
                            && itemForm.getReferenciaId() == null
                            && itemForm.getQuantidade() == null) {
                        continue;
                    }

                    if (itemForm.getTipo() == null || itemForm.getReferenciaId() == null) {
                        throw new IllegalArgumentException(
                                "Selecione um produto ou serviço para cada item."
                        );
                    }

                    ItemPedido item = new ItemPedido();
                    item.setQuantidade(itemForm.getQuantidade());

                    switch (itemForm.getTipo().trim().toUpperCase(Locale.ROOT)) {
                        case "PRODUTO" -> {
                            var produto = produtoRepository.findById(itemForm.getReferenciaId())
                                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
                            item.setProduto(produto);
                            item.setPrecoUnitario(produto.getPreco());
                        }
                        case "SERVICO" -> {
                            var servico = servicoRepository.findById(itemForm.getReferenciaId())
                                    .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado."));
                            item.setServico(servico);
                            item.setPrecoUnitario(servico.getPreco());
                        }
                        default -> throw new IllegalArgumentException(
                                "O tipo do item deve ser produto ou serviço."
                        );
                    }

                    pedido.adicionarItem(item);
                }
            }

            pedidoService.criarPedido(pedido);
            return "redirect:/pedidos";
        } catch (IllegalArgumentException exception) {
            prepararModelo(model, pedidoForm);
            model.addAttribute("erro", exception.getMessage());
            return "pedidos";
        }
    }

    @PostMapping("/{id}/entregar")
    public String entregar(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            pedidoService.entregarPedido(id);
            redirectAttributes.addFlashAttribute(
                    "sucesso", "Pedido entregue com sucesso.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute(
                    "erro", exception.getMessage());
        }

        return "redirect:/pedidos";
    }

    private void prepararModelo(Model model, PedidoForm pedidoForm) {
        model.addAttribute("pedidoForm", pedidoForm);
        model.addAttribute("pedidos", pedidoRepository.findAll());
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("funcionarios", funcionarioRepository.findAll());
        model.addAttribute("produtos", produtoRepository.findAll());
        model.addAttribute("servicos", servicoRepository.findAll());
    }
}
