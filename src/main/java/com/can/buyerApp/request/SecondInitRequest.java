package com.can.buyerApp.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SecondInitRequest {

    private Context context;
    private Message message;

    // ================= CONTEXT =================
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

        @Data
        public static class Location {
            private Country country;
            private City city;

            @Data
            public static class Country {
                private String code;
            }

            @Data
            public static class City {
                private String code;
            }
        }
    }

    // ================= MESSAGE =================
    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Message {
        private Order order;

        @Data
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Order {

            private List<Fulfillment> fulfillments;
            private Provider provider;
            private List<Item> items;
            private List<Payment> payments;

            // ================= FULFILLMENT =================
            @Data
            public static class Fulfillment {
                private Customer customer;
                private String id;

                @Data
                public static class Customer {
                    private Contact contact;
                    private Person person;

                    @Data
                    public static class Contact {
                        private String email;
                        private String phone;
                    }

                    @Data
                    public static class Person {
                        private String name;
                    }
                }
            }

            // ================= PROVIDER =================
            @Data
            public static class Provider {
                private String id;
            }

            // ================= ITEM =================
            @Data
            public static class Item {
                private String id;
                private String parent_item_id;
                private List<AddOn> add_ons;
                private XInput xinput;

                @Data
                public static class AddOn {
                    private String id;
                    private Quantity quantity;

                    @Data
                    public static class Quantity {
                        private Selected selected;

                        @Data
                        public static class Selected {
                            private int count;
                        }
                    }
                }

                @Data
                public static class XInput {
                    private Form form;
                    private FormResponse form_response;

                    @Data
                    public static class Form {
                        private String id;
                    }

                    @Data
                    public static class FormResponse {
                        private String status;
                        private String submission_id;
                    }
                }
            }

            // ================= PAYMENT =================
            @Data
            public static class Payment {
                private String collected_by;
                private String status;
                private String type;
                private Params params;
                private List<Tag> tags;

                @Data
                public static class Params {
                    private String amount;
                    private String bank_account_number;
                    private String bank_code;
                    private String currency;
                }

                @Data
                public static class Tag {
                    private Descriptor descriptor;
                    private Boolean display;
                    private List<Value> list;

                    @Data
                    public static class Descriptor {
                        private String code;
                    }

                    @Data
                    public static class Value {
                        private Descriptor descriptor;
                        private String value;

                        @Data
                        public static class Descriptor {
                            private String code;
                        }
                    }
                }
            }
        }
    }
}
