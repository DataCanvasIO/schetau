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

import io.github.datacanvasio.schetau.db.mapper.TaskMapper;
import io.github.datacanvasio.schetau.db.model.Task;
import io.github.datacanvasio.schetau.db.model.TaskStatus;
import io.github.datacanvasio.schetau.service.RunService;
import io.github.datacanvasio.schetau.service.SignalService;
import io.github.datacanvasio.schetau.service.TaskService;
import io.github.datacanvasio.schetau.service.TimeService;
import io.github.datacanvasio.schetau.service.dto.TaskDto;
import io.github.datacanvasio.schetau.signal.TaskFinishedSignalFun;
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

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    TaskServiceImpl.class,
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class TaskServiceImplTest {
    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @MockBean
    private SignalService signalService;
    @MockBean
    private RunService runService;
    @MockBean
    private TimeService timeService;

    @Test
    public void testCreate() {
        when(taskMapper.insert(any(Task.class))).then(args -> {
            Task model = args.getArgument(0);
            model.setTaskId(5L);
            return 1;
        });
        TaskDto task = new TaskDto();
        task.setPlanId(2L);
        task.setJobId(3L);
        task.setCreateTime(1000L);
        task.setStatus(TaskStatus.WAITING);
        task = taskService.create(task);
        assertThat(task.getId()).isEqualTo(5L);
        verify(taskMapper, times(1)).insert(argThat(arg ->
            arg.getPlanId() == 2L
                && arg.getJobId() == 3L
                && arg.getCreateTime() == 1000L
                && arg.getTaskStatus() == TaskStatus.WAITING
        ));
        verifyNoMoreInteractions(taskMapper);
    }

    @Test
    public void testCreateSignals() {
        TaskDto task = new TaskDto();
        task.setId(3L);
        taskService.createSignals(task, "signal definition");
        verify(signalService, times(1))
            .evalAndSaveSignalTree("signal definition", task);
        verifyNoMoreInteractions(signalService);
    }

    @Test
    public void testSubmitReady() {
        Task model1 = new Task();
        Task model2 = new Task();
        when(taskMapper.findByStatus(TaskStatus.READY)).thenReturn(Arrays.asList(model1, model2));
        taskService.submitReady();
        InOrder inOrder = Mockito.inOrder(taskMapper, runService);
        inOrder.verify(taskMapper, times(1)).findByStatus(TaskStatus.READY);
        inOrder.verify(runService, times(2)).run(any(TaskDto.class), eq(taskService));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testEmitSignals() {
        Task model = new Task();
        model.setTaskId(1L);
        model.setPlanId(2L);
        model.setJobId(3L);
        model.setRunTimes(4L);
        when(taskMapper.findByStatus(TaskStatus.FINISHED)).thenReturn(Collections.singletonList(model));
        taskService.emitSignals();
        InOrder inOrder = Mockito.inOrder(taskMapper, signalService);
        inOrder.verify(taskMapper, times(1)).findByStatus(TaskStatus.FINISHED);
        inOrder.verify(signalService, times(1))
            .emit(TaskFinishedSignalFun.signature(model.getPlanId(), model.getJobId(), model.getRunTimes()));
        inOrder.verify(taskMapper, times(1))
            .update(argThat(m -> m.getTaskStatus() == TaskStatus.FINISHED_EMITTED));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testProcessWaiting() {
        taskService.processWaiting(10000L);
        InOrder inOrder = Mockito.inOrder(taskMapper, signalService);
        inOrder.verify(taskMapper, times(1))
            .updateWaitingToReadyBySignals();
        inOrder.verify(taskMapper, times(1))
            .updateWaitingToExpiredByTime(10000L);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSetSubmitted() {
        when(taskMapper.update(any(Task.class))).thenReturn(1);
        when(timeService.current()).thenReturn(5000L);
        TaskDto task = new TaskDto();
        task.setId(3L);
        taskService.setSubmitted(task);
        verify(taskMapper, times(1)).update(argThat(arg ->
            arg.getTaskId() == 3L
                && arg.getTaskStatus() == TaskStatus.SUBMITTED
                && arg.getRunTime() == 5000L
        ));
        verifyNoMoreInteractions(taskMapper);
    }

    @Test
    public void testSetFinished() {
        TaskDto task = new TaskDto();
        task.setId(3L);
        taskService.setFinished(task, "test result");
        verify(taskMapper, times(1)).update(argThat(arg ->
            arg.getTaskId() == 3L
                && arg.getTaskStatus() == TaskStatus.FINISHED
                && arg.getResult().equals("test result")
        ));
        verifyNoMoreInteractions(taskMapper);
    }
}
