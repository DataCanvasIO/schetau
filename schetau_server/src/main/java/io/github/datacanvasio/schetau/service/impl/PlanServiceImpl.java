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

import io.github.datacanvasio.schetau.db.mapper.PlanMapper;
import io.github.datacanvasio.schetau.db.model.Plan;
import io.github.datacanvasio.schetau.db.model.TaskStatus;
import io.github.datacanvasio.schetau.service.PlanService;
import io.github.datacanvasio.schetau.service.TaskService;
import io.github.datacanvasio.schetau.service.dto.JobDto;
import io.github.datacanvasio.schetau.service.dto.PlanDto;
import io.github.datacanvasio.schetau.service.dto.TaskDto;
import io.github.datacanvasio.schetau.service.mapper.PlanDtoMapper;
import io.github.datacanvasio.schetau.service.mapper.TaskDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class PlanServiceImpl implements PlanService {
    @Autowired
    private PlanMapper planMapper;

    @Autowired
    private TaskService taskService;

    @Override
    public PlanDto create(PlanDto plan) {
        Plan model = PlanDtoMapper.MAPPER.toModel(plan);
        planMapper.insert(model);
        return PlanDtoMapper.MAPPER.fromModel(model);
    }

    @Override
    public void createTasks(long currentTime) {
        List<PlanDto> plans = PlanDtoMapper.MAPPER.fromModels(
            planMapper.findByNextRunTimeBeforeWithJobs(currentTime)
        );
        for (PlanDto plan : plans) {
            createTasksForPlan(plan);
        }
    }

    @Transactional
    private void createTasksForPlan(PlanDto plan) {
        log.debug("Create tasks for plan: {}", plan);
        for (JobDto job : plan.getJobs()) {
            TaskDto task = TaskDtoMapper.MAPPER.fromPlanAndJob(plan, job);
            if (plan.getSignalDefinition() != null && !plan.getSignalDefinition().isEmpty()) {
                taskService.createSignals(task, plan.getSignalDefinition());
            }
            if (task.getSignalId() != null) {
                task.setStatus(TaskStatus.WAITING);
            } else {
                task.setStatus(TaskStatus.READY);
            }
            taskService.create(task);
        }
        if (plan.getRunInterval() > 0L) {
            plan.setNextRunTime(plan.getNextRunTime() + plan.getRunInterval());
        } else {
            plan.setNextRunTime(-1L);
        }
        plan.setRunTimes(plan.getRunTimes() + 1);
        planMapper.update(PlanDtoMapper.MAPPER.toModel(plan));
    }
}
