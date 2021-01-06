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

public final class TaskFinishedSignalFun extends SignalFun {
    private static final long serialVersionUID = -6820957830415019156L;

    protected TaskFinishedSignalFun(@Nonnull RtExpr[] paras) {
        super(paras);
    }

    @Nonnull
    public static String signature(long planId, long jobId, long runTimes) {
        return "TASK_FIN"
            + "_" + String.format("%04d", planId)
            + "_" + String.format("%04d", jobId)
            + "_" + String.format("%04d", runTimes);
    }

    @Nonnull
    @Override
    protected Object fun(@Nonnull Object[] values) {
        SignalTreeNode node = new SignalTreeNode();
        node.setSignature(signature((long) values[0], (long) values[1], (long) values[2]));
        return node;
    }
}
