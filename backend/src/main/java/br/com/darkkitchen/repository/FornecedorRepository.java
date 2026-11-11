package br.com.darkkitchen.repository;

import br.com.darkkitchen.entity.Fornecedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {

    @Query("select f from Fornecedor f where f.ativo = true order by f.nome")
    List<Fornecedor> listarAtivos();
}
