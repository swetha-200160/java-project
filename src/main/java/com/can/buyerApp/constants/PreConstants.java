package com.can.buyerApp.constants;

import java.util.Set;

public class PreConstants {

    private PreConstants() {}

    // Domain
    public static final String VALID_DOMAIN = "ONDC:FIS13";

    // Insurance Types
    public static final String MOTOR_INSURANCE = "MOTOR_INSURANCE";
    public static final Set<String> VALID_TYPES = Set.of("HEALTH_INSURANCE", MOTOR_INSURANCE, "MARINE_INSURANCE");

    // Actions
    public static final String SEARCH = "search";
    public static final String SELECT = "select";
    public static final String INIT = "init";
    public static final String CONFIRM = "confirm";
    public static final String CANCEL = "cancel";
    public static final String STATUS = "status";
    public static final String UPDATE="update";

    // Configuration
    public static final String MOTOR_TTL = "PT24H";
    public static final String MOTOR_VERSION = "2.0.0";
    public static final String COUNTRY_CODE = "IND";
    public static final String CITY_CODE = "*";
    public static final String BPP = "BPP";

    // Payment Settlement
    public static final String SETTLEMENT_TYPE = "SETTLEMENT_TYPE";
    public static final String SETTLEMENT_AMOUNT = "SETTLEMENT_AMOUNT";

    // Add-ons
    public static final String ADD_ONS = "ADD_ONS";

    // Progress Status
    public static final String ON_SEARCH_1 = "ON_SEARCH_1";
    public static final String ON_SEARCH_2 = "ON_SEARCH_2";
    public static final String ON_SELECT = "ON_SELECT";
    public static final String ON_INIT_1 = "ON_INIT_1";
    public static final String ON_INIT_2 = "ON_INIT_2";
    public static final String ON_CONFIRM = "ON_CONFIRM";


    // Motor Insurance Categories
    public static final String COMPREHENSIVE = "COMPREHENSIVE";
    public static final String THIRD_PARTY = "THIRD_PARTY";
    public static final String OWN_DAMAGE = "OWN_DAMAGE";

    // Motor Insurance Add-ons
    public static final String ZERO_DEPRECIATION = "ZERO_DEPRECIATION";
    public static final String ENGINE_PROTECTION = "ENGINE_PROTECTION";
    public static final String NCB_PROTECTION = "NCB_PROTECTION";
    public static final String ROADSIDE_ASSISTANCE = "ROADSIDE_ASSISTANCE";
    public static final String CONSUMABLES_COVER = "CONSUMABLES_COVER";
    public static final String KEY_REPLACEMENT = "KEY_REPLACEMENT";
    public static final String RETURN_TO_INVOICE = "RETURN_TO_INVOICE";
    public static final String PASSENGER_COVER = "PASSENGER_COVER";

    public static final String ORDER_FULFILLMENTS = "order.fulfillments";
}