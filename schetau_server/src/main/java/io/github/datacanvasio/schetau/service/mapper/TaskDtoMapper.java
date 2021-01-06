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

package io.github.datacanvasio.schetau.service.mapper;

import io.github.datacanvasio.schetau.db.model.Task;
import io.github.datacanvasio.schetau.service.dto.JobDto;
import io.github.datacanvasio.schetau.service.dto.PlanDto;
import io.github.datacanvasio.schetau.service.dto.TaskDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface TaskDtoMapper {
    TaskDtoMapper MAPPER = Mappers.getMapper(TaskDtoMapper.class);

    @Mappings({
        @Mapping(target = "taskId", source = "id"),
        @Mapping(target = "taskStatus", source = "status"),
    })
    Task toModel(TaskDto dto);

    @Mappings({
        @Mapping(target = "id", source = "taskId"),
        @Mapping(target = "status", source = "taskStatus"),
    })
    TaskDto fromModel(Task model);

    List<TaskDto> fromModels(List<Task> models);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "planId", source = "plan.id"),
        @Mapping(target = "jobId", source = "job.id"),
        @Mapping(target = "createTime", source = "plan.nextRunTime"),
        @Mapping(target = "signalId", ignore = true),
        @Mapping(target = "runTime", ignore = true),
        @Mapping(target = "status", ignore = true),
        @Mapping(target = "result", ignore = true),
    })
    TaskDto fromPlanAndJob(PlanDto plan, JobDto job);
}
