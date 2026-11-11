package br.com.darkkitchen.controller;

import br.com.darkkitchen.dto.PratoDto;
import br.com.darkkitchen.service.PratoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Público: o cliente vê o cardápio sem precisar de login. */
@RestController
@RequestMapping("/api/cardapio")
@RequiredArgsConstructor
public class CardapioController {
    private final PratoService pratoService;

    @GetMapping
    public List<PratoDto.CardapioResposta> listar() {
        return pratoService.listarCardapio();
    }

    @GetMapping("/{id}")
    public PratoDto.CardapioResposta buscar(@PathVariable Long id) {
        return pratoService.buscarNoCardapio(id);
    }
}
