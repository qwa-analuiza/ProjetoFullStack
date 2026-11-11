package br.com.darkkitchen.service;

import br.com.darkkitchen.config.TokenService;
import br.com.darkkitchen.dto.AutenticacaoDto;
import br.com.darkkitchen.dto.UsuarioDto;
import br.com.darkkitchen.entity.Perfil;
import br.com.darkkitchen.entity.Usuario;
import br.com.darkkitchen.exception.NaoAutorizadoException;
import br.com.darkkitchen.exception.NaoEncontradoException;
import br.com.darkkitchen.exception.RegraNegocioException;
import br.com.darkkitchen.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder codificadorDeSenha;
    private final TokenService tokenService;

    /** Cadastro público: sempre cria um CLIENTE. */
    @Transactional
    public AutenticacaoDto.Resposta cadastrar(AutenticacaoDto.CadastroRequisicao requisicao) {
        String email = requisicao.email().trim().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) throw new RegraNegocioException("E-mail já cadastrado");

        Usuario cliente = new Usuario();
        cliente.setNome(requisicao.nome().trim());
        cliente.setEmail(email);
        cliente.setSenhaHash(codificadorDeSenha.encode(requisicao.senha()));
        cliente.setEndereco(requisicao.endereco().trim());
        cliente.setTelefone(requisicao.telefone().trim());
        cliente.setPerfil(Perfil.CLIENTE);
        usuarioRepository.save(cliente);
        return new AutenticacaoDto.Resposta(tokenService.gerar(cliente), UsuarioDto.Resposta.de(cliente));
    }

    @Transactional(readOnly = true)
    public AutenticacaoDto.Resposta entrar(AutenticacaoDto.LoginRequisicao requisicao) {
        Usuario usuario = usuarioRepository.buscarPorEmail(requisicao.email().trim().toLowerCase())
                .filter(u -> codificadorDeSenha.matches(requisicao.senha(), u.getSenhaHash()))
                .orElseThrow(() -> new NaoAutorizadoException("E-mail ou senha inválidos"));
        // só revela que está desativado para quem acertou a senha
        if (!usuario.isAtivo())
            throw new NaoAutorizadoException("Seu acesso está desativado. Fale com o administrador.");
        return new AutenticacaoDto.Resposta(tokenService.gerar(usuario), UsuarioDto.Resposta.de(usuario));
    }

    @Transactional(readOnly = true)
    public UsuarioDto.Resposta buscarUsuarioLogado(String email) {
        return UsuarioDto.Resposta.de(usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new NaoEncontradoException("Usuário não encontrado")));
    }
}
