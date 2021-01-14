/*
 * Copyright 2020 DataCanvas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.datacanvasio.schetau.controller.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.List;

@Data
@JsonPropertyOrder({
    "name",
    "first_run_time",
    "run_interval",
    "expire_time",
    "wait_timeout",
    "signal_definition",
    "jobs",
    "run_times",
    "next_run_time",
})
public class PlanResponse {
    @JsonProperty("id")
    private Long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("first_run_time")
    private Long firstRunTime;
    @JsonProperty("run_interval")
    private Long runInterval;
    @JsonProperty("expire_time")
    private Long expireTime;
    @JsonProperty("wait_timeout")
    private Long waitTimeout;
    @JsonProperty("signal_definition")
    private String signalDefinition;
    @JsonProperty("jobs")
    private List<String> jobs;
    @JsonProperty("run_times")
    private Long runTimes;
    @JsonProperty("next_run_time")
    private Long nextRunTime;
}
