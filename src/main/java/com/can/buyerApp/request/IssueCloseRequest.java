package com.can.buyerApp.request;

import lombok.Data;

import java.util.List;

@Data
public class IssueCloseRequest {

//    private Context context;
//    private Message message;
//
//    @Data
//    public static class Context {
//        private String domain;
//        private Location location;
//        private String action;
//        private String version;
//        private String bap_uri;
//        private String bap_id;
//        private String bpp_id;
//        private String bpp_uri;
//        private String transaction_id;
//        private String ttl;
//        private String message_id;
//        private String timestamp;
//    }
//
//    @Data
//    public static class Location {
//        private Country country;
//        private City city;
//    }
//
//    @Data
//    public static class Country {
//        private String code;
//    }
//
//    @Data
//    public static class City {
//        private String code;
//    }
//
//    @Data
//    public static class Message {
//        private String id;
//        private String status;
//    }


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
        private Issue issue;

        @Data
        public static class Issue {
            private String id;
            private String created_at;
            private String updated_at;
            private String status;
            private String rating;
            private IssueActions issue_actions;

            @Data
            public static class IssueActions {
                private List<ComplainantAction> complainant_actions;

                @Data
                public static class ComplainantAction {
                    private String complainant_action;
                    private String short_desc;
                    private String updated_at;
                    private UpdatedBy updated_by;

                    @Data
                    public static class UpdatedBy {
                        private Org org;
                        private Contact contact;
                        private Person person;

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
                    }
                }
            }
        }
    }
}
