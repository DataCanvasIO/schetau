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

import io.github.datacanvasio.schetau.service.ActivityService;
import io.github.datacanvasio.schetau.service.PlanService;
import io.github.datacanvasio.schetau.service.SchetauService;
import io.github.datacanvasio.schetau.service.TaskService;
import io.github.datacanvasio.schetau.service.TimeService;
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

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    SchetauServiceImpl.class,
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class SchetauServiceImplTest {
    @Autowired
    private SchetauService schetauService;

    @MockBean
    private ActivityService activityService;
    @MockBean
    private PlanService jobService;
    @MockBean
    private TaskService taskService;
    @MockBean
    private TimeService timeService;

    @Test
    public void testScheduleInactive() {
        doNothing().when(timeService).setTime(anyLong());
        when(activityService.check(anyLong())).thenReturn(false);
        schetauService.schedule(2000L);
        InOrder inOrder = Mockito.inOrder(activityService, jobService, taskService, timeService);
        inOrder.verify(timeService, times(1)).setTime(2000L);
        inOrder.verify(activityService, times(1)).check(2000L);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testScheduleActive() {
        doNothing().when(timeService).setTime(anyLong());
        when(activityService.check(anyLong())).thenReturn(true);
        schetauService.schedule(4000L);
        InOrder inOrder = Mockito.inOrder(activityService, jobService, taskService, timeService);
        inOrder.verify(timeService, times(1)).setTime(4000L);
        inOrder.verify(activityService, times(1)).check(4000L);
        inOrder.verify(taskService, times(1)).emitSignals();
        inOrder.verify(jobService, times(1)).createTasks(4000L);
        inOrder.verify(taskService, times(1)).processWaiting(4000L);
        inOrder.verify(taskService, times(1)).submitReady();
        inOrder.verifyNoMoreInteractions();
    }
}
