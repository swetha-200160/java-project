package com.can.buyerApp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AddonDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String addonId;
    private String addonName;
    private String addonPrice;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @JsonBackReference
    private SearchCatalog searchCatalog;
}
