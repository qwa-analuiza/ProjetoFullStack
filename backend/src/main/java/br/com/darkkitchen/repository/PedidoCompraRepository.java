package br.com.darkkitchen.repository;

import br.com.darkkitchen.entity.PedidoCompra;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, Long> {

    @EntityGraph(attributePaths = {"fornecedor", "itens", "itens.ingrediente"})
    @Query("select c from PedidoCompra c order by c.criadoEm desc")
    List<PedidoCompra> listarTodas();

    @EntityGraph(attributePaths = {"fornecedor", "itens", "itens.ingrediente"})
    @Query("select c from PedidoCompra c where c.id = :id")
    Optional<PedidoCompra> buscarComItens(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from PedidoCompra c where c.id = :id")
    Optional<PedidoCompra> buscarParaAtualizar(@Param("id") Long id);
}
