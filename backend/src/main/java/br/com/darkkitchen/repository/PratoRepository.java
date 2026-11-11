package br.com.darkkitchen.repository;

import br.com.darkkitchen.entity.Prato;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PratoRepository extends JpaRepository<Prato, Long> {

    @Query("select p from Prato p where p.ativo = true order by p.categoria, p.nome")
    List<Prato> listarDoCardapio();

    @EntityGraph(attributePaths = {"itens", "itens.ingrediente"})
    @Query("select p from Prato p order by p.nome")
    List<Prato> listarComFicha();

    @EntityGraph(attributePaths = {"itens", "itens.ingrediente"})
    @Query("select p from Prato p where p.id = :id")
    Optional<Prato> buscarComFicha(@Param("id") Long id);

    @EntityGraph(attributePaths = {"itens", "itens.ingrediente"})
    @Query("select p from Prato p where p.id in :ids")
    List<Prato> buscarVariosComFicha(@Param("ids") Collection<Long> ids);
}
