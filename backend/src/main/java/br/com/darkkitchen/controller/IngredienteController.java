package br.com.darkkitchen.controller;

import br.com.darkkitchen.dto.IngredienteDto;
import br.com.darkkitchen.service.IngredienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ingredientes")
@RequiredArgsConstructor
public class IngredienteController {
    private final IngredienteService ingredienteService;

    @GetMapping
    public List<IngredienteDto.Resposta> listar() {
        return ingredienteService.listar();
    }

    @GetMapping("/{id}")
    public IngredienteDto.Resposta buscar(@PathVariable Long id) {
        return ingredienteService.buscar(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IngredienteDto.Resposta criar(@Valid @RequestBody IngredienteDto.Requisicao requisicao) {
        return ingredienteService.criar(requisicao);
    }

    @PutMapping("/{id}")
    public IngredienteDto.Resposta atualizar(@PathVariable Long id, @Valid @RequestBody IngredienteDto.Requisicao requisicao) {
        return ingredienteService.atualizar(id, requisicao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) {
        ingredienteService.desativar(id);
    }
}
