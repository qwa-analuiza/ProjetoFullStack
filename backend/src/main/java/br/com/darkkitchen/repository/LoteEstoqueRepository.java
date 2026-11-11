package br.com.darkkitchen.repository;

import br.com.darkkitchen.entity.LoteEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;

public interface LoteEstoqueRepository extends JpaRepository<LoteEstoque, Long> {

    /** Lotes não vencidos, o que vence primeiro vem primeiro. */
    @Query("select l from LoteEstoque l where l.ingrediente.id = :ingredienteId and l.quantidadeAtual > 0 "
            + "and (l.validade is null or l.validade >= :hoje) order by case when l.validade is null then 1 else 0 end, l.validade, l.id")
    List<LoteEstoque> listarUtilizaveis(@Param("ingredienteId") Long ingredienteId, @Param("hoje") LocalDate hoje);

    @Query("select l from LoteEstoque l join fetch l.ingrediente where l.quantidadeAtual > 0 "
            + "and l.validade is not null and l.validade <= :limite order by l.validade")
    List<LoteEstoque> listarComValidadeAte(@Param("limite") LocalDate limite);

    @Query("select l from LoteEstoque l join fetch l.ingrediente where l.quantidadeAtual > 0 "
            + "order by case when l.validade is null then 1 else 0 end, l.validade, l.id")
    List<LoteEstoque> listarComSaldo();
}
