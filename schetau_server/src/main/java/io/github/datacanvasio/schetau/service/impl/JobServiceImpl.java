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

package io.github.datacanvasio.schetau.service.impl;

import io.github.datacanvasio.schetau.db.mapper.JobMapper;
import io.github.datacanvasio.schetau.db.model.Job;
import io.github.datacanvasio.schetau.service.JobService;
import io.github.datacanvasio.schetau.service.dto.JobDto;
import io.github.datacanvasio.schetau.service.mapper.JobDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private JobMapper jobMapper;

    @Override
    public JobDto create(JobDto job) {
        Job model = JobDtoMapper.MAPPER.toModel(job);
        jobMapper.insert(model);
        return JobDtoMapper.MAPPER.fromModel(model);
    }

    @Override
    public JobDto getById(Long id) {
        return JobDtoMapper.MAPPER.fromModel(jobMapper.findById(id));
    }

    @Override
    public List<JobDto> listAll() {
        return JobDtoMapper.MAPPER.formModels(jobMapper.findAll());
    }

    @Override
    public void update(JobDto job) {
        jobMapper.update(JobDtoMapper.MAPPER.toModel(job));
    }

    @Override
    public void delete(Long id) {
        jobMapper.deleteById(id);
    }
}
