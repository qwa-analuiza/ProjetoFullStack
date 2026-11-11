package br.com.darkkitchen.repository;

import br.com.darkkitchen.entity.Pedido;
import br.com.darkkitchen.entity.StatusPedido;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @EntityGraph(attributePaths = {"cliente", "entregador", "itens", "itens.prato"})
    @Query("select p from Pedido p where p.cliente.id = :clienteId order by p.criadoEm desc")
    List<Pedido> listarDoCliente(@Param("clienteId") Long clienteId);

    @EntityGraph(attributePaths = {"cliente", "entregador", "itens", "itens.prato"})
    @Query("select p from Pedido p where p.status in :status order by p.criadoEm")
    List<Pedido> listarPorStatus(@Param("status") Collection<StatusPedido> status);

    @EntityGraph(attributePaths = {"cliente", "entregador", "itens", "itens.prato"})
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> buscarComItens(@Param("id") Long id);

    @EntityGraph(attributePaths = {"itens", "itens.prato"})
    @Query("select p from Pedido p where p.criadoEm >= :inicio")
    List<Pedido> listarDesde(@Param("inicio") Instant inicio);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Pedido p where p.id = :id")
    Optional<Pedido> buscarParaAtualizar(@Param("id") Long id);

    @EntityGraph(attributePaths = {"cliente", "entregador", "itens", "itens.prato"})
    @Query("select p from Pedido p where p.entregador.id = :entregadorId order by p.atualizadoEm desc")
    List<Pedido> listarDoEntregador(@Param("entregadorId") Long entregadorId);
}
