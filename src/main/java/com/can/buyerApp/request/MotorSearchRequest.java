package com.can.buyerApp.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class MotorSearchRequest {

    private Context context;
    private Message message;

    @Data
    public static class Context {
        private String action;
        private String bap_id;
        private String bap_uri;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String bpp_id;
        @JsonInclude(JsonInclude.Include.NON_NULL)
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
        private Intent intent;

        @Data
        public static class Intent {
            private Category category;
            @JsonInclude(JsonInclude.Include.NON_NULL)
            private Provider provider;
            private Payment payment;

            @JsonInclude(JsonInclude.Include.NON_NULL)
            private Fulfillment fulfillment;

            @Data
            public static class Category {
                private Descriptor descriptor;

                @Data
                public static class Descriptor {
                    private String code;
                }
            }

            @Data
            public static class Provider {
                private String id;
                private List<Item> items;

                @Data
                public static class Item {
                    private String id;
                    private XInput xinput;

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
            }

            @Data
            public static class Payment {
                private String collected_by;
                private List<Tag> tags;

                @Data
                public static class Tag {
                    private Descriptor descriptor;
                    private boolean display;
                    private List<TagListItem> list;

                    @Data
                    public static class Descriptor {
                        private String code;
                    }

                    @Data
                    public static class TagListItem {
                        private Descriptor descriptor;
                        private String value;
                    }
                }
            }

            @Data
            @JsonInclude(JsonInclude.Include.NON_NULL)
            public static class Fulfillment {
                @JsonInclude(JsonInclude.Include.NON_NULL)
                private Customer customer;
                @JsonInclude(JsonInclude.Include.NON_NULL)
                private Agent agent;

                @Data
                public static class Customer {
                    private Person person;

                    @Data
                    public static class Person {
                        private String id;
                        private String name;
                    }
                }

                @Data
                public static class Agent {
                    private Person person;

                    @Data
                    public static class Person {
                        private String id;
                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        private String name;
                    }
                }
            }
        }
    }
}