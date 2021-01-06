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
import io.github.datacanvasio.schetau.signal.SignalFun;
import io.github.datacanvasio.schetau.signal.TaskFinishedSignalFun;
import org.junit.jupiter.api.BeforeAll;
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

import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {
    SignalServiceImpl.class,
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SignalServiceImplEvalTest {
    @Autowired
    private SignalService signalService;

    @MockBean
    private SignalMapper signalMapper;
    @MockBean
    private SignalRelationMapper signalRelationMapper;

    @BeforeAll
    public static void setupAll() {
        SignalFun.register();
    }

    @Test
    public void testTaskFinished() {
        String signature = TaskFinishedSignalFun.signature(1L, 2L, 1L);
        when(signalMapper.findBySignature(signature)).thenReturn(null);
        when(signalMapper.insert(any(Signal.class))).thenReturn(1);
        String signalDefinition = "TaskFinished(1, 2, 1)";
        signalService.evalAndSaveSignalTree(signalDefinition, null);
        InOrder inOrder = Mockito.inOrder(signalMapper);
        inOrder.verify(signalMapper, times(1))
            .findBySignature(signature);
        inOrder.verify(signalMapper, times(1))
            .insert(argThat(arg -> arg.getSignature().equals(signature) && arg.getCountDown() == 1));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAllOf() {
        final String signature1 = TaskFinishedSignalFun.signature(1L, 2L, 1L);
        when(signalMapper.findBySignature(signature1)).thenReturn(null);
        when(signalMapper.insert(any(Signal.class))).then(args -> {
            Signal model = args.getArgument(0);
            if (model.getSignature().equals(signature1)) {
                model.setSignalId(1L);
            } else if (model.getSignature().equals("ALL_OF_1_2")) {
                model.setSignalId(3L);
            }
            return 1;
        });
        final String signature2 = TaskFinishedSignalFun.signature(1L, 2L, 2L);
        Signal signal2 = new Signal();
        signal2.setSignalId(2L);
        signal2.setSignature(signature2);
        signal2.setCountDown(0L);
        when(signalMapper.findBySignature(signature2)).thenReturn(signal2);
        String signalDefinition = "AllOf(TaskFinished(1, 2, 1), TaskFinished(1, 2, 2))";
        signalService.evalAndSaveSignalTree(signalDefinition, null);
        InOrder inOrder = Mockito.inOrder(signalMapper, signalRelationMapper);
        inOrder.verify(signalMapper, times(1))
            .findBySignature(signature1);
        inOrder.verify(signalMapper, times(1))
            .insert(argThat(arg -> arg.getSignature().equals(signature1) && arg.getCountDown() == 1));
        inOrder.verify(signalMapper, times(1))
            .findBySignature(signature2);
        inOrder.verify(signalMapper, times(1))
            .findBySignature("ALL_OF_1_2");
        inOrder.verify(signalMapper, times(1))
            .insert(argThat(arg -> arg.getSignature().equals("ALL_OF_1_2") && arg.getCountDown() == 1));
        inOrder.verify(signalRelationMapper, times(2))
            .insert(eq(3L), or(eq(1L), eq(2L)));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testAnyOf() {
        final String signature1 = TaskFinishedSignalFun.signature(1L, 2L, 1L);
        when(signalMapper.findBySignature(signature1)).thenReturn(null);
        when(signalMapper.insert(any(Signal.class))).then(args -> {
            Signal model = args.getArgument(0);
            if (model.getSignature().equals(signature1)) {
                model.setSignalId(1L);
            } else if (model.getSignature().equals("ANY_OF_1_2")) {
                model.setSignalId(3L);
            }
            return 1;
        });
        final String signature2 = TaskFinishedSignalFun.signature(1L, 2L, 2L);
        Signal signal2 = new Signal();
        signal2.setSignalId(2L);
        signal2.setSignature(signature2);
        signal2.setCountDown(0L);
        when(signalMapper.findBySignature(signature2)).thenReturn(signal2);
        String signalDefinition = "AnyOf(TaskFinished(1, 2, 1), TaskFinished(1, 2, 2))";
        signalService.evalAndSaveSignalTree(signalDefinition, null);
        InOrder inOrder = Mockito.inOrder(signalMapper, signalRelationMapper);
        inOrder.verify(signalMapper, times(1))
            .findBySignature(signature1);
        inOrder.verify(signalMapper, times(1))
            .insert(argThat(arg -> arg.getSignature().equals(signature1) && arg.getCountDown() == 1));
        inOrder.verify(signalMapper, times(1))
            .findBySignature(signature2);
        inOrder.verify(signalMapper, times(1))
            .findBySignature("ANY_OF_1_2");
        inOrder.verify(signalMapper, times(1))
            .insert(argThat(arg -> arg.getSignature().equals("ANY_OF_1_2") && arg.getCountDown() == 0));
        inOrder.verify(signalRelationMapper, times(2))
            .insert(eq(3L), or(eq(1L), eq(2L)));
        inOrder.verifyNoMoreInteractions();
    }
}
