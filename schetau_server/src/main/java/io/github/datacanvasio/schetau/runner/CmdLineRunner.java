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

package io.github.datacanvasio.schetau.runner;

import io.github.datacanvasio.schetau.service.TaskService;
import io.github.datacanvasio.schetau.service.dto.TaskDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

@Slf4j
public class CmdLineRunner implements Runner {
    private ExecutorService executorService;

    public void start() {
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
            log.info("ExecutorService created.");
        }
    }

    @Override
    public void run(TaskDto task, String executionInfo, @Nonnull TaskService taskService) {
        start();
        String[] cmd = new String[]{"sh", "-c", executionInfo};
        log.info("The command is: `{}`", String.join(" ", cmd));
        Objects.requireNonNull(executorService).execute(() -> {
            try {
                Process process = Runtime.getRuntime().exec(cmd);
                taskService.setFinished(task, "");
                if (log.isInfoEnabled()) {
                    String output = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
                    String error = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
                    log.info("The console output is: \n{}", output);
                    log.info("The console error output is: \n{}", error);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void stop() {
        if (executorService == null) {
            return;
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        executorService = null;
        log.info("ExecutorService destroyed.");
    }
}
