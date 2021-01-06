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

package io.github.datacanvasio.schetau.signal;

import io.github.datacanvasio.expretau.runtime.RtExpr;

import javax.annotation.Nonnull;

public abstract class CompositeSignalFun extends SignalFun {
    private static final long serialVersionUID = -3169278821089824359L;

    protected CompositeSignalFun(@Nonnull RtExpr[] paras) {
        super(paras);
    }

    @Override
    protected Object fun(@Nonnull Object[] values) {
        SignalTreeNode node = new SignalTreeNode();
        for (Object value : values) {
            node.addChild((SignalTreeNode) value);
        }
        node.setSignature(signature());
        return node;
    }

    protected abstract String signature();
}
