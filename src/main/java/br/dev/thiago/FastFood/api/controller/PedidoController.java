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
    PedidoService pedidoService;

    @Autowired
    ProdutoService produtoService;

    @GetMapping("{id}")
    public ResponseEntity<Pedido> findById(@PathVariable Long id) {
        if (pedidoService.existsById(id)) {
            return ResponseEntity.ok(pedidoService.findById(id).get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Pedido>> listarPedidos() {
        if (pedidoService.listarPedidos().isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(pedidoService.listarPedidos());
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Pedido>> listarPorStatus(@PathVariable Pedido.Status status) {

        if (pedidoService.listarPorStatus(status).isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(pedidoService.listarPorStatus(status));
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido adicionarPedido(@Valid @RequestBody PedidoRequest pedidoRequest) {

        List<Long> ids = pedidoRequest.getProdutoId();

        List<Long> idsNaoEncontrados = ids.stream()
                .filter(id -> !produtoService.existsById(id))
                .toList();

        if (!idsNaoEncontrados.isEmpty()) {
            throw new NoSuchElementException("Produtos não encontrados: " + idsNaoEncontrados);
        }

        Pedido pedido = new Pedido();

        pedido.setDataAbertura(LocalDateTime.now());
        pedido.setStatus(Pedido.Status.ABERTO);
        pedido.setPrazo(pedido.getDataAbertura().plusMinutes(14));
        pedido.setTelefone(pedidoRequest.getTelefone());

        List<Produto> produtos = ids.stream()
                .map(id -> produtoService.findById(id).get())
                .toList();

        BigDecimal valorTotal = produtos.stream()
                .map(Produto::getPreco)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        pedido.setProdutos(produtos);
        pedido.setValorTotal(valorTotal);

        return pedidoService.salvarPedido(pedido);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarPedido(@PathVariable Long id) {
        if (pedidoService.existsById(id)) {
            pedidoService.deletarPedido(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<Pedido> updatePedido(@PathVariable Long id,
            @Valid @RequestBody Pedido pedido) {

        if (pedidoService.existsById(id)) {
            pedido.setId(id);
            pedido = pedidoService.salvarPedido(pedido);
            return ResponseEntity.ok(pedido);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/status/{id}")
    public ResponseEntity<Pedido> alterarStatus(@PathVariable Long id,
            @RequestBody StatusRequest statusRequest) {

        Pedido pedido = pedidoService.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pedido com ID " + id + " não encontrado"));
        
        if (statusRequest.getStatus().equals(Pedido.Status.ENTREGUE)) { pedido.setDataFinalizacao(LocalDateTime.now()); }

        pedido = pedidoService.alterarStatus(pedido, statusRequest.getStatus());

        return ResponseEntity.ok(pedido);
    }
}