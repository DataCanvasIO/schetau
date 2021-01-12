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

package io.github.datacanvasio.schetau.service.impl;

import io.github.datacanvasio.schetau.db.mapper.JobMapper;
import io.github.datacanvasio.schetau.db.model.Job;
import io.github.datacanvasio.schetau.service.JobService;
import io.github.datacanvasio.schetau.service.dto.JobDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    JobServiceImpl.class,
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class JobServiceImplTest {
    @Autowired
    private JobService jobService;

    @MockBean
    private JobMapper jobMapper;

    @Test
    public void testCreate() {
        when(jobMapper.insert(any(Job.class))).then(args -> {
            Job model = args.getArgument(0);
            model.setJobId(5L);
            return 1;
        });
        JobDto job = new JobDto();
        job.setName("test");
        job.setType("CmdLine");
        job.setExecutionInfo("ls");
        job = jobService.create(job);
        assertThat(job.getId()).isEqualTo(5L);
        verify(jobMapper, times(1)).insert(argThat(arg ->
            arg.getJobName().equals("test")
                && arg.getJobType().equals("CmdLine")
                && arg.getExecutionInfo().equals("ls")
        ));
        verifyNoMoreInteractions(jobMapper);
    }

    @Test
    public void testGetById() {
        Job model = new Job();
        model.setJobId(1L);
        model.setJobName("test");
        model.setDescription("A job for test.");
        model.setJobType("CmdLine");
        model.setExecutionInfo("ls -l");
        when(jobMapper.findById(1L)).thenReturn(model);
        JobDto job = jobService.getById(1L);
        assertThat(job)
            .hasFieldOrPropertyWithValue("id", 1L)
            .hasFieldOrPropertyWithValue("name", "test")
            .hasFieldOrPropertyWithValue("description", "A job for test.")
            .hasFieldOrPropertyWithValue("type", "CmdLine")
            .hasFieldOrPropertyWithValue("executionInfo", "ls -l");
        verify(jobMapper, times(1)).findById(1L);
        verifyNoMoreInteractions(jobMapper);
    }

    @Test
    public void testListAll() {
        Job model = new Job();
        model.setJobId(2L);
        model.setJobName("test");
        model.setDescription("A job for test.");
        model.setJobType("CmdLine");
        model.setExecutionInfo("ls -l");
        when(jobMapper.findAll()).thenReturn(Collections.singletonList(model));
        List<JobDto> jobs = jobService.listAll();
        assertThat(jobs.size()).isEqualTo(1);
        assertThat(jobs.get(0))
            .hasFieldOrPropertyWithValue("id", 2L)
            .hasFieldOrPropertyWithValue("name", "test")
            .hasFieldOrPropertyWithValue("description", "A job for test.")
            .hasFieldOrPropertyWithValue("type", "CmdLine")
            .hasFieldOrPropertyWithValue("executionInfo", "ls -l");
        verify(jobMapper, times(1)).findAll();
        verifyNoMoreInteractions(jobMapper);
    }
}
