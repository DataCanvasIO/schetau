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

import io.github.datacanvasio.schetau.db.model.Job;
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
public class JobMapperTest {
    private static List<Job> modelList;

    @Autowired
    private JobMapper jobMapper;

    @BeforeAll
    public static void setupAll() throws IOException {
        modelList = DataUtil.readCsv(
            JobMapperTest.class.getResourceAsStream("/db/data/job.csv"),
            Job.class
        );
    }

    @Test
    public void testFindById() {
        Job model = jobMapper.findById(2L);
        assertThat(model).isEqualTo(modelList.get(1));
    }

    @Test
    public void testInsert() {
        Job model = new Job();
        model.setJobName("test");
        model.setJobType("CmdLine");
        model.setExecutionInfo("ls");
        int n = jobMapper.insert(model);
        assertThat(n).isEqualTo(1);
        assertThat(model.getJobId()).isNotNull();
    }

    @Configuration
    @EnableAutoConfiguration
    @MapperScans({@MapperScan(basePackageClasses = {TaskMapper.class})})
    static class Config {
    }
}
