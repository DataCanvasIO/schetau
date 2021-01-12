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
import io.github.datacanvasio.schetau.db.model.Node;
import io.github.datacanvasio.schetau.service.NodeService;
import io.github.datacanvasio.schetau.service.dto.NodeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    NodeServiceImpl.class,
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class NodeServiceImplTest {
    @Autowired
    private NodeService nodeService;

    @MockBean
    private NodeMapper nodeMapper;

    @BeforeEach
    public void setup() {
        when(nodeMapper.insert(any(Node.class))).thenReturn(1);
    }

    @Test
    public void testMe() {
        NodeDto me = nodeService.me();
        assertThat(me.getHostAddress()).matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
        verifyNoInteractions(nodeMapper);
    }

    @Test
    public void testSave() {
        nodeService.save();
        verify(nodeMapper, times(1)).insert(any(Node.class));
        verifyNoMoreInteractions(nodeMapper);
    }

    @Test
    public void testListAll() {
        Node model = new Node();
        model.setNodeId("4E415788-033F-47A2-A4B2-23709EE36DA6");
        model.setHostAddress("127.0.0.1");
        model.setHostName("localhost");
        when(nodeMapper.findAll()).thenReturn(Collections.singletonList(model));
        List<NodeDto> nodes = nodeService.listAll();
        assertThat(nodes.size()).isEqualTo(1);
        assertThat(nodes.get(0))
            .hasFieldOrPropertyWithValue("id", "4E415788-033F-47A2-A4B2-23709EE36DA6")
            .hasFieldOrPropertyWithValue("hostAddress", "127.0.0.1")
            .hasFieldOrPropertyWithValue("hostName", "localhost");
        verify(nodeMapper, times(1)).findAll();
        verifyNoMoreInteractions(nodeMapper);
    }
}
