package br.com.darkkitchen.controller;

import br.com.darkkitchen.dto.EstoqueDto;
import br.com.darkkitchen.service.EstoqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/estoque")
@RequiredArgsConstructor
public class EstoqueController {
    private final EstoqueService estoqueService;

    /** Estoque abaixo do mínimo + lotes vencidos ou que vencem nos próximos dias. */
    @GetMapping("/alertas")
    public EstoqueDto.AlertasResposta listarAlertas(@RequestParam(defaultValue = "3") int diasParaVencer) {
        return estoqueService.listarAlertas(diasParaVencer);
    }

    @GetMapping("/lotes")
    public List<EstoqueDto.LoteResposta> listarLotes() {
        return estoqueService.listarLotes();
    }

    /** Entrada avulsa (estoque inicial, ajuste). Compras recebidas já geram a entrada sozinhas. */
    @PostMapping("/entradas")
    @ResponseStatus(HttpStatus.CREATED)
    public EstoqueDto.LoteResposta registrarEntrada(@Valid @RequestBody EstoqueDto.EntradaRequisicao requisicao) {
        return estoqueService.registrarEntradaAvulsa(requisicao);
    }

    @PostMapping("/lotes/{id}/descartar")
    public EstoqueDto.LoteResposta descartarLote(@PathVariable Long id) {
        return estoqueService.descartarLote(id);
    }
}
