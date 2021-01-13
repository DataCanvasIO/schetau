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

import * as request from "superagent";

import { API_URL_BASE } from "./Api";
import { ResponseHandler } from "./Api";

export class JobsApi {
    public static readonly BaseUrl = API_URL_BASE + '/jobs';

    public static listAll(callback?: ResponseHandler): void {
        request
            .get(JobsApi.BaseUrl)
            .send()
            .end(callback);
    }

    public static create(data: any, callback?: ResponseHandler): void {
        request
            .post(JobsApi.BaseUrl)
            .send(data)
            .end(callback);
    }

    public static update(id: any, data: any, callback?: ResponseHandler): void {
        request
            .put(JobsApi.BaseUrl + '/' + id)
            .send(data)
            .end(callback);
    }

    public static delete(id: any, callback?: ResponseHandler): void {
        request
            .delete(JobsApi.BaseUrl + '/' + id)
            .send()
            .end(callback);
    }
}
