package com.can.buyerApp.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MotorInsuranceConstants {

    private MotorInsuranceConstants() {}

    // Vehicle Types
    public static final String TWO_WHEELER = "TWO_WHEELER";
    public static final String FOUR_WHEELER = "FOUR_WHEELER";

    // Coverage Types
    public static final String COMPREHENSIVE = "COMPREHENSIVE";
    public static final String THIRD_PARTY = "THIRD_PARTY";
    public static final String OWN_DAMAGE = "OWN_DAMAGE";

    // Two Wheeler Category Codes
    public static final String TWO_WHEELER_COMPREHENSIVE = "TWO_WHEELER_COMPRIHENSIVE_INSURANCE";
    public static final String TWO_WHEELER_THIRD_PARTY = "TWO_WHEELER_THIRD_PARTY_INSURANCE";
    public static final String TWO_WHEELER_OWN_DAMAGE_CODE = "TWO_WHEELER_OWN_DAMAGE";

    // Four Wheeler Category Codes
    public static final String FOUR_WHEELER_COMPREHENSIVE = "FOUR_WHEELER_COMPRIHENSIVE_INSURANCE";
    public static final String FOUR_WHEELER_THIRD_PARTY = "FOUR_WHEELER_THIRD_PARTY_INSURANCE";
    public static final String FOUR_WHEELER_OWN_DAMAGE_CODE = "FOUR_WHEELER_OWN_DAMAGE";

    // Parent Category Codes
    public static final String TWO_WHEELER_INSURANCE = "TWO_WHEELER_INSURANCE";
    public static final String FOUR_WHEELER_INSURANCE = "FOUR_WHEELER_INSURANCE";
    public static final String MOTOR_INSURANCE_CODE = "MOTOR_INSURANCE";

    // Two Wheeler Category Sets
    protected static final Set<String> TWO_WHEELER_CATEGORY_CODES = new HashSet<>(Arrays.asList(
            TWO_WHEELER_COMPREHENSIVE,
            TWO_WHEELER_THIRD_PARTY,
            TWO_WHEELER_OWN_DAMAGE_CODE,
            TWO_WHEELER_INSURANCE
    ));

    // Four Wheeler Category Sets
    protected static final Set<String> FOUR_WHEELER_CATEGORY_CODES = new HashSet<>(Arrays.asList(
            FOUR_WHEELER_COMPREHENSIVE,
            FOUR_WHEELER_THIRD_PARTY,
            FOUR_WHEELER_OWN_DAMAGE_CODE,
            FOUR_WHEELER_INSURANCE
    ));

    /**
     * Determine vehicle type from category code
     */
    public static String getVehicleType(String categoryCode) {
        if (categoryCode == null) {
            return null;
        }
        if (TWO_WHEELER_CATEGORY_CODES.contains(categoryCode)) {
            return TWO_WHEELER;
        } else if (FOUR_WHEELER_CATEGORY_CODES.contains(categoryCode)) {
            return FOUR_WHEELER;
        }
        return null;
    }

    /**
     * Determine coverage type from category code
     */
    public static String getCoverageType(String categoryCode) {
        if (categoryCode == null) {
            return null;
        }
        if (categoryCode.contains("COMPRIHENSIVE") || categoryCode.contains(COMPREHENSIVE)) {
            return COMPREHENSIVE;
        } else if (categoryCode.contains(THIRD_PARTY)) {
            return THIRD_PARTY;
        } else if (categoryCode.contains(OWN_DAMAGE)) {
            return OWN_DAMAGE;
        }
        return null;
    }

    /**
     * Check if category is a parent category
     */
    public static boolean isParentCategory(String categoryCode) {
        return MOTOR_INSURANCE_CODE.equals(categoryCode) ||
               TWO_WHEELER_INSURANCE.equals(categoryCode) ||
               FOUR_WHEELER_INSURANCE.equals(categoryCode);
    }
}