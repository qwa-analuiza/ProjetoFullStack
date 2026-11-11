package br.com.darkkitchen.repository;

import br.com.darkkitchen.entity.Ingrediente;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {

    @Query("select i from Ingrediente i where i.ativo = true order by i.nome")
    List<Ingrediente> listarAtivos();

    @Query("select i from Ingrediente i where i.ativo = true and i.estoqueAtual <= i.estoqueMinimo order by i.nome")
    List<Ingrediente> listarComEstoqueBaixo();

    /** Trava a linha para que dois pedidos não consumam o mesmo estoque ao mesmo tempo. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Ingrediente i where i.id = :id")
    Optional<Ingrediente> buscarParaAtualizar(@Param("id") Long id);
}
