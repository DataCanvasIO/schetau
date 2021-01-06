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

import io.github.datacanvasio.schetau.db.model.Plan;
import io.github.datacanvasio.schetau.service.dto.PlanDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(uses = {JobDtoMapper.class})
public interface PlanDtoMapper {
    PlanDtoMapper MAPPER = Mappers.getMapper(PlanDtoMapper.class);

    @Mappings({
        @Mapping(target = "planId", source = "id"),
        @Mapping(target = "planName", source = "name"),
    })
    Plan toModel(PlanDto dto);

    @Mappings({
        @Mapping(target = "id", source = "planId"),
        @Mapping(target = "name", source = "planName"),
    })
    PlanDto fromModel(Plan model);

    List<PlanDto> fromModels(List<Plan> models);
}
