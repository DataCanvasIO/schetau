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

import * as React from 'react';
import { autobind } from 'core-decorators';
import { ISubmitEvent } from '@rjsf/core';

import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import MuiForm from "@rjsf/material-ui";
import Box from '@material-ui/core/Box';

interface EntityDialogProps {
    profile: any;
    entityName: string;
    handleCreateUpdate: (entity: any, id?: any) => void;
}

interface EntityDialogState {
    isOpen: boolean;
    id?: any;
    data?: any;
}

export class EntityDialog extends React.Component<EntityDialogProps, EntityDialogState> {
    public constructor(props: EntityDialogProps) {
        super(props);
        this.state = {
            isOpen: false,
        };
    }

    @autobind
    public open(entity: any, id?: any): void {
        this.setState({
            isOpen: true,
            id: id,
            data: entity,
        });
    }

    @autobind
    private handleClose(): void {
        this.setState({
            isOpen: false
        });
    }

    @autobind
    private handleSubmit(event: ISubmitEvent<any>): void {
        const entity = event.formData;
        this.props.handleCreateUpdate(entity, this.state.id);
        this.handleClose();
    }

    public render() {
        let dlgContent;
        if (this.props.profile) {
            dlgContent = (
                <MuiForm
                    schema={this.props.profile}
                    formData={this.state.data}
                    onSubmit={this.handleSubmit}
                >
                    <DialogActions>
                        <Button type="submit" color="primary">Submit</Button>
                        <Button onClick={this.handleClose}>Cancel</Button>
                    </DialogActions>
                </MuiForm>
            );
        } else {
            dlgContent = (
                <Box />
            );
        }
        return (
            <Dialog disableBackdropClick open={this.state.isOpen} onClose={this.handleClose}>
                <DialogTitle>{this.state.id ? 'Update' : 'Create'} {this.props.entityName}</DialogTitle>
                <DialogContent>{dlgContent}</DialogContent>
            </Dialog>
        );
    }
}
