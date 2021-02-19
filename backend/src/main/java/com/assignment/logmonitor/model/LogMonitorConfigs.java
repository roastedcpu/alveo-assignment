package com.assignment.logmonitor.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.*;

public class LogMonitorConfigs   {
  @JsonProperty("refreshInterval")
  private Integer refreshInterval;

  public LogMonitorConfigs refreshInterval(Integer refreshInterval) {
    this.refreshInterval = refreshInterval;
    return this;
  }


  @ApiModelProperty(required = true, value = "The refresh interval in seconds (integer value)")
  @NotNull
  public Integer getRefreshInterval() {
    return refreshInterval;
  }

  public void setRefreshInterval(Integer refreshInterval) {
    this.refreshInterval = refreshInterval;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    LogMonitorConfigs logMonitorConfigs = (LogMonitorConfigs) o;
    return Objects.equals(this.refreshInterval, logMonitorConfigs.refreshInterval);
  }

  @Override
  public int hashCode() {
    return Objects.hash(refreshInterval);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class LogMonitorConfigs {\n");
    
    sb.append("    refreshInterval: ").append(toIndentedString(refreshInterval)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

