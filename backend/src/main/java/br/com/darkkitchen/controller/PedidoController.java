package br.com.darkkitchen.controller;

import br.com.darkkitchen.dto.PedidoDto;
import br.com.darkkitchen.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Pedidos do cliente logado. */
@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoController {
    private final PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoDto.Resposta criar(Authentication autenticacao, @Valid @RequestBody PedidoDto.Requisicao requisicao) {
        return pedidoService.criar(autenticacao.getName(), requisicao);
    }

    /** Histórico de pedidos do cliente. */
    @GetMapping
    public List<PedidoDto.Resposta> listarMeus(Authentication autenticacao) {
        return pedidoService.listarDoCliente(autenticacao.getName());
    }

    /** Acompanhar o status de um pedido. */
    @GetMapping("/{id}")
    public PedidoDto.Resposta buscar(Authentication autenticacao, @PathVariable Long id) {
        // cozinha, gerente e admin veem qualquer pedido; cliente e motoboy só os próprios
        boolean funcionario = autenticacao.getAuthorities().stream()
                .anyMatch(p -> List.of("ROLE_COZINHEIRO", "ROLE_GERENTE", "ROLE_ADMIN").contains(p.getAuthority()));
        return pedidoService.buscar(id, autenticacao.getName(), funcionario);
    }

    @PostMapping("/{id}/cancelar")
    public PedidoDto.Resposta cancelar(Authentication autenticacao, @PathVariable Long id) {
        return pedidoService.cancelarPeloCliente(id, autenticacao.getName());
    }
}
