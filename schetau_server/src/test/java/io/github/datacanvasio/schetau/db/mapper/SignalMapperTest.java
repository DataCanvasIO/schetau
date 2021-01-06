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

import io.github.datacanvasio.schetau.db.model.Signal;
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
public class SignalMapperTest {
    private static List<Signal> modelList;

    @Autowired
    private SignalMapper signalMapper;

    @BeforeAll
    public static void setupAll() throws IOException {
        modelList = DataUtil.readCsv(
            JobMapperTest.class.getResourceAsStream("/db/data/signal.csv"),
            Signal.class
        );
    }

    @Test
    public void testFindBySignature() {
        Signal model = signalMapper.findBySignature("TASK_FIN_0001_0001_0001");
        assertThat(model).isEqualTo(modelList.get(0));
    }

    @Test
    public void testFindParents() {
        List<Signal> models = signalMapper.findParents(3L);
        assertThat(models.size()).isEqualTo(2);
        assertThat(models).contains(modelList.get(4), modelList.get(5));
    }

    @Test
    public void testInsert() {
        Signal model = new Signal();
        model.setSignature("TaskFinished");
        model.setCountDown(1L);
        int n = signalMapper.insert(model);
        assertThat(n).isEqualTo(1);
    }

    @Test
    public void testUpdate() {
        Signal model = new Signal();
        model.setSignalId(3L);
        model.setCountDown(0L);
        int n = signalMapper.update(model);
        assertThat(n).isEqualTo(1);
    }

    @Configuration
    @EnableAutoConfiguration
    @MapperScans({@MapperScan(basePackageClasses = {TaskMapper.class})})
    static class Config {
    }
}
