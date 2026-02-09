package com.can.buyerApp.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class OnselectRequest {
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
            private Quote quote;

            @Data
            public static class Item {
                private String id;
                private String parent_item_id;
                private List<String> category_ids;
                private Descriptor descriptor;
                private Price price;
                private List<Tag> tags;
                private Time time;
                private XInput xinput;
                private List<AddOn> add_ons;

                @Data
                public static class Descriptor {
                    private String name;
                    private String short_desc;
                }

                @Data
                public static class Price {
                    private String currency;
                    private String value;
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
                        private Descriptor descriptor;  // Add this line

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

                        @AllArgsConstructor
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
                        private boolean multiple_sumbissions;
                        private String url;
                        private boolean resubmit;
                    }
                }

                @Data
                public static class AddOn {
                    private String id;
                    private Quantity quantity;
                    private Descriptor descriptor;
                    private Price price;

                    @Data
                    public static class Descriptor {
                        private String name;
                        private String code;
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
            }

            @Data
            public static class Provider {
                private Descriptor descriptor;
                private String id;

                @Data
                public static class Descriptor {
                    private String name;
                    private String short_desc;
                    private String long_desc;
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
                private Item.Price price;
                private String ttl;

                @Data
                public static class Breakup {
                    private Order.Item.Price price;
                    private String title;
                    @JsonInclude(JsonInclude.Include.NON_NULL)
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
            }
        }
    }
}
