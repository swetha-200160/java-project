package com.can.buyerApp.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class OnCancelRequest {

    private Context context;
    private Message message;

    @Data
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

    @Data
    public static class Message {
        private Order order;

        @Data
        public static class Order {
            private List<Fulfillment> fulfillments;
            private List<Item> items;
            private String id;
            private List<Payment> payments;
            private Provider provider;
            private Quote quote;
            private String status;
            private List<Document> documents;
            private String created_at;
            private String updated_at;
            private List<CancellationTerm> cancellation_terms;

            @Data
            public static class Fulfillment {
                private State state;
                private Customer customer;
                private String id;
                private String type;

                @Data
                public static class State {
                    private Descriptor descriptor;

                    @Data
                    public static class Descriptor {
                        private String code;

                        @JsonCreator
                        public Descriptor(@JsonProperty("code") String code) {
                            this.code = code;
                        }
                    }
                }

                @Data
                public static class Customer {
                    private Contact contact;

                    @Data
                    public static class Contact {
                        private String email;
                        private String phone;
                    }
                }
            }

            @Data
            public static class Item {
                private List<AddOn> add_ons;
                private Descriptor descriptor;
                private String id;
                private String parent_item_id;
                private Price price;
                private List<String> fulfillment_ids;
                private List<Tag> tags;
                private Time time;

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @Data
                public static class AddOn {
                    private String id;
                    private Descriptor descriptor;
                    private Quantity quantity;
                    private Price price;

                    @Data
                    public static class Descriptor {
                        private String code;
                        private String name;

                        @JsonCreator
                        public Descriptor(@JsonProperty("code") String code, @JsonProperty("name") String name) {
                            this.code = code;
                            this.name = name;
                        }
                    }

                    @Data
                    public static class Quantity {
                        private Selected selected;

                        @Data
                        public static class Selected {
                            private int count;
                        }
                    }
                }
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @Data
                public static class Descriptor {
                    private String name;
                    private String short_desc;

                    @JsonCreator
                    public Descriptor(@JsonProperty("name") String name, @JsonProperty("short_desc") String short_desc) {
                        this.name = name;
                        this.short_desc = short_desc;
                    }
                }

                @Data
                public static class Price {
                    private String currency;
                    private String value;

                    @JsonCreator
                    public Price(@JsonProperty("value") String value, @JsonProperty("currency") String currency) {
                        this.value = value;
                        this.currency = currency;
                    }
                }
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @Data
                public static class Tag {
                    private Descriptor descriptor;
                    private List<ItemValue> list;
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @Data
                    public static class Descriptor {
                        private String code;
                        private String name;
                        @JsonCreator
                        public Descriptor(@JsonProperty("code") String code, @JsonProperty("name") String name) {
                            this.code = code;
                            this.name = name;

                        }
                    }
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @Data
                    public static class ItemValue {
                        private Descriptor descriptor;
                        private String value;

                        @JsonCreator
                        public ItemValue(@JsonProperty("descriptor") Descriptor descriptor, @JsonProperty("value") String value) {
                            this.descriptor = descriptor;
                            this.value = value;
                        }
                    }
                }

                @Data
                public static class Time {
                    private String duration;
                    private String label;
                }
            }

            @Data
            public static class Payment {
                private String collected_by;
                private Params params;
                private String status;
                private String type;
                private List<Payment.Tag> tags;

                @Data
                public static class Params {
                    private String amount;
                    private String bank_account_number;
                    private String bank_code;
                    private String currency;
                    private String transaction_id;
                }

                @Data
                public static class Tag {
                    private Descriptor descriptor;
                    private boolean display;
                    private List<SubTag> list;

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @Data
                    public static class Descriptor {
                        private String code;

                        @JsonCreator
                        public Descriptor(@JsonProperty("code") String code) {
                            this.code = code;
                        }
                    }
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @Data
                    public static class SubTag {
                        private Descriptor descriptor;
                        private String value;

                        @JsonCreator
                        public SubTag(@JsonProperty("descriptor") Descriptor descriptor, @JsonProperty("value") String value) {
                            this.descriptor = descriptor;
                            this.value = value;
                        }
                    }
                }
            }

            @Data
            public static class Provider {
                private Descriptor descriptor;
                private String id;

                @Data
                public static class Descriptor {
                    private String long_desc;
                    private String name;
                    private String short_desc;
                    private List<Image> images;

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @Data
                    public static class Image {
                        private String url;
                        private String size_type;
                    }
                }
            }

            @Data
            public static class Quote {
                private List<Breakup> breakup;
                private String id;
                private Price price;
                private String ttl;
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @Data
                public static class Breakup {
                    private Price price;
                    private String title;

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    private Item item;

                    @Data
                    public static class Item {
                        private String id;
                        private List<Item.AddOn> add_ons;

                        @Data
                        public static class AddOn {
                            private String id;
                        }
                    }
                }

                @Data
                public static class Price {
                    private String currency;
                    private String value;

                    @JsonCreator
                    public Price(@JsonProperty("value") String value, @JsonProperty("currency") String currency) {
                        this.value = value;
                        this.currency = currency;
                    }
                }
            }

            @Data
            public static class Document {
                private Descriptor descriptor;
                private String mime_type;
                private String url;
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @Data
                public static class Descriptor {
                    private String code;
                    private String name;
                    private String short_desc;
                    private String long_desc;

                    @JsonCreator
                    public Descriptor(@JsonProperty("code") String code, @JsonProperty("name") String name,
                                      @JsonProperty("short_desc") String short_desc,
                                      @JsonProperty("long_desc") String long_desc) {
                        this.code = code;
                        this.name = name;
                        this.short_desc = short_desc;
                        this.long_desc = long_desc;
                    }
                }
            }

            @Data
            public static class CancellationTerm {
                private ExternalRef external_ref;
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @Data
                public static class ExternalRef {
                    private String mimetype;
                    private String url;
                }
            }
        }
    }
}
