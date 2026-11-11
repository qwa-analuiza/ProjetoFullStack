package br.com.darkkitchen.service;

import br.com.darkkitchen.dto.UsuarioDto;
import br.com.darkkitchen.entity.Perfil;
import br.com.darkkitchen.entity.Usuario;
import br.com.darkkitchen.exception.NaoEncontradoException;
import br.com.darkkitchen.exception.RegraNegocioException;
import br.com.darkkitchen.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/** Gestão de usuários pelo ADMIN (criar gerente, cozinheiro, etc.). */
@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorDeSenha;

    @Transactional(readOnly = true)
    public List<UsuarioDto.Resposta> listar() {
        return usuarioRepository.listarTodos().stream().map(UsuarioDto.Resposta::de).toList();
    }

    @Transactional
    public UsuarioDto.Resposta criarFuncionario(UsuarioDto.Requisicao requisicao) {
        if (requisicao.perfil() == Perfil.CLIENTE)
            throw new RegraNegocioException("Clientes se cadastram sozinhos em /api/auth/cadastro");
        String email = requisicao.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) throw new RegraNegocioException("E-mail já cadastrado");

        Usuario funcionario = new Usuario();
        funcionario.setNome(requisicao.nome().trim());
        funcionario.setEmail(email);
        funcionario.setSenhaHash(codificadorDeSenha.encode(requisicao.senha()));
        funcionario.setPerfil(requisicao.perfil());
        return UsuarioDto.Resposta.de(usuarioRepository.save(funcionario));
    }

    @Transactional
    public void desativar(Long id, String emailDeQuemPediu) {
        Usuario usuario = buscar(id);
        if (usuario.getEmail().equals(emailDeQuemPediu))
            throw new RegraNegocioException("Você não pode desativar o seu próprio acesso");
        usuario.setAtivo(false);
    }

    @Transactional
    public UsuarioDto.Resposta reativar(Long id) {
        Usuario usuario = buscar(id);
        usuario.setAtivo(true);
        return UsuarioDto.Resposta.de(usuario);
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado"));
    }
}
