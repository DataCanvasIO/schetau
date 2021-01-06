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

import io.github.datacanvasio.schetau.db.mapper.NodeMapper;
import io.github.datacanvasio.schetau.service.NodeService;
import io.github.datacanvasio.schetau.service.dto.NodeDto;
import io.github.datacanvasio.schetau.service.mapper.NodeDtoMapper;
import io.github.datacanvasio.schetau.util.HostUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class NodeServiceImpl implements NodeService {
    private NodeDto me;

    @Autowired
    private NodeMapper nodeMapper;

    @PostConstruct
    public void init() {
        me = HostUtil.getMyNode();
        log.info("Initialize node {}.", me);
    }

    @Override
    public NodeDto me() {
        return me;
    }

    @Override
    public void save() {
        nodeMapper.insert(NodeDtoMapper.MAPPER.toModel(me));
    }
}
