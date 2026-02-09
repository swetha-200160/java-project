package com.can.buyerApp.dto;

import com.can.buyerApp.request.AckStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Acknowledgement {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Context context;
    private Message message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Context {
        private String domain;
        private Location location;
        private String action;
        private String bap_id;
        private String bap_uri;
        private String transaction_id;
        private String message_id;
        private String timestamp;
        private String ttl;
        private String version;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Location {
            private City city;
            private Country country;

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class City {
                private String code;
            }

            @Data
            @NoArgsConstructor
            @AllArgsConstructor
            public static class Country {
                private String code;
            }
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private Ack ack;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Ack {
            private AckStatus status;
        }
    }

}
