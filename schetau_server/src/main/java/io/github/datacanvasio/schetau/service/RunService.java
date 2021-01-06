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

package io.github.datacanvasio.schetau.service;

import io.github.datacanvasio.schetau.runner.Runner;
import io.github.datacanvasio.schetau.service.dto.TaskDto;

import javax.annotation.Nonnull;

public interface RunService {
    void run(@Nonnull TaskDto task, TaskService taskService);

    void registerRunner(String type, Runner runner);
}
