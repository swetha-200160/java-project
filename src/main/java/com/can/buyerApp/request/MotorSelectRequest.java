package com.can.buyerApp.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
public class MotorSelectRequest {

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
            private List<Item> items;
            private Provider provider;

            @Data
            public static class Item {
                private String id;
                private String parent_item_id;
                private List<AddOn> add_ons;
                @JsonInclude(JsonInclude.Include.NON_NULL)
                private List<Tag> tags;
                @JsonInclude(JsonInclude.Include.NON_NULL)
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
                public static class Tag {
                    private Descriptor descriptor;
                    private List<TagValue> list;

                    @Data
                    public static class Descriptor {
                        private String code;
                        private String name;
                    }

                    @Data
                    public static class TagValue {
                        private Descriptor descriptor;
                        private String value;

                        @Data
                        public static class Descriptor {
                            private String code;
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



            @Data
            public static class Provider {
                private String id;
            }

        }
    }
}