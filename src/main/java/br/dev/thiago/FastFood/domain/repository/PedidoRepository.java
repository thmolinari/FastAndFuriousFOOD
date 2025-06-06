package br.dev.thiago.FastFood.domain.repository;

import br.dev.thiago.FastFood.domain.model.Pedido;
import java.util.List;
import org.springframework.data.repository.CrudRepository;


public interface PedidoRepository extends CrudRepository<Pedido, Long> {
    List<Pedido> findByStatus(Pedido.Status status);
}
