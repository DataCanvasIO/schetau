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

import io.github.datacanvasio.schetau.db.model.Job;
import io.github.datacanvasio.schetau.service.dto.JobDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface JobDtoMapper {
    JobDtoMapper MAPPER = Mappers.getMapper(JobDtoMapper.class);

    @Mappings({
        @Mapping(target = "jobId", source = "id"),
        @Mapping(target = "jobName", source = "name"),
        @Mapping(target = "jobType", source = "type"),
    })
    Job toModel(JobDto dto);

    @Mappings({
        @Mapping(target = "id", source = "jobId"),
        @Mapping(target = "name", source = "jobName"),
        @Mapping(target = "type", source = "jobType"),
    })
    JobDto fromModel(Job model);

    List<JobDto> formModels(List<Job> jobList);
}
