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

import io.github.datacanvasio.schetau.db.mapper.SignalMapper;
import io.github.datacanvasio.schetau.db.mapper.SignalRelationMapper;
import io.github.datacanvasio.schetau.db.model.Signal;
import io.github.datacanvasio.schetau.service.SignalService;
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

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    SignalServiceImpl.class,
})
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class SignalServiceImplTest {
    @Autowired
    private SignalService signalService;

    @MockBean
    private SignalMapper signalMapper;
    @MockBean
    private SignalRelationMapper signalRelationMapper;

    @Test
    public void testEmitNotExist() {
        when(signalMapper.findBySignature(anyString())).thenReturn(null);
        when(signalMapper.insert(any(Signal.class))).thenReturn(1);
        signalService.emit("TaskFinished");
        InOrder inOrder = Mockito.inOrder(signalMapper);
        inOrder.verify(signalMapper, times(1))
            .findBySignature("TaskFinished");
        inOrder.verify(signalMapper, times(1))
            .insert(argThat(arg ->
                arg.getSignature().equals("TaskFinished")
                    && arg.getCountDown() == 0
            ));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testEmitExist() {
        Signal model = new Signal();
        model.setSignalId(1L);
        model.setSignature("TaskFinished");
        model.setCountDown(1L);
        when(signalMapper.findBySignature(anyString())).thenReturn(model);
        when(signalMapper.findParents(1L)).thenReturn(Collections.emptyList());
        when(signalMapper.update(any(Signal.class))).thenReturn(1);
        signalService.emit("TaskFinished");
        InOrder inOrder = Mockito.inOrder(signalMapper);
        inOrder.verify(signalMapper, times(1))
            .findBySignature("TaskFinished");
        inOrder.verify(signalMapper, times(1))
            .findParents(1L);
        inOrder.verify(signalMapper, times(1))
            .update(argThat(arg ->
                arg.getSignalId() == 1L
                    && arg.getSignature().equals("TaskFinished")
                    && arg.getCountDown() == 0
            ));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testEmitAlreadyEmitted() {
        Signal model = new Signal();
        model.setSignalId(1L);
        model.setSignature("TaskFinished");
        model.setCountDown(0L);
        when(signalMapper.findBySignature(anyString())).thenReturn(model);
        when(signalMapper.findParents(1L)).thenReturn(Collections.emptyList());
        signalService.emit("TaskFinished");
        InOrder inOrder = Mockito.inOrder(signalMapper);
        inOrder.verify(signalMapper, times(1))
            .findBySignature("TaskFinished");
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testEmitHasParents() {
        Signal model = new Signal();
        model.setSignalId(1L);
        model.setSignature("TASK_FIN");
        model.setCountDown(1L);
        when(signalMapper.findBySignature(anyString())).thenReturn(model);
        Signal parent = new Signal();
        parent.setSignalId(2L);
        parent.setSignature("ALL_OF");
        parent.setCountDown(2L);
        when(signalMapper.findParents(1L)).thenReturn(Collections.singletonList(parent));
        when(signalMapper.update(any(Signal.class))).thenReturn(1);
        signalService.emit("TASK_FIN");
        InOrder inOrder = Mockito.inOrder(signalMapper);
        inOrder.verify(signalMapper, times(1))
            .findBySignature("TASK_FIN");
        inOrder.verify(signalMapper, times(1))
            .findParents(1L);
        inOrder.verify(signalMapper, times(1))
            .update(argThat(arg ->
                arg.getSignalId() == 2L
                    && arg.getSignature().equals("ALL_OF")
                    && arg.getCountDown() == 1
            ));
        inOrder.verify(signalMapper, times(1))
            .update(argThat(arg ->
                arg.getSignalId() == 1L
                    && arg.getSignature().equals("TASK_FIN")
                    && arg.getCountDown() == 0
            ));
        inOrder.verifyNoMoreInteractions();
    }
}
