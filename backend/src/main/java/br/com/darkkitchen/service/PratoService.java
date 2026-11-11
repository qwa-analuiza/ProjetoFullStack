package br.com.darkkitchen.service;

import br.com.darkkitchen.dto.PratoDto;
import br.com.darkkitchen.entity.Ingrediente;
import br.com.darkkitchen.entity.ItemFicha;
import br.com.darkkitchen.entity.Prato;
import br.com.darkkitchen.exception.NaoEncontradoException;
import br.com.darkkitchen.exception.RegraNegocioException;
import br.com.darkkitchen.repository.IngredienteRepository;
import br.com.darkkitchen.repository.PratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PratoService {
    private final PratoRepository pratoRepository;
    private final IngredienteRepository ingredienteRepository;

    // ---- cardápio público ----
    @Transactional(readOnly = true)
    public List<PratoDto.CardapioResposta> listarCardapio() {
        return pratoRepository.listarDoCardapio().stream().map(PratoDto.CardapioResposta::de).toList();
    }

    @Transactional(readOnly = true)
    public PratoDto.CardapioResposta buscarNoCardapio(Long id) {
        Prato prato = pratoRepository.findById(id).filter(Prato::isAtivo)
                .orElseThrow(() -> new NaoEncontradoException("Prato não encontrado"));
        return PratoDto.CardapioResposta.de(prato);
    }

    // ---- ADMIN: pratos com ficha técnica, custo e margem ----
    @Transactional(readOnly = true)
    public List<PratoDto.Resposta> listar() {
        return pratoRepository.listarComFicha().stream().map(PratoDto.Resposta::de).toList();
    }

    @Transactional(readOnly = true)
    public PratoDto.Resposta buscar(Long id) {
        return PratoDto.Resposta.de(buscarComFicha(id));
    }

    @Transactional
    public PratoDto.Resposta criar(PratoDto.Requisicao requisicao) {
        Prato prato = new Prato();
        preencher(prato, requisicao);
        return PratoDto.Resposta.de(pratoRepository.save(prato));
    }

    @Transactional
    public PratoDto.Resposta atualizar(Long id, PratoDto.Requisicao requisicao) {
        Prato prato = buscarComFicha(id);
        preencher(prato, requisicao);
        return PratoDto.Resposta.de(prato);
    }

    /** Prato já vendido não é apagado: só sai do cardápio. */
    @Transactional
    public void desativar(Long id) {
        buscarComFicha(id).setAtivo(false);
    }

    private void preencher(Prato prato, PratoDto.Requisicao requisicao) {
        prato.setNome(requisicao.nome().trim());
        prato.setDescricao(requisicao.descricao());
        prato.setCategoria(requisicao.categoria());
        prato.setImagemUrl(requisicao.imagemUrl());
        prato.setPreco(requisicao.preco());
        if (requisicao.ativo() != null) prato.setAtivo(requisicao.ativo());

        prato.getItens().clear();
        Set<Long> jaUsados = new HashSet<>();
        for (PratoDto.ItemFichaRequisicao itemRequisicao : requisicao.ficha()) {
            if (!jaUsados.add(itemRequisicao.ingredienteId()))
                throw new RegraNegocioException("Ingrediente repetido na ficha técnica: " + itemRequisicao.ingredienteId());
            Ingrediente ingrediente = ingredienteRepository.findById(itemRequisicao.ingredienteId())
                    .filter(Ingrediente::isAtivo)
                    .orElseThrow(() -> new NaoEncontradoException("Ingrediente não encontrado: " + itemRequisicao.ingredienteId()));
            ItemFicha item = new ItemFicha();
            item.setPrato(prato);
            item.setIngrediente(ingrediente);
            item.setQuantidade(itemRequisicao.quantidade());
            prato.getItens().add(item);
        }
    }

    private Prato buscarComFicha(Long id) {
        return pratoRepository.buscarComFicha(id).orElseThrow(() -> new NaoEncontradoException("Prato não encontrado"));
    }
}
