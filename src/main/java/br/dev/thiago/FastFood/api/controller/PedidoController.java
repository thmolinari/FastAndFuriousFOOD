package br.dev.thiago.FastFood.api.controller;

import br.dev.thiago.FastFood.domain.DTO.PedidoRequest;
import br.dev.thiago.FastFood.domain.DTO.StatusRequest;
import br.dev.thiago.FastFood.domain.model.Pedido;
import br.dev.thiago.FastFood.domain.model.Produto;
import br.dev.thiago.FastFood.domain.service.PedidoService;
import br.dev.thiago.FastFood.domain.service.ProdutoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/pedido")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ProdutoService produtoService;

    @GetMapping("{id}")
    public ResponseEntity<Pedido> buscarPedidoPorId(@PathVariable Long id) {
        return pedidoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarTodosPedidos() {
        List<Pedido> pedidos = pedidoService.listarPedidos();
        return pedidos.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(pedidos);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Pedido>> listarPedidosPorStatus(@PathVariable Pedido.Status status) {
        List<Pedido> pedidos = pedidoService.listarPorStatus(status);
        return pedidos.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(pedidos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido criarPedido(@Valid @RequestBody PedidoRequest pedidoRequest) {
        List<Long> idsProdutos = pedidoRequest.getProdutoId();

        List<Long> idsInvalidos = idsProdutos.stream()
                .filter(id -> !produtoService.existsById(id))
                .toList();

        if (!idsInvalidos.isEmpty()) {
            throw new NoSuchElementException("Produtos não encontrados: " + idsInvalidos);
        }

        List<Produto> produtos = idsProdutos.stream()
                .map(id -> produtoService.findById(id).get())
                .toList();

        BigDecimal total = produtos.stream()
                .map(Produto::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Pedido novoPedido = new Pedido();
        novoPedido.setDataAbertura(LocalDateTime.now());
        novoPedido.setPrazo(novoPedido.getDataAbertura().plusMinutes(14));
        novoPedido.setTelefone(pedidoRequest.getTelefone());
        novoPedido.setStatus(Pedido.Status.ABERTO);
        novoPedido.setProdutos(produtos);
        novoPedido.setValorTotal(total);

        return pedidoService.salvarPedido(novoPedido);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> removerPedido(@PathVariable Long id) {
        if (!pedidoService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoService.deletarPedido(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<Pedido> atualizarPedido(@PathVariable Long id, @Valid @RequestBody Pedido pedido) {
        if (!pedidoService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedido.setId(id);
        return ResponseEntity.ok(pedidoService.salvarPedido(pedido));
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<Pedido> atualizarStatusPedido(@PathVariable Long id, @RequestBody StatusRequest statusRequest) {
        Pedido pedido = pedidoService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido com ID " + id + " não encontrado"));

        if (statusRequest.getStatus() == Pedido.Status.ENTREGUE) {
            pedido.setDataFinalizacao(LocalDateTime.now());
        }

        return ResponseEntity.ok(pedidoService.alterarStatus(pedido, statusRequest.getStatus()));
    }
}
