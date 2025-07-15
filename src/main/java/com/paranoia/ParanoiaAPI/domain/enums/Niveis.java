package com.paranoia.ParanoiaAPI.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Niveis {
    NIVEL_1(1,100,25, null),
    NIVEL_2(2,250,25, null),
    NIVEL_3(3,450,25, null),
    NIVEL_4(4,700,25, null),
    NIVEL_5(5,1000,25, null),
    NIVEL_6(6,1350,30, null),
    NIVEL_7(7,1750,30, null),
    NIVEL_8(8,2200,30, null),
    NIVEL_9(9,2700,30, null),
    NIVEL_10(10,3250,30, null),
    NIVEL_11(11,3850,35, null),
    NIVEL_12(12,4500,35, null),
    NIVEL_13(13,5200,35, null),
    NIVEL_14(14,5950,35, null),
    NIVEL_15(15,6750,35, null),
    NIVEL_16(16,7600,40, null),
    NIVEL_17(17,8500,40, null),
    NIVEL_18(18,9450,40, null),
    NIVEL_19(19,10450,40, null),
    NIVEL_20(20,11500,40, null);

    final int nivel;
    final int expProximoNivel;
    final int bitNoias;
    private Niveis proximoNivel;

    static {
        NIVEL_1.proximoNivel = NIVEL_2;
        NIVEL_2.proximoNivel = NIVEL_3;
        NIVEL_3.proximoNivel = NIVEL_4;
        NIVEL_4.proximoNivel = NIVEL_5;
        NIVEL_5.proximoNivel = NIVEL_6;
        NIVEL_6.proximoNivel = NIVEL_7;
        NIVEL_7.proximoNivel = NIVEL_8;
        NIVEL_8.proximoNivel = NIVEL_9;
        NIVEL_9.proximoNivel = NIVEL_10;
        NIVEL_10.proximoNivel = NIVEL_11;
        NIVEL_11.proximoNivel = NIVEL_12;
        NIVEL_12.proximoNivel = NIVEL_13;
        NIVEL_13.proximoNivel = NIVEL_14;
        NIVEL_14.proximoNivel = NIVEL_15;
        NIVEL_15.proximoNivel = NIVEL_16;
        NIVEL_16.proximoNivel = NIVEL_17;
        NIVEL_17.proximoNivel = NIVEL_18;
        NIVEL_18.proximoNivel = NIVEL_19;
        NIVEL_19.proximoNivel = NIVEL_20;
    }

    public Niveis proximoNivel(){
        return this.proximoNivel;
    }
}
