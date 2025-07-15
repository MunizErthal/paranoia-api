package com.paranoia.ParanoiaAPI.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Medalhas {
    MEMBRO_DA_INTELIGENCIA(1, "Membro da Inteligência","Resolva 1 EnigmaTchê",5, true, true),
    ANALISTA_DA_INTELIGENCIA(2, "Análista da Inteligência","Resolva 5 EnigmaTchês",10, true, true),
    MESTRE_DA_INGELIGENCIA(3, "Mestre da Inteligência","Resolva 10 EnigmaTchês",15, true, true),
    CRANIO_DA_INGELIGENCIA(4, "Crânio da Inteligência","Resolva 30 EnigmaTchês",50, true, true),
    LIDER_DA_INTELIGENCIA(5, "Líder da Inteligência","Resolva 50 EnigmaTchês",90, true, true),
    SUPREMO_MEMBRO_DA_INTELIGENCIA(6, "Supremo membro da inteligência","Resolva 100 EnigmaTchês",200, true, true),
    CONDECORAÇAO_ALFA(7, "Condecoração Alfa","Resolva todos os EnigmaTchês de um ciclo",20, true, true),
    CONDECORAÇAO_BETA(8, "Condecoração Beta","Resolva todos os EnigmaTchês de três ciclos",65, true, true),
    CONDECORAÇAO_DELTA(9, "Condecoração Delta","Resolva todos os EnigmaTchês de seis ciclos",140, true, true),
    CONDECORAÇAO_OMEGA(10, "Condecoração Omega","Resolva todos os EnigmaTchês de doze ciclos",300, true, true),
    QUEM_INDICA_AMIGO_E(13, "Quem indica amigo é","Indique a paranoia para um novo usuário",100, true, true),
    ESQUADRAO_VENCEDOR(15, "Esquadrão vencedor","Escape de uma sala da Paranoia",150, true, true),
    SUPER_ESQUADRAO(16, "Super esquadrão","Escape de duas sala da Paranoia",250, true, true),
    JOGADOR(17, "Jogador","Jogue um escape na paranoia",100, true, true),
    NAO_PRECISO_DISSO(21, "Não preciso disso","Acerte um EnigmaTchê sem usar dicas",110, true, true),
    PARANOICO(22, "Paranoico","Conclua uma reserva",30, true, false),
    FLASH(23, "Flash","Seja o primeiro a responder o EnigmaTchê",100, true, false),
    VENCEDOR_HOME_OFFICE(24, "Vencedor home office","Vença um Escape de Mesa",100, true, false),
    MESTRE_HOME_OFFICE(25, "Mestre home office","Vença um Escape de Mesa sem usar dicas",100, true, false),
    A_INTELIGENCIA_SERVE_PRA_ISSO(26, "A inteligência serve para isso","Peça uma dica no Escape de Mesa.",100, true, false),
    AGENTE_HOME_OFFICE(27, "Agente home office", "Jogue um Escape de Mesa",100, true, false);
    //INSONIA("Acesse o Site entre 3 e 4 da manhã",10, false, false),
    //MEMBRO_DA_RESISTÊNCIA("Fique conectado por 10 horas",10, false, false),
    //LENDO_NAS_ENTRE_LINHAS("Encontre o Código no Contrato",0, false, false),
    //QUERO_UMA_MEDALHA("Clicar em todas as medalhas do site",25, false, false),

    final Integer identificadorBaseAntiga;
    final String nome;
    final String descricao;
    final int experiencia;
    final Boolean mostrarDescricao;
    final Boolean mostrarMedalha;

    public static Set<Medalhas> getByNames(List<String> medalhas) {
        return CollectionUtils.emptyIfNull(medalhas).stream()
                .map(Medalhas::valueOf)
                .collect(Collectors.toSet());
    }

    public static Set<Medalhas> getByIdentificadorBaseAntiga(List<Integer> identificador) {
        return Stream.of(values())
                .filter(medalha -> identificador.contains(medalha.identificadorBaseAntiga))
                .collect(Collectors.toSet());
    }
}