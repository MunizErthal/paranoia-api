package com.paranoia.ParanoiaAPI.factory;

import com.paranoia.ParanoiaAPI.domain.enums.Produtos;
import com.paranoia.ParanoiaAPI.helpers.ProdutoHelperInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ProdutoHelperFactory {

    @Autowired
    ApplicationContext context;

    public ProdutoHelperInterface obterHelper(Produtos produto){
        return context.getBean(produto.getHelper());
    }
}