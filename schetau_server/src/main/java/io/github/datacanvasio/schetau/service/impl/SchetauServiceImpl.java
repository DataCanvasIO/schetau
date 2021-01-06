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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SchetauServiceImpl implements SchetauService {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private PlanService planService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TimeService timeService;

    @Override
    public void schedule(long currentTime) {
        timeService.setTime(currentTime);
        log.debug("currentTime = {}", currentTime);
        if (!activityService.check(currentTime)) {
            return;
        }
        taskService.emitSignals();
        planService.createTasks(currentTime);
        taskService.processWaiting(currentTime);
        taskService.submitReady();
    }
}
