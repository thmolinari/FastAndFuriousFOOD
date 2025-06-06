package br.dev.thiago.FastFood.api.controller;

import br.dev.thiago.FastFood.domain.model.Produto;
import br.dev.thiago.FastFood.domain.service.ProdutoService;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    ProdutoService produtoService;

    @GetMapping("{id}")
    public ResponseEntity<Produto> findById(@PathVariable Long id) {
        if (produtoService.findById(id).isPresent()) {
            return ResponseEntity.ok(produtoService.findById(id).get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Produto>> listarProdutos() {
        if (produtoService.listarProdutos().isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(produtoService.listarProdutos());
        }
    }
    
    @GetMapping("/cat/{categoria}")
    public ResponseEntity<List<Produto>> findByCategoria(String categoria) {
        if (produtoService.listByCategoria(categoria).isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(produtoService.listByCategoria(categoria));
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto adicionarProduto(@Valid
            @RequestBody Produto produto) {

        return produtoService.salvarProduto(produto);
    }

    @PutMapping("{id}")
    public ResponseEntity<Produto> updateProduto(@PathVariable Long id,
                                        @Valid @RequestBody Produto produto) {

        if (produtoService.existsById(id)) {
            produto.setId(id);
            produto = produtoService.salvarProduto(produto);
            return ResponseEntity.ok(produto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        if (produtoService.existsById(id)) {
            produtoService.deletarProduto(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}