package com.can.buyerApp.request;

import lombok.Data;

import java.util.List;

@Data
public class MotorUpdateRequest{

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
        private String update_target;
        private Order order;

        // ================= ORDER =================
        @Data
        public static class Order {
            private String id;
            private List<Fulfillment> fulfillments;

            // ================= FULFILLMENT =================
            @Data
            public static class Fulfillment {
                private Customer customer;

                @Data
                public static class Customer {
                    private Contact contact;

                    @Data
                    public static class Contact {
                        private String phone;
                    }
                }
            }
        }
    }
}
