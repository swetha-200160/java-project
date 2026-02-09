package com.can.buyerApp.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class SearchCatalog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String transactionId;

    @Column(nullable = false)
    private String messageId;

    @Column(name = "item_id")
    private String itemId;

    private String parentItemId;

    private String itemName;
    private String price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "searchCatalog", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<GeneralInformation> generalInformationList = new ArrayList<>();

    @OneToMany(mappedBy = "searchCatalog", cascade = CascadeType.ALL, orphanRemoval = true)
   @JsonManagedReference
    private List<AddonDetail> addonDetails = new ArrayList<>();
}
