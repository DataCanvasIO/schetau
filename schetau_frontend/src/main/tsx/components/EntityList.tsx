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
import { autobind } from 'core-decorators';

import Table from "@material-ui/core/Table";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import TableCell from "@material-ui/core/TableCell";
import TableBody from "@material-ui/core/TableBody";

interface Entities {
    [id: string]: any;
}

interface EntityListProps {
    profile: any;
    entityProvider: EntityProvider;
    valueMappings?: {
        [column: string]: (value: any) => any;
    }
}

interface EntityListState {
    entities: Entities;
}

export type EntityProvider = (callback: (data: any) => void) => void;

export class EntityList extends React.Component<EntityListProps, EntityListState> {
    public constructor(props: EntityListProps) {
        super(props);
        this.state = { entities: {} };
    }

    @autobind
    public setEntitys(data: any): void {
        const entities: Entities = {};
        for (const item of data) {
            entities[item.id] = item;
        }
        this.setState({ entities: entities });
    }

    @autobind
    public refresh(): void {
        this.props.entityProvider(data => this.setEntitys(data));
    }

    public componentDidMount(): void {
        this.refresh();
    }

    public render() {
        const heads = [];
        for (const key in this.props.profile.properties) {
            heads.push(<TableCell key={key}>{key}</TableCell>);
        }
        const rows = [];
        for (const id in this.state.entities) {
            const entity = this.state.entities[id];
            const cells = [];
            for (const key in this.props.profile.properties) {
                var value = entity[key];
                if (this.props.valueMappings) {
                    const mapping = this.props.valueMappings[key];
                    if (mapping) {
                        value = mapping(value);
                    }
                }
                cells.push(<TableCell key={key}>{value}</TableCell>);
            }
            rows.push(<TableRow key={id}>{cells}</TableRow>);
        }
        return (
            <Table>
                <TableHead>
                    <TableRow>{heads}</TableRow>
                </TableHead>
                <TableBody>
                    {rows}
                </TableBody>
            </Table>
        );
    }
}
