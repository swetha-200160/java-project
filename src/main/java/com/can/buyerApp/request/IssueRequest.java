package com.can.buyerApp.request;
import lombok.Data;
import java.util.List;

@Data
public class IssueRequest {

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
            private String category;
            private String sub_category;
            private ComplainantInfo complainant_info;
            private OrderDetails order_details;
            private Description description;
            private Source source;
            private ExpectedResponseTime expected_response_time;
            private ExpectedResolutionTime expected_resolution_time;
            private String status;
            private String issue_type;
            private IssueActions issue_actions;
            private String created_at;
            private String updated_at;

            @Data
            public static class ComplainantInfo {
                private Contact contact;
                private Person person;

                @Data
                public static class Contact {
                    private String email;
                    private String phone;
                }

                @Data
                public static class Person {
                    private String name;
                }
            }

            @Data
            public static class OrderDetails {
                private String id;
                private String state;
                private String provider_id;
                private List<Fulfillment> fulfillments;
                private List<Item> items;

                @Data
                public static class Fulfillment {
                    private String id;
                    private String state;
                }

                @Data
                public static class Item {
                    private String id;
                }
            }

            @Data
            public static class Description {
                private String short_desc;
                private String long_desc;
                private AdditionalDesc additional_desc;
                private List<String> images;

                @Data
                public static class AdditionalDesc {
                    private String url;
                    private String content_type;
                }
            }

            @Data
            public static class Source {
                private String network_participant_id;
                private String type;
            }

            @Data
            public static class ExpectedResponseTime {
                private String duration;
            }

            @Data
            public static class ExpectedResolutionTime {
                private String duration;
            }

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


//    private Context context;
//    private Message message;
//
//
//    @Data
//    public static class Context {
//        private String domain;
//        private Location location;
//        private String version;
//        private String action;
//        private String ttl;
//        private String timestamp;
//        private String bap_id;
//        private String bap_uri;
//        private String transaction_id;
//        private String message_id;
//        private String bpp_id;
//        private String bpp_uri;
//
//        @Data
//        public static class Location {
//            private Country country;
//            private City city;
//
//            @Data
//            public static class Country {
//                private String code;
//            }
//
//            @Data
//            public static class City {
//                private String code;
//            }
//        }
//    }
//
//    @Data
//    public static class Message {
//        private Issue issue;
//    }
//
//    @Data
//    public static class Issue {
//        private String id;
//        private Customer customer;
//        private IssueDetails issue_details;
//        private String status;
//        private String sub_category;
//        private String category;
//    }
//
//    @Data
//    public static class Customer {
//        private String email;
//        private String name;
//        private String phone;
//    }
//
//    @Data
//    public static class IssueDetails {
//        private String long_desc;
//        private String short_desc;
//    }
}
