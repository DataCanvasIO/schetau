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

import io.github.datacanvasio.schetau.config.WebMvcConfigDev;
import io.github.datacanvasio.schetau.controller.advise.GlobalExceptionHandler;
import io.github.datacanvasio.schetau.controller.advise.ResponseBodyDecorator;
import io.github.datacanvasio.schetau.service.JobService;
import io.github.datacanvasio.schetau.service.dto.JobDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static io.github.datacanvasio.schetau.controller.WebMvcTestUtils.success;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {JobController.class})
public class JobControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private JobService jobService;

    @Test
    public void testListAll() throws Exception {
        JobDto job = new JobDto();
        job.setId(1L);
        job.setName("test");
        job.setType("CmdLine");
        job.setDescription("for test");
        when(jobService.listAll()).thenReturn(Collections.singletonList(job));
        mvc.perform(
            get("/api/jobs")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(success())
            .andExpect(jsonPath("$.data[0].id").value(1L))
            .andExpect(jsonPath("$.data[0].name").value("test"))
            .andExpect(jsonPath("$.data[0].type").value("CmdLine"))
            .andExpect(jsonPath("$.data[0].description").value("for test"));
        verify(jobService, times(1)).listAll();
        verifyNoMoreInteractions(jobService);
    }

    @Test
    public void testCreate() throws Exception {
        when(jobService.create(any(JobDto.class))).then(args -> {
            JobDto job = args.getArgument(0);
            job.setId(5L);
            return job;
        });
        mvc.perform(
            post("/api/jobs")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"name\": \"test\","
                    + "\"description\": \"for test\","
                    + "\"type\": \"CmdLine\","
                    + "\"execution_info\": \"ls\""
                    + "}")
        )
            .andDo(print())
            .andExpect(success())
            .andExpect(jsonPath("$.data.id").value(5L))
            .andExpect(jsonPath("$.data.name").value("test"))
            .andExpect(jsonPath("$.data.type").value("CmdLine"))
            .andExpect(jsonPath("$.data.description").value("for test"));
        verify(jobService, times(1)).create(any(JobDto.class));
        verifyNoMoreInteractions(jobService);
    }

    // Mock application
    @Configuration
    @EnableAutoConfiguration
    @Import({
        JobController.class,
        ResponseBodyDecorator.class,
        GlobalExceptionHandler.class,
        WebMvcConfigDev.class,
    })
    static class Config {
    }
}
