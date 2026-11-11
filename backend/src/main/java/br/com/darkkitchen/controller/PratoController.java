package br.com.darkkitchen.controller;

import br.com.darkkitchen.dto.PratoDto;
import br.com.darkkitchen.service.PratoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/** Pratos com ficha técnica, custo e margem (só ADMIN). */
@RestController
@RequestMapping("/api/pratos")
@RequiredArgsConstructor
public class PratoController {
    private final PratoService pratoService;

    @GetMapping
    public List<PratoDto.Resposta> listar() {
        return pratoService.listar();
    }

    @GetMapping("/{id}")
    public PratoDto.Resposta buscar(@PathVariable Long id) {
        return pratoService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PratoDto.Resposta criar(@Valid @RequestBody PratoDto.Requisicao requisicao) {
        return pratoService.criar(requisicao);
    }

    @PutMapping("/{id}")
    public PratoDto.Resposta atualizar(@PathVariable Long id, @Valid @RequestBody PratoDto.Requisicao requisicao) {
        return pratoService.atualizar(id, requisicao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) {
        pratoService.desativar(id);
    }
}
