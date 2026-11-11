package br.com.darkkitchen.controller;

import br.com.darkkitchen.dto.UsuarioDto;
import br.com.darkkitchen.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Só ADMIN: cria gerentes, cozinheiros e motoboys; desativa e reativa acessos. */
@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {
    private final UsuarioService usuarioService;

    @GetMapping
    public List<UsuarioDto.Resposta> listar() {
        return usuarioService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioDto.Resposta criarFuncionario(@Valid @RequestBody UsuarioDto.Requisicao requisicao) {
        return usuarioService.criarFuncionario(requisicao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(Authentication autenticacao, @PathVariable Long id) {
        usuarioService.desativar(id, autenticacao.getName());
    }

    @PostMapping("/{id}/reativar")
    public UsuarioDto.Resposta reativar(@PathVariable Long id) {
        return usuarioService.reativar(id);
    }
}
