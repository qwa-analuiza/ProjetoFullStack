package br.com.darkkitchen.service;

import br.com.darkkitchen.dto.FornecedorDto;
import br.com.darkkitchen.entity.Fornecedor;
import br.com.darkkitchen.exception.NaoEncontradoException;
import br.com.darkkitchen.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FornecedorService {
    private final FornecedorRepository fornecedorRepository;

    @Transactional(readOnly = true)
    public List<FornecedorDto.Resposta> listar() {
        return fornecedorRepository.listarAtivos().stream().map(FornecedorDto.Resposta::de).toList();
    }

    @Transactional
    public FornecedorDto.Resposta criar(FornecedorDto.Requisicao requisicao) {
        Fornecedor fornecedor = new Fornecedor();
        preencher(fornecedor, requisicao);
        return FornecedorDto.Resposta.de(fornecedorRepository.save(fornecedor));
    }

    @Transactional
    public FornecedorDto.Resposta atualizar(Long id, FornecedorDto.Requisicao requisicao) {
        Fornecedor fornecedor = buscarAtivo(id);
        preencher(fornecedor, requisicao);
        return FornecedorDto.Resposta.de(fornecedor);
    }

    @Transactional
    public void desativar(Long id) {
        buscarAtivo(id).setAtivo(false);
    }

    private void preencher(Fornecedor fornecedor, FornecedorDto.Requisicao requisicao) {
        fornecedor.setNome(requisicao.nome().trim());
        fornecedor.setCnpj(requisicao.cnpj());
        fornecedor.setEmail(requisicao.email());
        fornecedor.setTelefone(requisicao.telefone());
    }

    private Fornecedor buscarAtivo(Long id) {
        return fornecedorRepository.findById(id).filter(Fornecedor::isAtivo)
                .orElseThrow(() -> new NaoEncontradoException("Fornecedor não encontrado"));
    }
}
