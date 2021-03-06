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

package io.github.datacanvasio.schetau.controller.mapper;

import io.github.datacanvasio.schetau.controller.response.PlanResponse;
import io.github.datacanvasio.schetau.service.dto.JobDto;
import io.github.datacanvasio.schetau.service.dto.PlanDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import javax.annotation.Nonnull;

@Mapper
public interface PlanResponseMapper {
    PlanResponseMapper MAPPER = Mappers.getMapper(PlanResponseMapper.class);

    PlanResponse fromPlan(PlanDto plan);

    List<PlanResponse> fromPlans(List<PlanDto> plans);

    default String getJobName(@Nonnull JobDto job) {
        return job.getName();
    }
}
