package com.assignment.logmonitor.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum LogLevelEnum {
    INFO("INFO"),
    WARNING("WARNING"),
    ERROR("ERROR");

    private String value;

    LogLevelEnum(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static com.assignment.logmonitor.model.LogLevelEnum fromValue(String value) {
        for (com.assignment.logmonitor.model.LogLevelEnum b : com.assignment.logmonitor.model.LogLevelEnum.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
}
