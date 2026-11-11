package br.com.darkkitchen.controller;

import br.com.darkkitchen.dto.FornecedorDto;
import br.com.darkkitchen.service.FornecedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/fornecedores")
@RequiredArgsConstructor
public class FornecedorController {
    private final FornecedorService fornecedorService;

    @GetMapping
    public List<FornecedorDto.Resposta> listar() {
        return fornecedorService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FornecedorDto.Resposta criar(@Valid @RequestBody FornecedorDto.Requisicao requisicao) {
        return fornecedorService.criar(requisicao);
    }

    @PutMapping("/{id}")
    public FornecedorDto.Resposta atualizar(@PathVariable Long id, @Valid @RequestBody FornecedorDto.Requisicao requisicao) {
        return fornecedorService.atualizar(id, requisicao);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable Long id) {
        fornecedorService.desativar(id);
    }
}
