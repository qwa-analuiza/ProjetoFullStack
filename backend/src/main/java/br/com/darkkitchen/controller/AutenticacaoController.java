package br.com.darkkitchen.controller;

import br.com.darkkitchen.dto.AutenticacaoDto;
import br.com.darkkitchen.dto.UsuarioDto;
import br.com.darkkitchen.service.AutenticacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AutenticacaoController {
    private final AutenticacaoService autenticacaoService;

    @PostMapping("/auth/cadastro")
    @ResponseStatus(HttpStatus.CREATED)
    public AutenticacaoDto.Resposta cadastrar(@Valid @RequestBody AutenticacaoDto.CadastroRequisicao requisicao) {
        return autenticacaoService.cadastrar(requisicao);
    }

    @PostMapping("/auth/login")
    public AutenticacaoDto.Resposta entrar(@Valid @RequestBody AutenticacaoDto.LoginRequisicao requisicao) {
        return autenticacaoService.entrar(requisicao);
    }

    @GetMapping("/eu")
    public UsuarioDto.Resposta usuarioLogado(Authentication autenticacao) {
        return autenticacaoService.buscarUsuarioLogado(autenticacao.getName());
    }
}
