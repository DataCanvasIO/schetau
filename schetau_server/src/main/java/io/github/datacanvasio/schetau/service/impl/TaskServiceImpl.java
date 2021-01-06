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
import io.github.datacanvasio.schetau.service.mapper.TaskDtoMapper;
import io.github.datacanvasio.schetau.signal.TaskFinishedSignalFun;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import javax.annotation.Nonnull;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {
    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private SignalService signalService;
    @Autowired
    private RunService runService;
    @Autowired
    private TimeService timeService;

    private List<TaskDto> getByStatus(TaskStatus status) {
        return TaskDtoMapper.MAPPER.fromModels(taskMapper.findByStatus(status));
    }

    private void submit(TaskDto task) {
        log.debug("Submit task: {}", task);
        runService.run(task, this);
    }

    @Transactional
    private void emitSignalsFor(@Nonnull TaskDto task) {
        signalService.emit(TaskFinishedSignalFun.signature(
            task.getPlanId(),
            task.getJobId(),
            task.getRunTimes()
        ));
        task.setStatus(TaskStatus.FINISHED_EMITTED);
        taskMapper.update(TaskDtoMapper.MAPPER.toModel(task));
    }

    @Override
    public TaskDto create(TaskDto task) {
        Task model = TaskDtoMapper.MAPPER.toModel(task);
        taskMapper.insert(model);
        return TaskDtoMapper.MAPPER.fromModel(model);
    }

    @Override
    public void createSignals(@Nonnull TaskDto task, String signalDefinition) {
        task.setSignalId(signalService.evalAndSaveSignalTree(signalDefinition, task));
    }

    @Override
    public void submitReady() {
        List<TaskDto> tasks = getByStatus(TaskStatus.READY);
        for (TaskDto task : tasks) {
            submit(task);
        }
    }

    @Transactional
    @Override
    public void setSubmitted(@Nonnull TaskDto task) {
        task.setStatus(TaskStatus.SUBMITTED);
        task.setRunTime(timeService.current());
        taskMapper.update(TaskDtoMapper.MAPPER.toModel(task));
    }

    @Override
    public void emitSignals() {
        for (TaskDto task : getByStatus(TaskStatus.FINISHED)) {
            emitSignalsFor(task);
        }
    }

    @Override
    public void processWaiting(long currentTime) {
        int num;
        num = taskMapper.updateWaitingToReadyBySignals();
        if (num > 0) {
            log.info("{} tasks set to READY status.", num);
        }
        num = taskMapper.updateWaitingToExpiredByTime(currentTime);
        if (num > 0) {
            log.info("{} tasks set to EXPIRED status.", num);
        }
    }

    @Override
    public void setFinished(@Nonnull TaskDto task, String result) {
        task.setStatus(TaskStatus.FINISHED);
        task.setResult(result);
        taskMapper.update(TaskDtoMapper.MAPPER.toModel(task));
    }
}
