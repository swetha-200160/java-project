package com.can.buyerApp.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class OnUpdateRequest {

    public Context context;
    public Message message;


    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Context {
        private String action;
        private String bap_id;
        private String bap_uri;
        private String bpp_id;
        private String bpp_uri;
        private String domain;
        private Location location;
        private String message_id;
        private String timestamp;
        private String transaction_id;
        private String ttl;
        private String version;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Location {
        private Country country;
        private City city;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Country {
        private String code;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class City {
        private String code;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Message {
        private Order order;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Order {
        private List<Fulfillment> fulfillments;
        private List<Item> items;
        private String id;
        private List<Payment> payments;
        private Provider provider;
        private Quote quote;
        private String status;
        private List<Document> documents;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String created_at;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String updated_at;
        private List<CancellationTerm> cancellation_terms;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Fulfillment {
        private State state;
        private Customer customer;
        private String id;
        private String type;
        private List<Tag> tags;

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class State {
        private Descriptor descriptor;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Descriptor {
        private String code;
        private String name;
        private String short_desc;
        private String long_desc;
        private List<Image> images;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Image {
        private String url;
        private String size_type;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Customer {
        private Contact contact;
        private Person person;

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Contact {
        private String email;
        private String phone;
    }
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Person {
        private String name;
    }


    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Tag {
        private Descriptor descriptor;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean display;
        private List<TagList> list;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TagList {
        private Descriptor descriptor;
        private String value;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public  static class Item {
        private List<AddOn> add_ons;
        private List<String> category_ids;
        private Descriptor descriptor;
        private String id;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String parent_item_id;
        private List<String> fulfillment_ids;
        private Price price;
        private List<Tag> tags;
        private Time time;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class AddOn {
        private String id;
        private Descriptor descriptor;
        private Quantity quantity;
        private Price price;
    }


    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Quantity {
        private Selected selected;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Selected {
        private int count;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Price {
        private String value;
        private String currency;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Time {
        private String duration;
        private String label;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public  static class Payment {
        private String collected_by;
        private PaymentParams params;
        private String status;
        private String type;
        private List<Tag> tags;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static  class PaymentParams {
        private String amount;
        private String bank_account_number;
        private String bank_code;
        private String currency;
        private String transaction_id;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Provider {
        private Descriptor descriptor;
        private String id;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Quote {
        private String id;
        private List<Breakup> breakup;
        private Price price;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String ttl;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public  static class Breakup {
        private Price price;
        private String title;
        private Item item;

    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public  static class Document {
        private Descriptor descriptor;
        private String mime_type;
        private String url;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class CancellationTerm {
        private ExternalRef external_ref;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ExternalRef {
        private String mimetype;
        private String url;
    }
}
