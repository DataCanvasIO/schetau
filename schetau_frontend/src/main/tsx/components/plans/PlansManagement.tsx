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
import { JSONSchema7 } from 'json-schema';

import { checkStatusHandler } from "../../apis/Api";
import { PlansApi } from "../../apis/PlansApi";
import { ProfilesApi } from "../../apis/ProfilesApi";

import { EntityList } from "../EntityList";

interface PlansManagementProps {
}

interface PlansManagementState {
    responseProfile: JSONSchema7;
}

export class PlansManagement extends React.Component<PlansManagementProps, PlansManagementState> {
    public constructor(props: PlansManagementProps) {
        super(props);
        this.state = {
            responseProfile: {},
        };
    }

    public componentDidMount(): void {
        ProfilesApi.get('PlanResponse', (err, res) => {
            console.log('err = ', err, ', res = ', res);
            if (!err) {
                this.setState({ responseProfile: res.body });
            }
        });
    }

    public render() {
        return (
            <EntityList
                profile={this.state.responseProfile}
                entityProvider={callback => PlansApi.listAll(checkStatusHandler(callback))}
            />
        );
    }
}
