package br.com.darkkitchen.controller;

import br.com.darkkitchen.dto.CompraDto;
import br.com.darkkitchen.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {
    private final CompraService compraService;

    @GetMapping
    public List<CompraDto.Resposta> listar() {
        return compraService.listar();
    }

    @GetMapping("/{id}")
    public CompraDto.Resposta buscar(@PathVariable Long id) {
        return compraService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompraDto.Resposta criar(@Valid @RequestBody CompraDto.Requisicao requisicao) {
        return compraService.criar(requisicao);
    }

    @PostMapping("/{id}/enviar")
    public CompraDto.Resposta enviar(@PathVariable Long id) {
        return compraService.enviar(id);
    }

    @PostMapping("/{id}/cancelar")
    public CompraDto.Resposta cancelar(@PathVariable Long id) {
        return compraService.cancelar(id);
    }

    /** Corpo opcional: sem corpo, recebe tudo como foi pedido (sem validade). */
    @PostMapping("/{id}/receber")
    public CompraDto.Resposta receber(@PathVariable Long id,
                                      @Valid @RequestBody(required = false) CompraDto.RecebimentoRequisicao requisicao) {
        return compraService.receber(id, requisicao);
    }
}
