package br.dev.thiago.FastFood.domain.service;

import br.dev.thiago.FastFood.domain.model.Pedido;
import br.dev.thiago.FastFood.domain.repository.PedidoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PedidoService {

    @Autowired
    PedidoRepository pedidoRepo;

    public boolean existsById(Long id) {
        return pedidoRepo.existsById(id);
    }

    public List<Pedido> listarPedidos() {
        return (List<Pedido>) pedidoRepo.findAll();
    }

    public Optional<Pedido> findById(Long id) {
        return pedidoRepo.findById(id);
    }
    
    public Pedido salvarPedido(Pedido pedido) {
        return pedidoRepo.save(pedido);
    }
    
    public void deletarPedido(Long id) {
        pedidoRepo.deleteById(id);
    }
    
    public List<Pedido> listarPorStatus(Pedido.Status status) {
        return (List<Pedido>) pedidoRepo.findByStatus(status);
    }
    
    public Pedido alterarStatus(Pedido pedido, Pedido.Status status) {
        if (pedido.getStatus() == Pedido.Status.ENTREGUE) {
            throw new IllegalArgumentException
            ("Não é possível alterar o status de um pedido já entregue.");
        }
        
        pedido.setStatus(status);
        return pedidoRepo.save(pedido);
    }
}
