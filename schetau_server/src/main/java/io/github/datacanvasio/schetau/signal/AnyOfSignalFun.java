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

public final class AnyOfSignalFun extends CompositeSignalFun {
    private static final long serialVersionUID = 9174981114820402649L;

    protected AnyOfSignalFun(@Nonnull RtExpr[] paras) {
        super(paras);
    }

    @Nonnull
    @Override
    protected String signature() {
        return "ANY_OF";
    }
}
