package br.com.darkkitchen.service;

import br.com.darkkitchen.dto.IngredienteDto;
import br.com.darkkitchen.entity.Ingrediente;
import br.com.darkkitchen.exception.NaoEncontradoException;
import br.com.darkkitchen.repository.IngredienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredienteService {
    private final IngredienteRepository ingredienteRepository;

    @Transactional(readOnly = true)
    public List<IngredienteDto.Resposta> listar() {
        return ingredienteRepository.listarAtivos().stream().map(IngredienteDto.Resposta::de).toList();
    }

    @Transactional(readOnly = true)
    public IngredienteDto.Resposta buscar(Long id) {
        return IngredienteDto.Resposta.de(buscarAtivo(id));
    }

    /** O estoque inicial entra por /api/estoque/entradas, para ficar registrado com lote e validade. */
    @Transactional
    public IngredienteDto.Resposta criar(IngredienteDto.Requisicao requisicao) {
        Ingrediente ingrediente = new Ingrediente();
        preencher(ingrediente, requisicao);
        return IngredienteDto.Resposta.de(ingredienteRepository.save(ingrediente));
    }

    @Transactional
    public IngredienteDto.Resposta atualizar(Long id, IngredienteDto.Requisicao requisicao) {
        Ingrediente ingrediente = buscarAtivo(id);
        preencher(ingrediente, requisicao);
        return IngredienteDto.Resposta.de(ingrediente);
    }

    /** Não apaga: só desativa, para não quebrar fichas técnicas e histórico. */
    @Transactional
    public void desativar(Long id) {
        buscarAtivo(id).setAtivo(false);
    }

    private void preencher(Ingrediente ingrediente, IngredienteDto.Requisicao requisicao) {
        ingrediente.setNome(requisicao.nome().trim());
        ingrediente.setUnidade(requisicao.unidade());
        ingrediente.setEstoqueMinimo(requisicao.estoqueMinimo());
        ingrediente.setCustoUnitario(requisicao.custoUnitario());
    }

    private Ingrediente buscarAtivo(Long id) {
        return ingredienteRepository.findById(id).filter(Ingrediente::isAtivo)
                .orElseThrow(() -> new NaoEncontradoException("Ingrediente não encontrado"));
    }
}
