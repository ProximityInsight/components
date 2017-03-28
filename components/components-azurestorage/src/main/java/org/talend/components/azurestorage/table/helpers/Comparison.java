// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.components.azurestorage.table.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.azure.storage.table.TableQuery.QueryComparisons;

public enum Comparison {

    EQUAL("EQUAL"),

    NOT_EQUAL("NOT EQUAL"),

    GREATER_THAN("GREATER THAN"),

    GREATER_THAN_OR_EQUAL("GREATER THAN OR EQUAL"),

    LESS_THAN("LESS THAN"),

    LESS_THAN_OR_EQUAL("LESS THAN OR EQUAL");

    private String displayName;

    private static Map<String, Comparison> mapPossibleValues = new HashMap<>();

    private static List<String> possibleValues = new ArrayList<>();

    static {
        for (Comparison comparison : values()) {
            mapPossibleValues.put(comparison.displayName, comparison);
            possibleValues.add(comparison.displayName);
        }
    }

    private Comparison(String displayName) {
        this.displayName = displayName;
    }

    public static List<String> possibleValues() {
        return possibleValues;
    }

    private static Comparison parse(String s) {
        if (!mapPossibleValues.containsKey(s)) {
            throw new IllegalArgumentException(String.format("Invalid value %s, it must be %s", s, mapPossibleValues));
        }
        return mapPossibleValues.get(s);
    }

    /**
     * Convert a function form String value to Azure Type {@link QueryComparisons}
     */
    public static String getQueryComparisons(String s) {
        switch (parse(s)) {
        case EQUAL:
            return QueryComparisons.EQUAL;
        case NOT_EQUAL:
            return QueryComparisons.NOT_EQUAL;
        case GREATER_THAN:
            return QueryComparisons.GREATER_THAN;
        case GREATER_THAN_OR_EQUAL:
            return QueryComparisons.GREATER_THAN_OR_EQUAL;
        case LESS_THAN:
            return QueryComparisons.LESS_THAN;
        case LESS_THAN_OR_EQUAL:
            return QueryComparisons.LESS_THAN_OR_EQUAL;
        default:
            return null;
        }
    }

    @Override
    public String toString() {
        return this.displayName;
    }
}