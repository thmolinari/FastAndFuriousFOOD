package br.dev.thiago.FastFood.domain.service;

import br.dev.thiago.FastFood.domain.model.Produto;
import br.dev.thiago.FastFood.domain.repository.ProdutoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProdutoService {

    @Autowired
    ProdutoRepository produtoRepo;

    public boolean existsById(Long id) {
        return produtoRepo.existsById(id);
    }

    public Optional<Produto> findById(Long id) {
        return produtoRepo.findById(id);
    }

    public List<Produto> listarProdutos() {
        return (List<Produto>) produtoRepo.findAll();
    }

    public List<Produto> listByCategoria(String categoria) {
        return produtoRepo.findByCategoria(categoria);
    }

    public Produto salvarProduto(Produto produto) {
        return produtoRepo.save(produto);
    }

    public void deletarProduto(Long id) {
        produtoRepo.deleteById(id);
    }

}