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

import CheckIcon from '@material-ui/icons/Check';

import { checkStatusHandler } from "../../apis/Api"
import { NodesApi } from "../../apis/NodesApi";
import { ProfilesApi } from "../../apis/ProfilesApi";

import { EntityList } from "../EntityList";

interface NodesManagementProps {
}

interface NodesManagementState {
    profile: any;
}

export class NodesManagement extends React.Component<NodesManagementProps, NodesManagementState> {
    public constructor(props: NodesManagementProps) {
        super(props);
        this.state = { profile: {} };
    }

    public componentDidMount(): void {
        ProfilesApi.get('NodeResponse', (err, res) => {
            console.log('err = ', err, ', res = ', res);
            if (!err) {
                this.setState({ profile: res.body });
            }
        });
    }

    public render() {
        return (
            <EntityList
                profile={this.state.profile}
                entityProvider={callback => NodesApi.listAll(checkStatusHandler(callback))}
                valueMappings={{
                    'is_me': value => value ? <CheckIcon /> : '',
                }}
            />
        );
    }
}
