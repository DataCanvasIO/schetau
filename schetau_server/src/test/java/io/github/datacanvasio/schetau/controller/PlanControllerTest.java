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
import io.github.datacanvasio.schetau.service.PlanService;
import io.github.datacanvasio.schetau.service.dto.JobDto;
import io.github.datacanvasio.schetau.service.dto.PlanDto;
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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {PlanController.class})
public class PlanControllerTest {
    private static final String URL_BASE = "/api/plans";
    private static final String URL_WITH_ID = URL_BASE + "/{id}";
    private static final String URL_WITH_JOBS = URL_BASE + "/{id}/jobs";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PlanService planService;

    @Test
    public void testListAll() throws Exception {
        PlanDto plan = new PlanDto();
        plan.setId(1L);
        plan.setName("test");
        JobDto job = new JobDto();
        job.setId(2L);
        job.setName("testJob");
        plan.setJobs(Collections.singletonList(job));
        when(planService.listAll()).thenReturn(Collections.singletonList(plan));
        mvc.perform(
            get(URL_BASE)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(success())
            .andExpect(jsonPath("$.data[0].id").value(1L))
            .andExpect(jsonPath("$.data[0].name").value("test"))
            .andExpect(jsonPath("$.data[0].jobs[0]").value("testJob"));
        verify(planService, times(1)).listAll();
        verifyNoMoreInteractions(planService);
    }

    @Test
    public void testCreate() throws Exception {
        when(planService.create(any(PlanDto.class))).then(args -> {
            PlanDto plan = args.getArgument(0);
            plan.setId(5L);
            plan.setNextRunTime(plan.getFirstRunTime());
            return plan;
        });
        mvc.perform(
            post(URL_BASE)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"name\": \"test\","
                    + "\"first_run_time\": 10000,"
                    + "\"run_interval\": 1000"
                    + "}")
        )
            .andDo(print())
            .andExpect(success())
            .andExpect(jsonPath("$.data.id").value(5L))
            .andExpect(jsonPath("$.data.name").value("test"))
            .andExpect(jsonPath("$.data.first_run_time").value(10000))
            .andExpect(jsonPath("$.data.next_run_time").value(10000));
        verify(planService, times(1)).create(any(PlanDto.class));
        verifyNoMoreInteractions(planService);
    }

    @Test
    public void testUpdate() throws Exception {
        mvc.perform(
            put(URL_WITH_ID, "2")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{"
                    + "\"name\": \"testUpdate\""
                    + "}")
        )
            .andDo(print())
            .andExpect(success());
        verify(planService, times(1)).update(argThat(arg ->
            arg.getId() == 2L
                && arg.getName().equals("testUpdate")
        ));
        verifyNoMoreInteractions(planService);
    }

    @Test
    public void testDelete() throws Exception {
        mvc.perform(
            delete(URL_WITH_ID, "3")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(success());
        verify(planService, times(1)).delete(3L);
        verifyNoMoreInteractions(planService);
    }

    @Test
    public void testAddJob() throws Exception {
        mvc.perform(
            put(URL_WITH_JOBS, 1)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"job_id\": 5}")
        )
            .andDo(print())
            .andExpect(success());
        verify(planService, times(1)).addJob(1L, 5L);
        verifyNoMoreInteractions(planService);
    }

    @Test
    public void testRemoveJob() throws Exception {
        mvc.perform(
            delete(URL_WITH_JOBS, 3)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"job_id\": 7}")
        )
            .andDo(print())
            .andExpect(success());
        verify(planService, times(1)).removeJob(3L, 7L);
        verifyNoMoreInteractions(planService);
    }

    // Mock application
    @Configuration
    @EnableAutoConfiguration
    @Import({
        PlanController.class,
        ResponseBodyDecorator.class,
        GlobalExceptionHandler.class,
        WebMvcConfigDev.class,
    })
    static class Config {
    }
}
