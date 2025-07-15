package com.paranoia.ParanoiaAPI.dto.MercadoPago;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class DataPayloadDTO {
    private String id;

    public DataPayloadDTO(Map<String, Object> data) {
        this.id = (String) data.get("id");
    }
}
