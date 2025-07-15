package com.paranoia.ParanoiaAPI.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.paranoia.ParanoiaAPI.domain.enums.HistoricoAcoes;
import com.paranoia.ParanoiaAPI.exception.ParanoiaException;
import com.paranoia.ParanoiaAPI.helpers.ProdutoHelperInterface;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class JSONUtils {
    public static <T> List<T> lerArquivoDicasJSON(final String arquivo, final Class<T> clazz) {
        ClassLoader classLoader = ProdutoHelperInterface.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(arquivo);

        if (inputStream != null) {
            try {
                String jsonTexto = IOUtils.toString(inputStream, "UTF-8");
                ObjectMapper objectMapper = new ObjectMapper();

                // Create an ObjectReader for the specified class
                ObjectReader reader = objectMapper.readerFor(clazz);

                // Use readValues to read the JSON into an iterator
                Iterator<T> iterator = reader.readValues(jsonTexto);

                // Collect the results into a list
                List<T> resultList = new ArrayList<>();
                iterator.forEachRemaining(resultList::add);

                return resultList;
            } catch (Exception e) {
                throw new ParanoiaException(HttpStatus.INTERNAL_SERVER_ERROR, HistoricoAcoes.LER_ARQUIVO_DICAS_ERROR, e);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        } else {
            return Collections.emptyList();
        }
    }
}
