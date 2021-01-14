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
import { PlansApi } from "../../apis/PlansApi";
import { ProfilesApi } from "../../apis/ProfilesApi";

import { EntityList } from "../EntityList";
import { EntityDialog } from "../EntityDialog";
import { deleteButton, updateButton } from "../EntityUtils";

interface PlansManagementProps {
}

interface PlansManagementState {
    requestProfile: JSONSchema7;
    responseProfile: JSONSchema7;
    jobIdProfile: JSONSchema7;
}

export class PlansManagement extends React.Component<PlansManagementProps, PlansManagementState> {
    private lst: React.RefObject<EntityList> = React.createRef();
    private dlg: React.RefObject<EntityDialog> = React.createRef();
    private dlg_job: React.RefObject<EntityDialog> = React.createRef();

    public constructor(props: PlansManagementProps) {
        super(props);
        this.state = {
            requestProfile: {},
            responseProfile: {},
            jobIdProfile: {},
        };
    }

    @autobind
    private createOrUpdate(entity: any, id?: any) {
        if (id) {
            PlansApi.update(id, entity, this.lst.current?.refreshAfterChange());
        } else {
            PlansApi.create(entity, this.lst.current?.refreshAfterChange());
        }
    }

    @autobind
    private delete(id: any) {
        PlansApi.delete(id, this.lst.current?.refreshAfterChange());
    }

    @autobind
    private addRemoveJob(entity: any, id: any, flag: any) {
        if (flag === 'ADD') {
            PlansApi.addJob(id, entity, this.lst.current?.refreshAfterChange());
        } else if (flag === 'REMOVE') {
            PlansApi.removeJob(id, entity, this.lst.current?.refreshAfterChange());
        }
    }

    @autobind
    private handleOpenCreate() {
        this.dlg.current?.open('Create Plan', null);
    }

    @autobind
    private handleOpenUpdate(id: any, entity: any) {
        this.dlg.current?.open('Update Plan', entity, id);
    }

    @autobind
    private buttonOpenAddRemoveJob(title: string, id: any, flag: string) {
        return (
            <Button onClick={() => this.dlg_job.current?.open(title, null, id, flag)}>{title}</Button>
        );
    }

    public componentDidMount(): void {
        ProfilesApi.get('PlanResponse', data => this.setState({ responseProfile: data }));
        ProfilesApi.get('PlanRequest', data => this.setState({ requestProfile: data }));
        ProfilesApi.get('JobIdRequest', data => this.setState({ jobIdProfile: data }));
    }

    public render() {
        return (
            <React.Fragment>
                <Box>
                    <Button
                        variant="outlined"
                        color="primary"
                        onClick={this.handleOpenCreate}
                    > Create Plan </Button>
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
                            __add_job__: {
                                header: 'Add Job',
                                value: (id, _entity) => this.buttonOpenAddRemoveJob('Add Job', id, 'ADD'),
                            },
                            __remove_job__: {
                                header: 'Remove Job',
                                value: (id, _entity) => this.buttonOpenAddRemoveJob('Remove Job', id, 'REMOVE'),
                            },
                        }}
                        entityProvider={callback => PlansApi.listAll(checkStatusHandler(callback))}
                    />
                </Box>
                <EntityDialog
                    ref={this.dlg}
                    profile={this.state.requestProfile}
                    handleCreateUpdate={this.createOrUpdate}
                />
                <EntityDialog
                    ref={this.dlg_job}
                    profile={this.state.jobIdProfile}
                    handleCreateUpdate={this.addRemoveJob}
                />
            </React.Fragment>
        );
    }
}
