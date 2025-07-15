package com.paranoia.ParanoiaAPI.repository;

import com.paranoia.ParanoiaAPI.domain.Venda;
import com.paranoia.ParanoiaAPI.domain.VendaItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VendaItemRepository extends JpaRepository<VendaItem, UUID> {
    List<VendaItem> findByVenda(Venda venda);
}
