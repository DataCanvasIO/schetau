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

package io.github.datacanvasio.schetau.boot;

import io.github.datacanvasio.schetau.db.mapper.TaskMapper;
import io.github.datacanvasio.schetau.service.impl.SchetauServiceImpl;
import io.github.datacanvasio.schetau.signal.SignalFun;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackageClasses = {
    SchetauServiceImpl.class
})
@EnableScheduling
@EnableTransactionManagement
@MapperScans({@MapperScan(basePackageClasses = {TaskMapper.class})})
public class WebAppInitializer extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SignalFun.register();
        SpringApplication.run(WebAppInitializer.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(WebAppInitializer.class);
    }
}
