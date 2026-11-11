package br.com.darkkitchen.repository;

import br.com.darkkitchen.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("select u from Usuario u where u.email = :email")
    Optional<Usuario> buscarPorEmail(@Param("email") String email);

    boolean existsByEmail(String email);

    @Query("select u from Usuario u order by u.nome")
    List<Usuario> listarTodos();
}
