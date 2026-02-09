package com.can.buyerApp.request;

import lombok.Data;

@Data
public class IssueStatusRequest {
    private Context context;
    private Message message;

    @Data
    public static class Context {
        private String domain;
        private Location location;
        private String action;
        private String version;
        private String bap_uri;
        private String bap_id;
        private String bpp_id;
        private String bpp_uri;
        private String transaction_id;
        private String ttl;
        private String message_id;
        private String timestamp;
    }

    @Data
    public static class Location {
        private Country country;
        private City city;
    }

    @Data
    public static class Country {
        private String code;
    }

    @Data
    public static class City {
        private String code;
    }

    @Data
    public static class Message {
        private String issue_id;
    }

}
