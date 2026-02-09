package com.can.buyerApp.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MotorOnInitRequest {
	private Context context;
    private Message message;

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Context {
        private String action;
        
        @JsonProperty("bap_id")
        private String bap_id;
        
        @JsonProperty("bap_uri")
        private String bap_uri;
        
        @JsonProperty("bpp_id")
        private String bpp_id;
        
        @JsonProperty("bpp_uri")
        private String bpp_uri;
        
        private String domain;
        private Location location;
        
        @JsonProperty("message_id")
        private String message_id;
        
        private String timestamp;
        
        @JsonProperty("transaction_id")
        private String transaction_id;
        
        private String ttl;
        private String version;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Location {
            private Country country;
            private City city;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Country {
                private String code;
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class City {
                private String code;
            }
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Message {
        private Order order;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        public static class Order {
            private List<Fulfillment> fulfillments;
            private List<Item> items;
            private List<Payment> payments;
            private Provider provider;
            private Quote quote;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Fulfillment {
                private Customer customer;
                private String id;
                private String type;
                private State state;

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Customer {
                    private Contact contact;
                    private Person person;

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class Contact {
                        private String email;
                        private String phone;
                    }

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class Person {
                        private String name;
                    }
                }

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class State {
                    private Descriptor descriptor;

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class Descriptor {
                        private String name;
                        private String code;
                    }
                }
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Item {
                @JsonProperty("fulfillment_ids")
                private List<String> fulfillment_ids;
                
                private String id;
                
                @JsonProperty("parent_item_id")
                private String parent_item_id;
                
                @JsonProperty("category_ids")
                private List<String> category_ids;
                
                private Descriptor descriptor;
                private Price price;
                private Time time;
                
                @JsonProperty("add_ons")
                private List<AddOn> add_ons;
                
                private XInput xinput;
                private List<Tag> tags;

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Descriptor {
                    private String name;
                    
                    @JsonProperty("short_desc")
                    private String short_desc;
                    
                    private List<Image> images;
                    private String code;

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class Image {
                        private String url;
                        
                        @JsonProperty("size_type")
                        private String size_type;
                    }
                }

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Price {
                    private String value;
                    private String currency;
                }

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Time {
                    private String duration;
                    private String label;
                }

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class AddOn {
                    private String id;
                    private Price price;
                    private Quantity quantity;
                    private Descriptor descriptor;
                    private List<Tag> tags; // Only in Motor 2.1

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class Quantity {
                        private Selected selected;

                        @Data
                        @JsonIgnoreProperties(ignoreUnknown = true)
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        public static class Selected {
                            private Integer count;
                        }
                    }
                }

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class XInput {
                    private Head head;
                    private Form form;
                    private Boolean required;

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class Head {
                        private Descriptor descriptor;
                        private Index index;
                        private List<String> headings;

                        @Data
                        @JsonIgnoreProperties(ignoreUnknown = true)
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        public static class Descriptor {
                            private String name;
                        }

                        @Data
                        @JsonIgnoreProperties(ignoreUnknown = true)
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        public static class Index {
                            private Integer min;
                            private Integer cur;
                            private Integer max;
                        }
                    }

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class Form {
                        private String id;
                        
                        @JsonProperty("mime_type")
                        private String mime_type;
                        
                        private String url;
                        private Boolean resubmit;
                        
                        @JsonProperty("multiple_sumbissions")
                        private Boolean multiple_sumbissions;
                    }
                }

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Tag {
                    private Descriptor descriptor;
                    private List<TagValue> list;

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class Descriptor {
                        private String name;
                        private String code;
                        
                        @JsonProperty("short_desc")
                        private String short_desc;
                    }

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class TagValue {
                        private Descriptor descriptor;
                        private String value;
                    }
                }
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Payment {
                @JsonProperty("collected_by")
                private String collected_by;
                
                private String url; // Only in Motor 2.0
                private Params params;
                private String status;
                private String type;
                private List<Tag> tags;

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Params {
                    private String amount;
                    
                    @JsonProperty("bank_account_number")
                    private String bank_account_number;
                    
                    @JsonProperty("bank_code")
                    private String bank_code;
                    
                    private String currency;
                }

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Tag {
                    private Descriptor descriptor;
                    private Boolean display;
                    private List<TagValue> list;

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class Descriptor {
                        private String code;
                    }

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class TagValue {
                        private Descriptor descriptor;
                        private String value;
                    }
                }
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Provider {
                private String id;
                private Descriptor descriptor;

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Descriptor {
                    @JsonProperty("long_desc")
                    private String long_desc;
                    
                    private String name;
                    
                    @JsonProperty("short_desc")
                    private String short_desc;
                    
                    private List<Image> images;

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class Image {
                        private String url;
                    }
                }
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Quote {
                private String id;
                private Price price;
                private List<Breakup> breakup;
                private String ttl;

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Price {
                    private String currency;
                    private String value;
                }

                @Data
                @JsonIgnoreProperties(ignoreUnknown = true)
                @JsonInclude(JsonInclude.Include.NON_NULL)
                public static class Breakup {
                    private Price price;
                    private String title;
                    private Item item;

                    @Data
                    @JsonIgnoreProperties(ignoreUnknown = true)
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class Item {
                        private String id;
                    }
                }
            }
        }
    }

}
