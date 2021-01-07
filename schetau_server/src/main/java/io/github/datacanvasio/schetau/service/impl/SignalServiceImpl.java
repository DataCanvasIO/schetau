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

import io.github.datacanvasio.expretau.exception.ExpretauCompileException;
import io.github.datacanvasio.expretau.exception.ExpretauParseException;
import io.github.datacanvasio.expretau.parser.ExpretauCompiler;
import io.github.datacanvasio.expretau.runtime.exception.FailGetEvaluator;
import io.github.datacanvasio.schetau.db.mapper.SignalMapper;
import io.github.datacanvasio.schetau.db.mapper.SignalRelationMapper;
import io.github.datacanvasio.schetau.db.model.Signal;
import io.github.datacanvasio.schetau.service.SignalService;
import io.github.datacanvasio.schetau.service.dto.SignalDto;
import io.github.datacanvasio.schetau.service.dto.TaskDto;
import io.github.datacanvasio.schetau.service.mapper.SignalDtoMapper;
import io.github.datacanvasio.schetau.signal.SignalTreeNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Service
public class SignalServiceImpl implements SignalService {
    @Autowired
    private SignalMapper signalMapper;
    @Autowired
    private SignalRelationMapper signalRelationMapper;

    private void countDownRecursive(@Nonnull SignalDto signal) {
        if (signal.getCountDown() > 0) {
            signal.setCountDown(signal.getCountDown() - 1);
            if (signal.getCountDown() == 0) {
                List<SignalDto> signals = SignalDtoMapper.MAPPER.fromModels(
                    signalMapper.findParents(signal.getId())
                );
                for (SignalDto s : signals) {
                    countDownRecursive(s);
                }
            }
            signalMapper.update(SignalDtoMapper.MAPPER.toModel(signal));
        }
    }

    private SignalDto saveSignal(String signature, long countDown) {
        SignalDto signal = SignalDtoMapper.MAPPER.fromModel(signalMapper.findBySignature(signature));
        if (signal == null) {
            signal = new SignalDto();
            signal.setSignature(signature);
            signal.setCountDown(countDown);
            Signal model = SignalDtoMapper.MAPPER.toModel(signal);
            signalMapper.insert(model);
            return SignalDtoMapper.MAPPER.fromModel(model);
        }
        // Do not update count down, for the existing signal may emitted already.
        return signal;
    }

    /**
     * Recursively save a signal tree.
     *
     * @param tree the signal tree
     * @return the signal id of root
     */
    @Nullable
    private SignalDto saveSignalTree(SignalTreeNode tree) {
        if (tree == null) {
            return null;
        }
        Set<SignalTreeNode> childrenNodes = tree.getChildren();
        SignalDto signal;
        if (childrenNodes.size() == 0) {
            return saveSignal(tree.getSignature(), 1);
        } else {
            Map<Long, SignalDto> children = new HashMap<>(childrenNodes.size());
            for (SignalTreeNode node : tree.getChildren()) {
                SignalDto child = saveSignalTree(node);
                if (child != null) {
                    children.put(child.getId(), child);
                }
            }
            if (children.size() == 1) {
                for (SignalDto child : children.values()) {
                    return child;
                }
            }
            String signature = tree.getSignature();
            long countDown = 0;
            if (signature.equals("ANY_OF")) {
                countDown = children.values().stream().anyMatch(c -> c.getCountDown() <= 0) ? 0 : 1;
            } else if (signature.equals("ALL_OF")) {
                countDown = children.values().stream().filter(c -> c.getCountDown() > 0).count();
            }
            signature += '_' + children.keySet().stream()
                .map(Object::toString)
                .sorted()
                .collect(Collectors.joining("_"));
            signal = saveSignal(signature, countDown);
            for (Long cid : children.keySet()) {
                signalRelationMapper.insert(signal.getId(), cid);
            }
        }
        return signal;
    }

    @Transactional
    @Override
    public void emit(String signature) {
        SignalDto signal = SignalDtoMapper.MAPPER.fromModel(signalMapper.findBySignature(signature));
        if (signal == null) {
            signal = new SignalDto();
            signal.setSignature(signature);
            signal.setCountDown(0L);
            signalMapper.insert(SignalDtoMapper.MAPPER.toModel(signal));
        } else {
            countDownRecursive(signal);
        }
    }

    @Transactional
    @Override
    public Long evalAndSaveSignalTree(String signalDefinition, TaskDto task) {
        try {
            // TODO context
            SignalTreeNode tree = (SignalTreeNode) ExpretauCompiler.INS
                .parse(signalDefinition)
                .compileIn(null).eval(null);
            SignalDto signal = saveSignalTree(tree);
            if (signal != null) {
                return signal.getId();
            }
        } catch (ExpretauParseException | ExpretauCompileException | FailGetEvaluator e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
