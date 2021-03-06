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

package io.github.datacanvasio.schetau.controller;

import io.github.datacanvasio.schetau.controller.mapper.NodeResponseMapper;
import io.github.datacanvasio.schetau.controller.response.NodeResponse;
import io.github.datacanvasio.schetau.service.NodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Node APIs")
@RestController
@RequestMapping("/nodes")
public class NodeController {
    @Autowired
    private NodeService nodeService;

    @Operation(summary = "List all nodes.")
    @GetMapping("")
    public List<NodeResponse> listAll() {
        return NodeResponseMapper.MAPPER.fromNodes(nodeService.listAll(), nodeService.me().getId());
    }
}
