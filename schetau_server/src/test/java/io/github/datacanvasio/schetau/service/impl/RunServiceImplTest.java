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

import io.github.datacanvasio.schetau.db.model.TaskStatus;
import io.github.datacanvasio.schetau.runner.Runner;
import io.github.datacanvasio.schetau.service.JobService;
import io.github.datacanvasio.schetau.service.RunService;
import io.github.datacanvasio.schetau.service.TaskService;
import io.github.datacanvasio.schetau.service.dto.JobDto;
import io.github.datacanvasio.schetau.service.dto.TaskDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.annotation.Nonnull;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    RunServiceImpl.class,
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class RunServiceImplTest {
    @Autowired
    private RunService runService;

    @MockBean
    private JobService jobService;
    @MockBean
    private TaskService taskService;

    @Test
    public void testRun() {
        runService.registerRunner("Noop", new Runner() {
            @Override
            public void run(TaskDto task, String executionInfo, @Nonnull TaskService taskService) {
                taskService.setFinished(task, executionInfo);
            }

            @Override
            public void stop() {
            }
        });
        TaskDto task = new TaskDto();
        task.setId(1L);
        task.setPlanId(2L);
        task.setJobId(3L);
        task.setCreateTime(1000L);
        task.setStatus(TaskStatus.READY);
        JobDto job = new JobDto();
        job.setId(3L);
        job.setType("Noop");
        job.setExecutionInfo("test");
        when(jobService.getById(3L)).thenReturn(job);
        runService.run(task, taskService);
        InOrder inOrder = Mockito.inOrder(jobService, taskService);
        inOrder.verify(jobService, times(1)).getById(3L);
        inOrder.verify(taskService, times(1)).setSubmitted(task);
        inOrder.verify(taskService, times(1)).setFinished(task, "test");
        inOrder.verifyNoMoreInteractions();
    }
}
