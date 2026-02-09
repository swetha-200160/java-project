package com.can.buyerApp.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class PaymentOnStatus {
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
                private List<String> category_ids;
                private Price price;
                private Descriptor descriptor;
                private String id;
                private List<String> fulfillment_ids;
                private List<Tag> tags;
                private Time time;

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

                @Data
                public static class Price {
                    private String currency;
                    private String value;
                }

                @Data
                public static class Descriptor {
                    private String name;
                    private String short_desc;
                }

                @Data
                public static class Tag {
                    private Descriptor descriptor;
                    private List<ListItem> list;

                    @Data
                    public static class Descriptor {
                        private String name;
                        private String code;
                    }

                    @Data
                    public static class ListItem {
                        private Descriptor descriptor;
                        private String value;

                        @Data
                        public static class Descriptor {
                            private String code;
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
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Payment {
                private String collected_by;
                private Params params;
                private String status;
                private String type;
                private List<Tag> tags;

                @Data
                @JsonInclude(JsonInclude.Include.NON_NULL)
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
                    private List<ListItem> list;

                    @Data
                    public static class Descriptor {
                        private String code;
                    }

                    @Data
                    public static class ListItem {
                        private Descriptor descriptor;
                        private String value;

                        @Data
                        public static class Descriptor {
                            private String code;
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

                    @Data
                    public static class Image {
                        private String url;
                        private String size_type;
                    }
                }
            }

            @Data
            public static class Quote {
                private String id;
                private List<Breakup> breakup;
                private Price price;

                @Data
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Breakup {
                    private Price price;
                    private String title;
                    private Item item;

                    @Data
                    public static class Item {
                        private String id;
                        private List<AddOn> add_ons;

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
                }
            }

            @Data
            public static class Document {
                private Descriptor descriptor;
                private String mime_type;
                private String url;

                @Data
                public static class Descriptor {
                    private String code;
                    private String name;
                    private String short_desc;
                    private String long_desc;
                }
            }

            @Data
            public static class CancellationTerm {
                private ExternalRef external_ref;

                @Data
                public static class ExternalRef {
                    private String mimetype;
                    private String url;
                }
            }
        }
    }
}
