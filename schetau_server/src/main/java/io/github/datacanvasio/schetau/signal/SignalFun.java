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

import io.github.datacanvasio.expretau.op.FunFactory;
import io.github.datacanvasio.expretau.runtime.RtExpr;
import io.github.datacanvasio.expretau.runtime.TypeCode;
import io.github.datacanvasio.expretau.runtime.op.RtFun;

import javax.annotation.Nonnull;

public abstract class SignalFun extends RtFun {
    private static final long serialVersionUID = -579372051213990169L;

    protected SignalFun(@Nonnull RtExpr[] paras) {
        super(paras);
    }

    public static void register() {
        FunFactory.INS.registerUdf("AllOf", AllOfSignalFun::new);
        FunFactory.INS.registerUdf("AnyOf", AnyOfSignalFun::new);
        FunFactory.INS.registerUdf("TaskFinished", TaskFinishedSignalFun::new);
    }

    @Override
    public int typeCode() {
        return TypeCode.OBJECT;
    }
}
