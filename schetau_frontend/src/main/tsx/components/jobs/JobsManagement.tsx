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
import { autobind } from "core-decorators";
import { JSONSchema7 } from 'json-schema';

import Box from "@material-ui/core/Box";
import Button from "@material-ui/core/Button";

import { checkStatusHandler } from "../../apis/Api";
import { JobsApi } from "../../apis/JobsApi";
import { ProfilesApi } from "../../apis/ProfilesApi";

import { EntityList } from "../EntityList";
import { EntityDialog } from "../EntityDialog";
import { deleteButton, updateButton } from "../EntityUtils";

interface JobsManagementProps {
}

interface JobsManagementState {
    requestProfile: JSONSchema7;
    responseProfile: JSONSchema7;
}

export class JobsManagement extends React.Component<JobsManagementProps, JobsManagementState> {
    private lst: React.RefObject<EntityList> = React.createRef();
    private dlg: React.RefObject<EntityDialog> = React.createRef();

    public constructor(props: JobsManagementProps) {
        super(props);
        this.state = {
            requestProfile: {},
            responseProfile: {},
        };
    }

    @autobind
    private createOrUpdate(entity: any, id?: any) {
        if (id) {
            JobsApi.update(id, entity, this.lst.current?.refreshAfterChange());
        } else {
            JobsApi.create(entity, this.lst.current?.refreshAfterChange());
        }
    }

    @autobind
    private delete(id: any) {
        JobsApi.delete(id, this.lst.current?.refreshAfterChange());
    }

    @autobind
    private handleOpenCreate() {
        const entity = {
            type: 'CmdLine',
        };
        this.dlg.current?.open('Create Job', entity);
    }

    @autobind
    private handleOpenUpdate(id: any, entity: any) {
        this.dlg.current?.open('Update Job', entity, id);
    }

    public componentDidMount(): void {
        ProfilesApi.get('JobResponse', data => this.setState({ responseProfile: data }));
        ProfilesApi.get('JobRequest', data => this.setState({ requestProfile: data }));
    }

    public render() {
        return (
            <React.Fragment>
                <Box>
                    <Button
                        variant="outlined"
                        color="primary"
                        onClick={this.handleOpenCreate}
                    > Create Job </Button>
                    <EntityList
                        ref={this.lst}
                        profile={this.state.responseProfile}
                        additinalColumns={{
                            __update__: {
                                header: 'Edit',
                                value: (id, entity) => updateButton(id, entity, this.handleOpenUpdate),
                            },
                            __delete__: {
                                header: 'Delete',
                                value: (id, _entity) => deleteButton(id, this.delete),
                            },
                        }}
                        entityProvider={callback => JobsApi.listAll(checkStatusHandler(callback))}
                    />
                </Box>
                <EntityDialog
                    ref={this.dlg}
                    profile={this.state.requestProfile}
                    handleCreateUpdate={this.createOrUpdate}
                />
            </React.Fragment>
        );
    }
}
