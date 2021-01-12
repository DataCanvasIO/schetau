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
import lombok.ToString;

@ToString
public class SchetauResponse {
    public static final String OK = "0";
    public static final String SUCCESS = "success";

    @JsonProperty("status")
    private final String status;
    @JsonProperty("message")
    private final String message;
    @JsonProperty("data")
    private final Object data;

    public SchetauResponse(Object data) {
        this.data = data;
        this.status = OK;
        this.message = SUCCESS;
    }

    public SchetauResponse(String errorCode, String message) {
        this.status = errorCode;
        this.message = message;
        this.data = null;
    }
}
