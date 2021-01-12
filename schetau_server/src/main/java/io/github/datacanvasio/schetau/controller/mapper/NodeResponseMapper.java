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

import io.github.datacanvasio.schetau.controller.response.NodeResponse;
import io.github.datacanvasio.schetau.service.dto.NodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

@Mapper
public interface NodeResponseMapper {
    NodeResponseMapper MAPPER = Mappers.getMapper(NodeResponseMapper.class);

    default NodeResponse fromNode(@Nullable NodeDto node, final String id) {
        if (node != null) {
            NodeResponse response = new NodeResponse();
            response.setId(node.getId());
            response.setHostAddress(node.getHostAddress());
            response.setHostName(node.getHostName());
            response.setMe(node.getId().equals(id));
            return response;
        }
        return null;
    }

    default List<NodeResponse> fromNodes(@Nullable List<NodeDto> nodes, final String id) {
        if (nodes != null) {
            return nodes.stream()
                .map(node -> fromNode(node, id))
                .collect(Collectors.toList());
        }
        return null;
    }
}
