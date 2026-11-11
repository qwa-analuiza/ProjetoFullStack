package br.com.darkkitchen.service;

import br.com.darkkitchen.dto.DashboardDto;
import br.com.darkkitchen.entity.ItemPedido;
import br.com.darkkitchen.entity.Pedido;
import br.com.darkkitchen.entity.StatusPedido;
import br.com.darkkitchen.repository.IngredienteRepository;
import br.com.darkkitchen.repository.LoteEstoqueRepository;
import br.com.darkkitchen.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final PedidoRepository pedidoRepository;
    private final IngredienteRepository ingredienteRepository;
    private final LoteEstoqueRepository loteRepository;

    @Transactional(readOnly = true)
    public DashboardDto.Resposta resumo() {
        LocalDate hoje = EstoqueService.hoje();
        Instant inicioDoDia = hoje.atStartOfDay(EstoqueService.FUSO).toInstant();
        List<Pedido> pedidosDeHoje = pedidoRepository.listarDesde(inicioDoDia);

        Map<StatusPedido, Long> pedidosPorStatus = new EnumMap<>(StatusPedido.class);
        Map<String, Long> vendidosPorPrato = new HashMap<>();
        BigDecimal faturamento = BigDecimal.ZERO;
        BigDecimal custo = BigDecimal.ZERO;

        for (Pedido pedido : pedidosDeHoje) {
            pedidosPorStatus.merge(pedido.getStatus(), 1L, Long::sum);
            if (pedido.getStatus() == StatusPedido.CANCELADO) continue;
            faturamento = faturamento.add(pedido.getTotal());
            for (ItemPedido item : pedido.getItens()) {
                custo = custo.add(item.getCustoUnitario().multiply(BigDecimal.valueOf(item.getQuantidade())));
                vendidosPorPrato.merge(item.getPrato().getNome(), item.getQuantidade().longValue(), Long::sum);
            }
        }

        List<DashboardDto.MaisVendido> maisVendidos = vendidosPorPrato.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .map(e -> new DashboardDto.MaisVendido(e.getKey(), e.getValue()))
                .toList();

        return new DashboardDto.Resposta(
                pedidosDeHoje.size(), faturamento, custo, faturamento.subtract(custo), pedidosPorStatus,
                ingredienteRepository.listarComEstoqueBaixo().size(),
                loteRepository.listarComValidadeAte(hoje.plusDays(3)).size(),
                maisVendidos);
    }
}
