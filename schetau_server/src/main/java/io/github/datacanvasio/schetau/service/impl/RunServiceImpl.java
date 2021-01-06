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

import io.github.datacanvasio.schetau.runner.CmdLineRunner;
import io.github.datacanvasio.schetau.runner.LoggerRunner;
import io.github.datacanvasio.schetau.runner.Runner;
import io.github.datacanvasio.schetau.service.JobService;
import io.github.datacanvasio.schetau.service.RunService;
import io.github.datacanvasio.schetau.service.TaskService;
import io.github.datacanvasio.schetau.service.dto.JobDto;
import io.github.datacanvasio.schetau.service.dto.TaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class RunServiceImpl implements RunService {
    private Map<String, Runner> runnerMap;

    @Autowired
    private JobService jobService;

    @PostConstruct
    private void init() {
        runnerMap = new LinkedHashMap<>(4);
        registerRunner("CmdLine", new CmdLineRunner());
        registerRunner("Logger", new LoggerRunner());
    }

    @Override
    public void run(@Nonnull TaskDto task, TaskService taskService) {
        JobDto job = jobService.getById(task.getJobId());
        String type = job.getType();
        Runner runner = runnerMap.get(type);
        if (runner == null) {
            throw new RuntimeException("Unknown job type \"" + type + "\".");
        }
        // Set to submitted first, or it may finished in advance.
        taskService.setSubmitted(task);
        runner.run(task, job.getExecutionInfo(), taskService);
    }

    @Override
    public void registerRunner(String type, Runner runner) {
        runnerMap.put(type, runner);
    }

    @PreDestroy
    public void stop() {
        for (Runner runner : runnerMap.values()) {
            runner.stop();
        }
    }
}
