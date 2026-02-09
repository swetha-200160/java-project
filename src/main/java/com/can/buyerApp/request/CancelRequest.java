package com.can.buyerApp.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelRequest {

    private Context context;
    private Message message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
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
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Location {
            private Country country;
            private City city;

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Country {
                private String code;
            }

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class City {
                private String code;
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String cancellation_reason_id;
        private Descriptor descriptor;
        private String order_id;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Descriptor {
            private String short_desc;
        }
    }
}
