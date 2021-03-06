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

import io.github.datacanvasio.schetau.db.mapper.PlanJobMapper;
import io.github.datacanvasio.schetau.db.mapper.PlanMapper;
import io.github.datacanvasio.schetau.db.model.Job;
import io.github.datacanvasio.schetau.db.model.Plan;
import io.github.datacanvasio.schetau.db.model.TaskStatus;
import io.github.datacanvasio.schetau.service.PlanService;
import io.github.datacanvasio.schetau.service.TaskService;
import io.github.datacanvasio.schetau.service.dto.PlanDto;
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

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    PlanServiceImpl.class,
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class PlanServiceImplTest {
    @Autowired
    private PlanService planService;

    @MockBean
    private PlanMapper planMapper;
    @MockBean
    private PlanJobMapper planJobMapper;

    @MockBean
    private TaskService taskService;

    @Test
    public void testCreate() {
        when(planMapper.insert(any(Plan.class))).then(args -> {
            Plan model = args.getArgument(0);
            model.setPlanId(5L);
            return 1;
        });
        PlanDto plan = new PlanDto();
        plan.setName("test");
        plan.setFirstRunTime(2000L);
        plan = planService.create(plan);
        assertThat(plan.getId()).isEqualTo(5L);
        verify(planMapper, times(1)).insert(argThat(arg ->
            arg.getPlanName().equals("test")
                && arg.getFirstRunTime() == 2000L
        ));
        verifyNoMoreInteractions(planMapper);
        verifyNoInteractions(planJobMapper);
        verifyNoInteractions(taskService);
    }

    @Test
    public void testListAll() {
        Plan model = new Plan();
        model.setPlanId(1L);
        model.setPlanName("test");
        model.setFirstRunTime(10000L);
        model.setRunInterval(0L);
        model.setRunTimes(0L);
        model.setNextRunTime(10000L);
        model.setExpireTime(-1L);
        model.setSignalDefinition(null);
        Job job = new Job();
        job.setJobId(2L);
        model.setJobs(Collections.singletonList(job));
        when(planMapper.findAllWithJobs()).thenReturn(Collections.singletonList(model));
        List<PlanDto> plans = planService.listAll();
        assertThat(plans.size()).isEqualTo(1);
        assertThat(plans.get(0))
            .hasFieldOrPropertyWithValue("id", 1L)
            .hasFieldOrPropertyWithValue("name", "test")
            .hasFieldOrPropertyWithValue("firstRunTime", 10000L)
            .hasFieldOrPropertyWithValue("runInterval", 0L)
            .hasFieldOrPropertyWithValue("runTimes", 0L)
            .hasFieldOrPropertyWithValue("nextRunTime", 10000L)
            .hasFieldOrPropertyWithValue("expireTime", -1L);
        assertThat(plans.get(0).getJobs().size()).isEqualTo(1);
        assertThat(plans.get(0).getJobs().get(0))
            .hasFieldOrPropertyWithValue("id", 2L);
        verify(planMapper, times(1)).findAllWithJobs();
        verifyNoMoreInteractions(planMapper);
        verifyNoInteractions(planJobMapper);
        verifyNoInteractions(taskService);
    }

    @Test
    public void testUpdate() {
        when(planMapper.update(any(Plan.class))).thenReturn(1);
        PlanDto plan = new PlanDto();
        plan.setId(3L);
        plan.setName("testUpdate");
        planService.update(plan);
        verify(planMapper, times(1)).update(argThat(arg ->
            arg.getPlanId() == 3L
                && arg.getPlanName().equals("testUpdate")
        ));
        verifyNoMoreInteractions(planMapper);
        verifyNoInteractions(planJobMapper);
        verifyNoInteractions(taskService);
    }

    @Test
    public void testDelete() {
        when(planMapper.deleteById(3L)).thenReturn(1);
        planService.delete(3L);
        verify(planMapper, times(1)).deleteById(3L);
        verifyNoMoreInteractions(planMapper);
        verifyNoInteractions(planJobMapper);
        verifyNoInteractions(taskService);
    }

    @Test
    public void testAddJob() {
        when(planJobMapper.insert(anyLong(), anyLong())).thenReturn(1);
        planService.addJob(2L, 3L);
        verify(planJobMapper, times(1)).insert(2L, 3L);
        verifyNoMoreInteractions(planJobMapper);
        verifyNoInteractions(planMapper);
        verifyNoInteractions(taskService);
    }

    @Test
    public void testRemoveJob() {
        when(planJobMapper.delete(anyLong(), anyLong())).thenReturn(1);
        planService.removeJob(2L, 3L);
        verify(planJobMapper, times(1)).delete(2L, 3L);
        verifyNoMoreInteractions(planJobMapper);
        verifyNoInteractions(planMapper);
        verifyNoInteractions(taskService);
    }

    @Test
    public void testCreateTasks() {
        Plan model = new Plan();
        model.setPlanId(1L);
        model.setPlanName("test");
        model.setFirstRunTime(10000L);
        model.setRunInterval(0L);
        model.setRunTimes(0L);
        model.setNextRunTime(10000L);
        model.setExpireTime(-1L);
        model.setSignalDefinition(null);
        Job job = new Job();
        job.setJobId(2L);
        model.setJobs(Collections.singletonList(job));
        when(planMapper.findByNextRunTimeBeforeWithJobs(10000L)).thenReturn(Collections.singletonList(model));
        planService.createTasks(10000L);
        InOrder inOrder = Mockito.inOrder(planMapper, planJobMapper, taskService);
        inOrder.verify(planMapper, times(1)).findByNextRunTimeBeforeWithJobs(10000L);
        inOrder.verify(taskService, times(1)).create(argThat(arg ->
            arg.getPlanId() == 1L
                && arg.getJobId() == 2L
                && arg.getCreateTime() == 10000L
                && arg.getRunTimes() == 0
                && arg.getStatus() == TaskStatus.READY
        ));
        inOrder.verify(planMapper, times(1)).update(argThat(arg ->
            arg.getNextRunTime() == -1L
                && arg.getRunTimes() == 1
        ));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testCreateTasksRepeatable() {
        Plan model = new Plan();
        model.setPlanId(1L);
        model.setPlanName("test");
        model.setRunInterval(10000L);
        model.setRunTimes(0L);
        model.setNextRunTime(10000L);
        model.setExpireTime(-1L);
        model.setSignalDefinition(null);
        Job job = new Job();
        job.setJobId(2L);
        model.setJobs(Collections.singletonList(job));
        when(planMapper.findByNextRunTimeBeforeWithJobs(10000L)).thenReturn(Collections.singletonList(model));
        planService.createTasks(10000L);
        InOrder inOrder = Mockito.inOrder(planMapper, planJobMapper, taskService);
        inOrder.verify(planMapper, times(1)).findByNextRunTimeBeforeWithJobs(10000L);
        inOrder.verify(taskService, times(1)).create(argThat(arg ->
            arg.getPlanId() == 1L
                && arg.getJobId() == 2L
                && arg.getCreateTime() == 10000L
                && arg.getRunTimes() == 0
                && arg.getStatus() == TaskStatus.READY
        ));
        inOrder.verify(planMapper, times(1)).update(argThat(arg ->
            arg.getNextRunTime() == 20000L
                && arg.getRunTimes() == 1
        ));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testCreateTasksNone() {
        when(planMapper.findByNextRunTimeBeforeWithJobs(10000L)).thenReturn(Collections.emptyList());
        planService.createTasks(10000L);
        verify(planMapper, times(1)).findByNextRunTimeBeforeWithJobs(10000L);
        verifyNoMoreInteractions(planMapper);
        verifyNoInteractions(planJobMapper);
        verifyNoInteractions(taskService);
    }
}
