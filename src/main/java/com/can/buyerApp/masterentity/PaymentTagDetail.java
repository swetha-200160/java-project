package com.can.buyerApp.masterentity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PaymentTagDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descriptorType;
    private String value;

    @ManyToOne
    @JoinColumn(name = "payment_tag_id")
    private PaymentTag paymentTag;
}
