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

import java.util.Arrays;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public final class CustomSignalFun extends SignalFun {
    private static final long serialVersionUID = -4557438858665746863L;

    protected CustomSignalFun(@Nonnull RtExpr[] paras) {
        super(paras);
    }

    @Nonnull
    public static String signature(@Nonnull Object... values) {
        return "CUSTOM_" + Arrays.stream(values)
            .map(Object::toString)
            .collect(Collectors.joining("_"));
    }

    @Nonnull
    @Override
    protected Object fun(@Nonnull Object[] values) {
        SignalTreeNode node = new SignalTreeNode();
        node.setSignature(signature(values));
        return node;
    }
}
