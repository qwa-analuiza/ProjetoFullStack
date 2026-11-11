package br.com.darkkitchen.dto;

import br.com.darkkitchen.entity.StatusPedido;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface DashboardDto {

    record MaisVendido(String prato, long quantidade) {}

    record Resposta(long pedidosHoje, BigDecimal faturamentoHoje, BigDecimal custoHoje, BigDecimal lucroBrutoHoje,
                    Map<StatusPedido, Long> pedidosPorStatus, long ingredientesComEstoqueBaixo,
                    long lotesVencendoOuVencidos, List<MaisVendido> maisVendidosHoje) {}
}
