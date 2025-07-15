package com.paranoia.ParanoiaAPI.service;

import com.paranoia.ParanoiaAPI.domain.Historico;
import com.paranoia.ParanoiaAPI.domain.Usuario;
import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.repository.HistoricoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HistoricoService {

    private final HistoricoRepository historicoRepository;

    @Autowired
    public HistoricoService(final HistoricoRepository historicoRepository) {
        this.historicoRepository = historicoRepository;
    }

    public void registrar(final Usuario usuario,
                               final HistoricoAcoes acao,
                               final String mensagem,
                               final Object ... args) {
        historicoRepository.save(Historico.builder()
                .descricao(String.format(mensagem, args))
                .criadoEm(LocalDateTime.now())
                .codigoHistorico(acao)
                .erro(acao.getErro())
                .usuario(usuario)
                .build());
    }
}
