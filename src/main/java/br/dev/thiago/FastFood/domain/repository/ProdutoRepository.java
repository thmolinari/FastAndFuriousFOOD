package br.dev.thiago.FastFood.domain.repository;

import br.dev.thiago.FastFood.domain.model.Produto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByCategoriaIgnoreCase(String categoria);
}

