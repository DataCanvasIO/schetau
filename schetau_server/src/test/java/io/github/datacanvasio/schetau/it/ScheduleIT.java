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

package io.github.datacanvasio.schetau.it;

import io.github.datacanvasio.schetau.db.mapper.PlanJobMapper;
import io.github.datacanvasio.schetau.db.mapper.TaskMapper;
import io.github.datacanvasio.schetau.service.JobService;
import io.github.datacanvasio.schetau.service.PlanService;
import io.github.datacanvasio.schetau.service.RunService;
import io.github.datacanvasio.schetau.service.SchetauService;
import io.github.datacanvasio.schetau.service.dto.JobDto;
import io.github.datacanvasio.schetau.service.dto.PlanDto;
import io.github.datacanvasio.schetau.service.dto.TaskDto;
import io.github.datacanvasio.schetau.service.impl.SchetauServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("it")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
public class ScheduleIT {
    @Autowired
    private SchetauService schetauService;
    @Autowired
    private PlanService planService;
    @Autowired
    private JobService jobService;
    @Autowired
    private RunService runService;

    @Autowired
    private PlanJobMapper planJobMapper;

    private HistoryRunner history;

    @BeforeEach
    public void setupAll() {
        history = new HistoryRunner();
        runService.registerRunner("Test", history);
    }

    @Test
    public void testSingleShot() {
        PlanDto plan = new PlanDto();
        plan.setName("SingleShot");
        plan.setFirstRunTime(3000L);
        plan = planService.create(plan);
        JobDto job = new JobDto();
        job.setName("history");
        job.setType("Test");
        job.setExecutionInfo("single shot");
        job = jobService.create(job);
        planJobMapper.insert(plan.getId(), job.getId());
        for (long time = 0; time < 10000L; time++) {
            schetauService.schedule(time);
        }
        List<TaskDto> tasks = history.getTasks();
        assertThat(tasks.size()).isEqualTo(1);
        assertThat(tasks.get(0).getRunTime()).isEqualTo(3000L);
    }

    @Test
    public void testRepeatedly() {
        PlanDto plan = new PlanDto();
        plan.setName("Repeatedly");
        plan.setFirstRunTime(3000L);
        plan.setRunInterval(2000L);
        plan.setExpireTime(8000L);
        plan = planService.create(plan);
        JobDto job = new JobDto();
        job.setName("history");
        job.setType("Test");
        job.setExecutionInfo("repeatedly");
        job = jobService.create(job);
        planJobMapper.insert(plan.getId(), job.getId());
        for (long time = 0; time < 10000L; time++) {
            schetauService.schedule(time);
        }
        List<TaskDto> tasks = history.getTasks();
        assertThat(tasks.size()).isEqualTo(3);
        assertThat(tasks.get(0).getRunTime()).isEqualTo(3000L);
        assertThat(tasks.get(1).getRunTime()).isEqualTo(5000L);
        assertThat(tasks.get(2).getRunTime()).isEqualTo(7000L);
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableTransactionManagement
    @ComponentScan(basePackageClasses = {SchetauServiceImpl.class})
    @MapperScans({@MapperScan(basePackageClasses = {TaskMapper.class})})
    static class Config {
    }
}
