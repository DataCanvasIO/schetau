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

import io.github.datacanvasio.schetau.db.model.Activity;
import io.github.datacanvasio.schetau.service.dto.ActivityDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ActivityDtoMapper {
    ActivityDtoMapper MAPPER = Mappers.getMapper(ActivityDtoMapper.class);

    Activity toModel(ActivityDto dto);

    ActivityDto fromModel(Activity model);
}
