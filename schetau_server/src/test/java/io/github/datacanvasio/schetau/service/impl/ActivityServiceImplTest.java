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

import io.github.datacanvasio.schetau.db.mapper.ActivityMapper;
import io.github.datacanvasio.schetau.db.model.Activity;
import io.github.datacanvasio.schetau.service.ActivityService;
import io.github.datacanvasio.schetau.service.NodeService;
import io.github.datacanvasio.schetau.service.dto.NodeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    ActivityServiceImpl.class,
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ActivityServiceImplTest {
    private static final String NODE_ID = "BFD2CAE2-D87C-42FD-AFE1-E569E58B9A9D";

    @Autowired
    private ActivityService activityService;

    @MockBean
    private ActivityMapper activityMapper;

    @MockBean
    private NodeService nodeService;

    @BeforeEach
    public void setup() {
        NodeDto node = new NodeDto();
        node.setId(NODE_ID);
        node.setHostAddress("127.0.0.1");
        node.setHostName("localhost");
        when(nodeService.me()).thenReturn(node);
        doNothing().when(nodeService).save();
        when(activityMapper.insert(any(Activity.class))).thenReturn(1);
        when(activityMapper.update(any(Activity.class))).thenReturn(1);
    }

    @Test
    public void testCheckEmptyTable() {
        when(activityMapper.findMaxSeqNo()).thenReturn(null);
        assertTrue(activityService.check(2000L));
        InOrder inOrder = Mockito.inOrder(nodeService, activityMapper);
        inOrder.verify(activityMapper, times(1)).findMaxSeqNo();
        inOrder.verify(nodeService, times(1)).me();
        inOrder.verify(nodeService, times(1)).save();
        inOrder.verify(activityMapper, times(1)).insert(argThat(arg ->
            arg.getSeqNo() == 1L
                && arg.getNodeId().equals(NODE_ID)
                && arg.getLastUpdatingTime() == 2000L
        ));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testCheckIsMe() {
        Activity model = new Activity();
        model.setSeqNo(5L);
        model.setNodeId(NODE_ID);
        model.setLastUpdatingTime(1000L);
        when(activityMapper.findMaxSeqNo()).thenReturn(model);
        assertTrue(activityService.check(2000L));
        InOrder inOrder = Mockito.inOrder(nodeService, activityMapper);
        inOrder.verify(activityMapper, times(1)).findMaxSeqNo();
        inOrder.verify(nodeService, times(1)).me();
        inOrder.verify(activityMapper, times(1)).update(argThat(arg ->
            arg.getSeqNo() == 5L
                && arg.getNodeId().equals(NODE_ID)
                && arg.getLastUpdatingTime() == 2000L
        ));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testCheckNotMe() {
        final String otherNodeId = "042945F4-DA7E-48FB-9EEC-A81399B22DCE";
        Activity model = new Activity();
        model.setSeqNo(5L);
        model.setNodeId(otherNodeId);
        model.setLastUpdatingTime(1000L);
        when(activityMapper.findMaxSeqNo()).thenReturn(model);
        assertFalse(activityService.check(2000L));
        InOrder inOrder = Mockito.inOrder(nodeService, activityMapper);
        inOrder.verify(activityMapper, times(1)).findMaxSeqNo();
        inOrder.verify(nodeService, times(1)).me();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testCheckNotMeTakeOver() {
        final String otherNodeId = "042945F4-DA7E-48FB-9EEC-A81399B22DCE";
        Activity model = new Activity();
        model.setSeqNo(5L);
        model.setNodeId(otherNodeId);
        model.setLastUpdatingTime(1000L);
        when(activityMapper.findMaxSeqNo()).thenReturn(model);
        assertTrue(activityService.check(4000L));
        InOrder inOrder = Mockito.inOrder(nodeService, activityMapper);
        inOrder.verify(activityMapper, times(1)).findMaxSeqNo();
        inOrder.verify(nodeService, times(1)).me();
        inOrder.verify(nodeService, times(1)).save();
        inOrder.verify(activityMapper, times(1)).insert(argThat(arg ->
            arg.getSeqNo() == 6L
                && arg.getNodeId().equals(NODE_ID)
                && arg.getLastUpdatingTime() == 4000L
        ));
        inOrder.verifyNoMoreInteractions();
    }
}
