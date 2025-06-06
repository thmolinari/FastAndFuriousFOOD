package br.dev.thiago.FastFood.domain.repository;

import br.dev.thiago.FastFood.domain.model.Produto;
import java.util.List;
import org.springframework.data.repository.CrudRepository;


public interface ProdutoRepository extends CrudRepository<Produto, Long> {
    List<Produto> findByCategoria(String categoria);
}

