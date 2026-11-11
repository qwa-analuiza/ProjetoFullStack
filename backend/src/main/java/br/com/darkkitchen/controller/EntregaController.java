package br.com.darkkitchen.controller;

import br.com.darkkitchen.dto.PedidoDto;
import br.com.darkkitchen.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Tela do motoboy (MOTOBOY, e também GERENTE e ADMIN). */
@RestController
@RequestMapping("/api/entregas")
@RequiredArgsConstructor
public class EntregaController {

    private final PedidoService pedidoService;

    /** Pedidos PRONTOS esperando um motoboy. */
    @GetMapping("/disponiveis")
    public List<PedidoDto.Resposta> disponiveis() {
        return pedidoService.listarProntosParaRetirada();
    }

    /** Entregas de quem está logado: em rota e concluídas hoje. */
    @GetMapping("/minhas")
    public List<PedidoDto.Resposta> minhas(Authentication autenticacao) {
        return pedidoService.listarEntregasDoEntregador(autenticacao.getName());
    }

    /** PRONTO → SAIU_PARA_ENTREGA, e o pedido fica com quem retirou. */
    @PostMapping("/{id}/retirar")
    public PedidoDto.Resposta retirar(Authentication autenticacao, @PathVariable Long id) {
        return pedidoService.retirarParaEntrega(id, autenticacao.getName());
    }

    /** SAIU_PARA_ENTREGA → ENTREGUE. */
    @PostMapping("/{id}/entregar")
    public PedidoDto.Resposta entregar(Authentication autenticacao, @PathVariable Long id) {
        boolean gestor = autenticacao.getAuthorities().stream()
                .anyMatch(p -> p.getAuthority().equals("ROLE_GERENTE") || p.getAuthority().equals("ROLE_ADMIN"));
        return pedidoService.concluirEntrega(id, autenticacao.getName(), gestor);
    }
}
