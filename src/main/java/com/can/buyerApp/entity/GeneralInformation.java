package com.can.buyerApp.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class GeneralInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String value;

    @ManyToOne
    @JoinColumn(name = "item_id")
     @JsonBackReference
    private SearchCatalog searchCatalog;
}
