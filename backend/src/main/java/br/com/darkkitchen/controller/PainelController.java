package br.com.darkkitchen.controller;

import br.com.darkkitchen.dto.PedidoDto;
import br.com.darkkitchen.entity.StatusPedido;
import br.com.darkkitchen.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Tela da cozinha / gestão de pedidos (COZINHEIRO, GERENTE e ADMIN). */
@RestController
@RequestMapping("/api/painel/pedidos")
@RequiredArgsConstructor
public class PainelController {
    private final PedidoService pedidoService;

    /** Fila de produção (mais antigos primeiro). Sem filtro: RECEBIDO, CONFIRMADO, EM_PREPARO e PRONTO. */
    @GetMapping
    public List<PedidoDto.Resposta> listarFila(Authentication autenticacao,
                                               @RequestParam(required = false) StatusPedido status) {
        boolean cozinheiro = autenticacao.getAuthorities().stream()
                .anyMatch(p -> p.getAuthority().equals("ROLE_COZINHEIRO"));
        return pedidoService.listarFila(status, cozinheiro);
    }

    /** Ao passar para CONFIRMADO, o estoque é baixado automaticamente. */
    @PatchMapping("/{id}/status")
    public PedidoDto.Resposta atualizarStatus(Authentication autenticacao, @PathVariable Long id,
                                              @Valid @RequestBody PedidoDto.StatusRequisicao requisicao) {
        boolean cozinheiro = autenticacao.getAuthorities().stream()
                .anyMatch(p -> p.getAuthority().equals("ROLE_COZINHEIRO"));
        return pedidoService.atualizarStatus(id, requisicao.status(), cozinheiro);
    }
}
