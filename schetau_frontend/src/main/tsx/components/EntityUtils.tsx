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

import * as React from "react";

import IconButton from "@material-ui/core/IconButton";
import EditIcon from "@material-ui/icons/Edit";
import DeleteIcon from "@material-ui/icons/Delete";

export function deleteButton(id: any, fun: (id: any) => void) {
    const onClick = () => {
        if (confirm('Are you sure to delete item (id = ' + id + ')?')) {
            fun(id);
        }
    }
    return (
        <IconButton onClick={onClick}><DeleteIcon /></IconButton>
    );
}

export function updateButton(id: any, entity: any, fun: (id: any, entity: any) => void) {
    return (
        <IconButton onClick={() => fun(id, entity)}><EditIcon /></IconButton>
    );
}
