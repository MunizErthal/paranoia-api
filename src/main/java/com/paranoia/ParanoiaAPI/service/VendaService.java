package com.paranoia.ParanoiaAPI.service;

import com.paranoia.ParanoiaAPI.domain.Endereco;
import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.domain.Venda;
import com.paranoia.ParanoiaAPI.domain.VendaItem;
import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.domain.enums.MercadoPagoStatusEnum;
import com.paranoia.ParanoiaAPI.domain.enums.Produtos;
import com.paranoia.ParanoiaAPI.dto.Venda.VendaCriacaoDTO;
import com.paranoia.ParanoiaAPI.dto.Venda.VendaCriacaoItensDTO;
import com.paranoia.ParanoiaAPI.dto.Venda.VendaDTO;
import com.paranoia.ParanoiaAPI.dto.Venda.VendaItemDTO;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import com.paranoia.ParanoiaAPI.repository.EnderecoRepository;
import com.paranoia.ParanoiaAPI.repository.VendaItemRepository;
import com.paranoia.ParanoiaAPI.repository.VendaRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.Map.entry;
import static java.util.Objects.isNull;

@Service
public class VendaService {
    private final MailService mailService;
    private final PerfilService perfilService;
    private final VendaRepository vendaRepository;
    private final VendaItemRepository vendaItemRepository;
    private final EnderecoRepository enderecoRepository;
    private final TaskScheduler taskScheduler;

    private static final int UMA_HORA = 3600000;
    private static final Map<String, BigDecimal> CUPONS = Map.ofEntries(entry("ESCAPEDEMESA5", BigDecimal.valueOf(5.0)));

    @Autowired
    public VendaService(final MailService mailService,
                        final PerfilService perfilService,
                        final VendaRepository vendaRepository,
                        final EnderecoRepository enderecoRepository,
                        final VendaItemRepository vendaItemRepository) {
        this.mailService = mailService;
        this.perfilService = perfilService;
        this.vendaRepository = vendaRepository;
        this.enderecoRepository = enderecoRepository;
        this.vendaItemRepository = vendaItemRepository;

        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.initialize();
        this.taskScheduler = threadPoolTaskScheduler;
    }

    public VendaDTO criar(final Usuario usuario,
                          final VendaCriacaoDTO vendaDTO) {
        if (usuario.getPerfil().getBitnoias() == vendaDTO.getBitnoia()) {
            throw new ParanoiaException(HttpStatus.BAD_REQUEST, HistoricoAcoes.CRIAR_VENDA_ERROR, null, "Você não tem a quantidade de BitNoias informada.");
        }

        var cupomDesconto = this.verificarCupom(vendaDTO.getCupom());
        if (!vendaDTO.getCupom().equals("") && cupomDesconto.compareTo(BigDecimal.ZERO) == 0) {
            throw new ParanoiaException(HttpStatus.BAD_REQUEST, HistoricoAcoes.CRIAR_VENDA_ERROR, null, "Cupom informado é inválido.");
        }

        var verificarPreco = CollectionUtils.emptyIfNull(vendaDTO.getItens()).stream()
                .map(item -> Produtos.valueOf(item.getProduto()).getValor()
                        .multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(vendaDTO.getFrete())
                .subtract(cupomDesconto)
                .subtract(BigDecimal.valueOf(vendaDTO.getBitnoia() * 0.05))
                .compareTo(vendaDTO.getValorTotal());

        if (verificarPreco != 0) {
            throw new ParanoiaException(HttpStatus.BAD_REQUEST, HistoricoAcoes.CRIAR_VENDA_ERROR, null, "Valor total dos itens não confere com o valor total da venda.");
        }

        var endereco = Optional.of(vendaDTO.getEndereco())
                .map(enderecoDTO -> Endereco.builder()
                        .cidade(enderecoDTO.getCidade())
                        .bairro(enderecoDTO.getBairro())
                        .rua(enderecoDTO.getRua())
                        .numero(enderecoDTO.getNumero())
                        .complemento(enderecoDTO.getComplemento())
                        .build())
                .map(enderecoRepository::save)
                .orElse(null);

        return Optional.of(vendaRepository.save(Venda.builder()
                        .bitnoia(vendaDTO.getBitnoia())
                        .compradoEm(LocalDateTime.now())
                        .cupom(vendaDTO.getCupom())
                        .entrega(vendaDTO.isRetirada())
                        .frete(vendaDTO.getFrete())
                        .usuario(usuario)
                        .valorTotal(vendaDTO.getValorTotal())
                        .reservadoPara(vendaDTO.getReservadoPara())
                        .endereco(endereco)
                        .build()))
                .map(venda -> {
                    var vandaItens = CollectionUtils.emptyIfNull(vendaDTO.getItens()).stream()
                                    .map(vendaCriacaoItensDTO -> VendaItem.builder()
                                            .venda(venda)
                                            .quantidade(vendaCriacaoItensDTO.getQuantidade())
                                            .valorTotal(vendaCriacaoItensDTO.getValorTotal())
                                            .valorIndividual(vendaCriacaoItensDTO.getValorIndividual())
                                            .produto(Produtos.valueOf(vendaCriacaoItensDTO.getProduto()))
                                            .build())
                            .collect(Collectors.toList());
                    vendaItemRepository.saveAll(vandaItens);

                    usuario.getPerfil().setBitnoias(usuario.getPerfil().getBitnoias() - vendaDTO.getBitnoia());
                    this.perfilService.salvar(usuario.getPerfil());

                    var response = new VendaDTO(venda);
                    response.setItens(vandaItens.stream()
                            .map(VendaItemDTO::new)
                            .collect(Collectors.toList()));
                    return response;
                }).orElseThrow(() -> new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.CRIAR_VENDA_ERROR, null, "Erro ao criar venda, usuário não encontrado"));
    }

    public List<LocalDateTime> obter() {
        return this.vendaRepository.findByReservadoParaAfter(LocalDateTime.now()).stream()
                .map(Venda::getReservadoPara)
                .collect(Collectors.toList());
    }

    public void alterarMercadoPago(final UUID vendaId,
                                   final String mercadoPagoId,
                                   final String htmlContrato) {
        vendaRepository.findById(vendaId)
                .ifPresentOrElse(venda -> {
                    venda.setMercadoPagoId(mercadoPagoId);
                    vendaRepository.save(venda);

                    var itens = vendaItemRepository.findByVenda(venda);
                    CompletableFuture.runAsync(() -> mailService.enviarEmailVendaEmProcessamento(venda, itens, htmlContrato));
                    taskScheduler.schedule(() -> validarPagamento(vendaId), new Date(System.currentTimeMillis() + UMA_HORA));
                }, () -> {
                    throw new ParanoiaException(HttpStatus.NOT_FOUND, HistoricoAcoes.ALTERAR_VENDA_ERROR, null, "Erro ao alterar mercado pago da venda");
                });
    }

    public void confirmar(final UUID vendaId,
                          final MercadoPagoStatusEnum status) {
        vendaRepository.findById(vendaId)
                .ifPresent(venda -> {
                    var itens = vendaItemRepository.findByVenda(venda);
                    if (List.of(MercadoPagoStatusEnum.APPROVED, MercadoPagoStatusEnum.AUTHORIZED).contains(status)) {
                        venda.setPagoEm(LocalDateTime.now());
                        venda.setStatus(status);
                        vendaRepository.save(venda);
                        CompletableFuture.runAsync(() -> mailService.enviarEmailVendaFinalizada(venda, itens));
                    } else if (List.of(MercadoPagoStatusEnum.PENDING, MercadoPagoStatusEnum.IN_PROCESS, MercadoPagoStatusEnum.IN_MEDIATION, MercadoPagoStatusEnum.REJECTED).contains(status)) {
                        venda.setStatus(status);
                        vendaRepository.save(venda);
                        CompletableFuture.runAsync(() -> mailService.enviarEmailVendaPendente(venda, itens));
                    } else if (List.of(MercadoPagoStatusEnum.CANCELLED, MercadoPagoStatusEnum.REFUNDED, MercadoPagoStatusEnum.CHARGED_BACK).contains(status)) {
                        CompletableFuture.runAsync(() -> mailService.enviarEmailVendaFalhou(venda, itens, Boolean.FALSE));
                        this.devolverBitNoias(venda);
                        itens.forEach(vendaItemRepository::delete);
                        vendaRepository.delete(venda);
                    }
                });
    }

    public BigDecimal verificarCupom(final String cupom) {
        return CUPONS.entrySet()
                .stream()
                .filter(entry -> StringUtils.equalsIgnoreCase(entry.getKey(), cupom))
                .findFirst()
                .orElseGet(() -> entry("", BigDecimal.ZERO))
                .getValue();
    }

    private void validarPagamento(final UUID vendaId) {
        vendaRepository.findById(vendaId)
                .ifPresent(venda -> {
                    if (isNull(venda.getPagoEm())
                            && (isNull(venda.getStatus()) || MercadoPagoStatusEnum.REJECTED.equals(venda.getStatus()))) {
                        this.devolverBitNoias(venda);

                        var itens = vendaItemRepository.findByVenda(venda);
                        CompletableFuture.runAsync(() -> mailService.enviarEmailVendaFalhou(venda, itens, Boolean.TRUE));
                        itens.forEach(vendaItemRepository::delete);
                        vendaRepository.delete(venda);
                    } else if (isNull(venda.getPagoEm())) {
                        taskScheduler.schedule(() -> validarPagamento(vendaId), new Date(System.currentTimeMillis() + (4 * UMA_HORA)));
                    }
                });
    }

    private void devolverBitNoias(Venda venda) {
        var bitNoias = venda.getUsuario().getPerfil().getBitnoias();
        venda.getUsuario().getPerfil().setBitnoias(bitNoias + venda.getBitnoia());
        perfilService.salvar(venda.getUsuario().getPerfil());
    }
}