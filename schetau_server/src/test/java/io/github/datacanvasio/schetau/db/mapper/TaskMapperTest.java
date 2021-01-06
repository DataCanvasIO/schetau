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

package io.github.datacanvasio.schetau.db.mapper;

import io.github.datacanvasio.schetau.db.model.Task;
import io.github.datacanvasio.schetau.db.model.TaskStatus;
import io.github.datacanvasio.schetau.util.DataUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaskMapperTest {
    private static List<Task> modelList;

    @Autowired
    private TaskMapper taskMapper;

    @BeforeAll
    public static void setupAll() throws IOException {
        modelList = DataUtil.readCsv(
            JobMapperTest.class.getResourceAsStream("/db/data/task.csv"),
            Task.class
        );
    }

    @Test
    public void testFindByStatus() {
        List<Task> models = taskMapper.findByStatus(TaskStatus.READY);
        assertThat(models.size()).isEqualTo(3);
        assertThat(models).contains(modelList.get(1), modelList.get(2), modelList.get(3));
    }

    @Test
    public void testInsert() {
        Task model = new Task();
        model.setPlanId(2L);
        model.setJobId(2L);
        model.setCreateTime(8L);
        model.setRunTimes(2L);
        model.setWaitTimeout(0L);
        model.setTaskStatus(TaskStatus.READY);
        int n = taskMapper.insert(model);
        assertThat(n).isEqualTo(1);
        assertThat(model.getTaskId()).isNotNull();
    }

    @Test
    public void testUpdate() {
        Task model = new Task();
        model.setTaskId(1L);
        model.setTaskStatus(TaskStatus.FINISHED);
        int n = taskMapper.update(model);
        assertThat(n).isEqualTo(1);
    }

    @Test
    public void testUpdateWaitingToExpiredByTime() {
        int n = taskMapper.updateWaitingToExpiredByTime(7000L);
        assertThat(n).isEqualTo(1);
    }

    @Test
    public void testUpdateWaitingToReadyBySignals() {
        int n = taskMapper.updateWaitingToReadyBySignals();
        assertThat(n).isEqualTo(1);
    }

    @Configuration
    @EnableAutoConfiguration
    @MapperScans({@MapperScan(basePackageClasses = {TaskMapper.class})})
    static class Config {
    }
}
