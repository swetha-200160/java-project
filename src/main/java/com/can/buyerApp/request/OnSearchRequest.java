package com.can.buyerApp.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
public class OnSearchRequest {

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
        private Catalog catalog;

        @Data
        public static class Catalog {
            private Descriptor descriptor;
            private List<Provider> providers;

            @Data
            public static class Descriptor {
                private String name;
            }

            @Data
            public static class Provider {
                private List<Category> categories;
                private ProviderDescriptor descriptor; // Rename this to avoid conflict
                private String id;
                private List<Item> items;
                private List<Payment> payments;


                @Data
                public static class Category {
                    private CategoryDescriptor descriptor; // Rename this to avoid conflict
                    private String id;


                    @Data
                    public static class CategoryDescriptor { // Rename this to avoid conflict
                        private String name;
                        private String code;
                    }
                }


                @Data
                public static class ProviderDescriptor { // Rename this to avoid conflict
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


                @Data
                public static class Item {
                    private List<String> category_ids;
                    private ItemDescriptor descriptor; // Rename this to avoid conflict
                    private String id;
                    private List<Tag> tags;
                    private Time time;
                    private XInput xinput;
                    private List<AddOn> add_ons;
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    private Price price;
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    private String parent_item_id;

                    // Default constructor
                    public Item() {}

                    @Data
                    public static class ItemDescriptor { // Rename this to avoid conflict
                        private String name;
                        private String short_desc;
                    }


                    @Data
                    public static class Tag {
                        private TagDescriptor descriptor; // Rename this to avoid conflict
                        private List<Detail> list;


                        @Data
                        public static class TagDescriptor { // Rename this to avoid conflict
                            private String name;
                            private String code;
                        }


                        @Data
                        public static class Detail {
                            private DetailDescriptor descriptor; // Rename this to avoid conflict
                            private String value;


                            @Data
                            public static class DetailDescriptor { // Rename this to avoid conflict
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
                            private HeadDescriptor descriptor; // Rename this to avoid conflict
                            private Index index;
                            private List<String> headings;


                            @Data
                            public static class HeadDescriptor { // Rename this to avoid conflict
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
                            private boolean multiple_submissions; // Fix typo from `multiple_sumbissions`
                        }
                    }


                    @Data
                    public static class AddOn {
                        private String id;
                        private Quantity quantity;
                        private AddOnDescriptor descriptor; // Rename this to avoid conflict
                        private Price price;


                        @Data
                        public static class Quantity {
                            private Available available;


                            @Data
                            public static class Available {
                                private int count;
                            }
                        }


                        @Data
                        public static class AddOnDescriptor { // Rename this to avoid conflict
                            private String name;
                            private String code;
                        }


                        @Data
                        public static class Price {
                            private String value;
                            private String currency;
                        }
                    }

                    @Data
                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    public static class Price {
                        private String currency;
                        private String value;

                    }
                }


                @Data
                public static class Payment {
                    private String collected_by;
                    private List<PaymentTag> tags; // Rename to avoid conflict


                    @Data
                    public static class PaymentTag { // Rename to avoid conflict
                        private PaymentTagDescriptor descriptor; // Rename to avoid conflict
                        private boolean display;
                        private List<Detail> list;


                        @Data
                        public static class PaymentTagDescriptor { // Rename to avoid conflict
                            private String code;
                        }


                        @Data
                        public static class Detail {
                            private DetailDescriptor descriptor; // Rename this to avoid conflict
                            private String value;


                            @Data
                            public static class DetailDescriptor { // Rename to avoid conflict
                                private String code;
                            }
                        }
                    }
                }
            }
        }
    }

}
