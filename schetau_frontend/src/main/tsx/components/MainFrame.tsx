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

import styles from "style/main.scss";

import * as React from 'react';
import { autobind } from 'core-decorators';

import Paper from '@material-ui/core/Paper';
import Box from '@material-ui/core/Box';
import Drawer from '@material-ui/core/Drawer';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemText from '@material-ui/core/ListItemText';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';

enum Management {
    ManageNodes,
    ManageJobs,
    ManagePlans,
}

interface MainFrameProps {
}

interface MainFrameState {
    management: Management;
}

export class MainFrame extends React.Component<MainFrameProps, MainFrameState> {
    public constructor(props: MainFrameProps) {
        super(props);
        this.state = {
            management: Management.ManageJobs,
        };
    }

    @autobind
    private handleOpenNodesManagement(): void {
        this.setState({
            management: Management.ManageNodes,
        });
    }

    @autobind
    private handleOpenJobsManagement(): void {
        this.setState({
            management: Management.ManageJobs,
        });
    }

    @autobind
    private handleOpenPlansManagement(): void {
        this.setState({
            management: Management.ManagePlans,
        });
    }

    @autobind
    private getMainPage() {
        return <Box />;
    }

    public render() {
        return (
            <Paper>
                <AppBar position="sticky" className={styles['bar']}>
                    <Toolbar>
                        <Typography variant="h3">ScheTau</Typography>
                    </Toolbar>
                </AppBar>
                <Drawer
                    variant="permanent"
                    anchor="left"
                    className={styles['drawer']}
                    classes={{ paper: styles['drawer-paper'] }}
                >
                    <Toolbar></Toolbar>
                    <List component="nav">
                        <ListItem button onClick={this.handleOpenNodesManagement}>
                            <ListItemText primary="Nodes" />
                        </ListItem>
                        <ListItem button onClick={this.handleOpenJobsManagement}>
                            <ListItemText primary="Jobs" />
                        </ListItem>
                        <ListItem button onClick={this.handleOpenPlansManagement}>
                            <ListItemText primary="Plans" />
                        </ListItem>
                    </List>
                </Drawer>
                {this.getMainPage()}
            </Paper>
        );
    }
}
