package com.can.buyerApp.request;

import lombok.Data;

import java.util.List;

@Data
public class SelectRequest {

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
            }

            @Data
            public static class Provider {
                private String id;
            }
        }
    }
}
