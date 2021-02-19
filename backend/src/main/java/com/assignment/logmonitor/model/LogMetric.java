package com.assignment.logmonitor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class LogMetric {
    @JsonProperty("level")
    private com.assignment.logmonitor.model.LogLevelEnum level;

    @JsonProperty("count")
    private Integer count;

    public com.assignment.logmonitor.model.LogMetric level(com.assignment.logmonitor.model.LogLevelEnum level) {
        this.level = level;
        return this;
    }


    @ApiModelProperty(required = true)
    @NotNull
    public com.assignment.logmonitor.model.LogLevelEnum getLevel() {
        return level;
    }

    public void setLevel(LogLevelEnum level) {
        this.level = level;
    }

    public com.assignment.logmonitor.model.LogMetric count(Integer count) {
        this.count = count;
        return this;
    }

    @ApiModelProperty(value = "Number of times that this log level occurred in the last period of observation")
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        com.assignment.logmonitor.model.LogMetric logMetric = (com.assignment.logmonitor.model.LogMetric) o;
        return Objects.equals(this.level, logMetric.level) && Objects.equals(this.count, logMetric.count);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, count);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class LogMetric {\n");
        sb.append("    level: ").append(toIndentedString(level)).append("\n");
        sb.append("    count: ").append(toIndentedString(count)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(java.lang.Object o) {
        if (o == null) return "null";
        return o.toString().replace("\n", "\n    ");
    }
}
