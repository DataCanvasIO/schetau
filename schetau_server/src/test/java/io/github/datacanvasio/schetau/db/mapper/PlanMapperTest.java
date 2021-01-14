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
import io.github.datacanvasio.schetau.db.model.Plan;
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
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PlanMapperTest {
    private static List<Plan> modelList;
    private static List<Job> jobList;

    @Autowired
    private PlanMapper planMapper;

    @BeforeAll
    public static void setupAll() throws IOException {
        modelList = DataUtil.readCsv(
            PlanMapperTest.class.getResourceAsStream("/db/data/plan.csv"),
            Plan.class
        );
        jobList = DataUtil.readCsv(
            PlanMapperTest.class.getResourceAsStream("/db/data/job.csv"),
            Job.class
        );
    }

    private static void assertPlanJobs(@Nonnull Plan model) {
        switch (model.getPlanId().intValue()) {
            case 1:
                assertThat(model.getJobs().size()).isEqualTo(2);
                assertThat(model.getJobs())
                    .contains(jobList.get(0), jobList.get(1));
                break;
            case 2:
                assertThat(model.getJobs()).isEmpty();
                break;
            case 3:
            case 4:
                assertThat(model.getJobs().size()).isEqualTo(1);
                assertThat(model.getJobs())
                    .contains(jobList.get(0));
                break;
            default:
                break;
        }
    }

    @Test
    public void testFindAllWithJobs() {
        List<Plan> models = planMapper.findAllWithJobs();
        assertThat(models.size()).isEqualTo(4);
        assertThat(models)
            .contains(modelList.get(0))
            .contains(modelList.get(1))
            .contains(modelList.get(2))
            .contains(modelList.get(3));
        models.forEach(PlanMapperTest::assertPlanJobs);
    }

    @Test
    public void testFindByNextRunTimeBeforeWithJobs() {
        List<Plan> models = planMapper.findByNextRunTimeBeforeWithJobs(2000L);
        assertThat(models.size()).isEqualTo(2);
        assertThat(models).contains(modelList.get(0), modelList.get(1));
        models.forEach(PlanMapperTest::assertPlanJobs);
    }

    @Test
    public void testFindByNextRunTimeBeforeWithJobsHasExpired() {
        List<Plan> models = planMapper.findByNextRunTimeBeforeWithJobs(11000L);
        assertThat(models.size()).isEqualTo(3);
        assertThat(models).contains(modelList.get(0), modelList.get(2), modelList.get(3));
    }

    @Test
    public void testInsert() {
        Plan model = new Plan();
        model.setPlanName("test");
        model.setFirstRunTime(1000L);
        int n = planMapper.insert(model);
        assertThat(n).isEqualTo(1);
        assertThat(model.getPlanId()).isNotNull();
    }

    @Test
    public void testUpdate() {
        Plan model = new Plan();
        model.setPlanId(2L);
        model.setRunTimes(1L);
        model.setNextRunTime(9000L);
        int n = planMapper.update(model);
        assertThat(n).isEqualTo(1);
    }

    @Test
    public void testDelete() {
        int n = planMapper.deleteById(2L);
        assertThat(n).isEqualTo(1);
    }

    @Configuration
    @EnableAutoConfiguration
    @MapperScans({@MapperScan(basePackageClasses = {TaskMapper.class})})
    static class Config {
    }
}
