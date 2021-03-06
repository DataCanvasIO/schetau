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

package io.github.datacanvasio.schetau.controller;

import io.github.datacanvasio.schetau.controller.advise.SchetauResponse;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;

import javax.annotation.Nonnull;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public final class WebMvcTestUtils {
    private WebMvcTestUtils() {
    }

    /**
     * Define 'success' for WebMvcTest.
     *
     * @return what 'success' means.
     */
    @Nonnull
    public static ResultMatcher success() {
        return ResultMatcher.matchAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.status").value(SchetauResponse.OK),
            jsonPath("$.message").value(SchetauResponse.SUCCESS)
        );
    }

    /**
     * Define 'errorCode' for WebMvcTest.
     *
     * @return what 'errorCode' means.
     */
    @Nonnull
    public static ResultMatcher errorCode(String code) {
        return ResultMatcher.matchAll(
            status().isOk(),
            content().contentType(MediaType.APPLICATION_JSON),
            jsonPath("$.status").value(code),
            jsonPath("$.message").value(notNullValue(String.class)),
            jsonPath("$.data").value(nullValue())
        );
    }
}
