package com.can.buyerApp.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
public class OnIssueRequest {

    private Context context;
    private Message message;

    @Data
    public static class Context {
        private String domain;
        private Location location;
        private String action;
        private String version;
        private String bap_id;
        private String bap_uri;
        private String bpp_id;
        private String bpp_uri;
        private String transaction_id;
        private String message_id;
        private String timestamp;
        private String ttl;
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
        private Issue issue;
    }

    @Data
    public static class Issue {
        private String id;
        private IssueActions issue_actions;
        private String created_at;
        private String updated_at;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private ResolutionProvider resolution_provider;
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Resolution resolution;
    }

    @Data
    public static class IssueActions {
        private List<RespondentAction> respondent_actions;
    }

    @Data
    public static class RespondentAction {
        private String respondent_action;
        private String short_desc;
        private String updated_at;
        private UpdatedBy updated_by;
        private int cascaded_level;
    }

    @Data
    public static class UpdatedBy {
        private Org org;
        private Contact contact;
        private Person person;
    }

    @Data
    public static class Org {
        private String name;
    }

    @Data
    public static class Contact {
        private String phone;
        private String email;
    }

    @Data
    public static class Person {
        private String name;
    }

    @Data
    public static class ResolutionProvider {
        private RespondentInfo respondent_info;

    }

    @Data
    public static class RespondentInfo {
        private String type;
        private Organization organization;
        private ResolutionSupport resolution_support;
    }

    @Data
    public static class Organization {
        private Org org;
        private Contact contact;
        private Person person;
    }

    @Data
    public static class ResolutionSupport {
        private String chat_link;
        private Contact contact;
        private List<Gros> gros;
    }

    @Data
    public static class Gros {
        private Person person;
        private Contact contact;
        private String gro_type;
    }

    @Data
    public static class Resolution {
        private String short_desc;
        private String long_desc;
        private String action_triggered;
        private double refund_amount;
    }


}
