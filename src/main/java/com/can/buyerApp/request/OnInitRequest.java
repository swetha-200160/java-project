package com.can.buyerApp.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
public class OnInitRequest {

    private Context context;
    private Message message;

    // ================= CONTEXT =================
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

    // ================= MESSAGE =================
    @Data
    public static class Message {
        private Order order;

        @Data
        public static class Order {

            private List<Fulfillment> fulfillments;
            private List<Item> items;
            private List<Payment> payments;
            private Provider provider;
            private Quote quote;

            // ================= FULFILLMENT =================
            @Data
            public static class Fulfillment {
                private Customer customer;
                private String id;
                private String type;
                private State state;

                @Data
                public static class State {
                    private Descriptor descriptor;

                    @Data
                    public static class Descriptor {
                        private String name;
                        private String code;
                    }
                }

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

            // ================= ITEM =================
            @Data
            public static class Item {
                private String id;
                private String parent_item_id;
                private List<String> fulfillment_ids;
                private List<String> category_ids;
                private Descriptor descriptor;
                private Price price;
                private Time time;
                private List<AddOn> add_ons;
                private XInput xinput;
                private List<Tag> tags;

                @Data
                public static class Descriptor {
                    private String name;
                    private String short_desc;
                    private List<Image> images;

                    @Data
                    public static class Image {
                        private String url;
                        private String size_type;
                    }
                }

                @Data
                public static class Price {
                    private String currency;
                    private String value;
                }

                @Data
                public static class Time {
                    private String duration;
                    private String label;
                }

                @Data
                public static class AddOn {
                    private String id;
                    private Price price;
                    private Quantity quantity;
                    private Descriptor descriptor;

                    @Data
                    public static class Price {
                        private String currency;
                        private String value;
                    }

                    @Data
                    public static class Quantity {
                        private Selected selected;

                        @Data
                        public static class Selected {
                            private int count;
                        }
                    }

                    @Data
                    public static class Descriptor {
                        private String name;
                        private String code;
                    }
                }

                @Data
                public static class XInput {
                    private Head head;
                    private Form form;
                    private boolean required;

                    @Data
                    public static class Head {
                        private Descriptor descriptor;
                        private Index index;
                        private List<String> headings;

                        @Data
                        public static class Descriptor {
                            private String name;
                        }

                        @Data
                        public static class Index {
                            private int min;
                            private int cur;
                            private int max;
                        }
                    }

                    @Data
                    public static class Form {
                        private String id;
                        private String mime_type;
                        private String url;
                        private boolean resubmit;
                        private boolean multiple_sumbissions;
                    }
                }

                @Data
                public static class Tag {
                    private Descriptor descriptor;
                    private List<Value> list;

                    @Data
                    public static class Descriptor {

                        private String name;
                        private String code;
                    }

                    @Data
                    public static class Value {
                        private String value;
                        private Descriptor descriptor;

                        @Data
                        public static class Descriptor {
                            private String code;
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            private String name;
                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            private String short_desc;
                        }
                    }
                }
            }

            // ================= PAYMENT =================
            @Data
            public static class Payment {
                private String collected_by;
                private String url;
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
                    private List<ListItem> list;

                    @Data
                    public static class Descriptor {
                        private String code;
                    }

                    @Data
                    public static class ListItem {
                        private Descriptor descriptor;
                        private String value;
                    }
                }
            }

            // ================= PROVIDER =================
            @Data
            public static class Provider {
                private String id;
                private Descriptor descriptor;

                @Data
                public static class Descriptor {
                    private String long_desc;
                    private String name;
                    private String short_desc;
                    private List<Image> images;

                    @Data
                    public static class Image {
                        private String url;
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        private String size_type;
                    }
                }
            }

            // ================= QUOTE =================
            @Data
            public static class Quote {
                private String id;
                private Price price;
                private List<Breakup> breakup;
                private String ttl;

                @Data
                public static class Price {
                    private String currency;
                    private String value;
                }

                @Data
                public static class Breakup {
                    private String title;
                    private Price price;
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    private Item item;

                    @Data
                    public static class Price {
                        private String currency;
                        private String value;
                    }

                    @Data
                    public static class Item {
                        private String id;
                    }
                }
            }
        }
    }
}
