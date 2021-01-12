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

package io.github.datacanvasio.schetau.controller;

import io.github.datacanvasio.schetau.config.WebMvcConfigDev;
import io.github.datacanvasio.schetau.controller.advise.GlobalExceptionHandler;
import io.github.datacanvasio.schetau.controller.advise.ResponseBodyDecorator;
import io.github.datacanvasio.schetau.service.NodeService;
import io.github.datacanvasio.schetau.service.dto.NodeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static io.github.datacanvasio.schetau.controller.WebMvcTestUtils.success;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = {NodeController.class})
public class NodeControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private NodeService nodeService;

    @Test
    public void testListAll() throws Exception {
        NodeDto node = new NodeDto();
        node.setId("069F56DF-76CB-4211-B15B-B63DA87CE05E");
        node.setHostAddress("127.0.0.1");
        node.setHostName("localhost");
        when(nodeService.me()).thenReturn(node);
        when(nodeService.listAll()).thenReturn(Collections.singletonList(node));
        mvc.perform(
            get("/api/nodes")
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andDo(print())
            .andExpect(success())
            .andExpect(jsonPath("$.data[0].id").value("069F56DF-76CB-4211-B15B-B63DA87CE05E"))
            .andExpect(jsonPath("$.data[0].host_address").value("127.0.0.1"))
            .andExpect(jsonPath("$.data[0].host_name").value("localhost"))
            .andExpect(jsonPath("$.data[0].is_me").value(true));
        verify(nodeService, times(1)).listAll();
        verify(nodeService, times(1)).me();
        verifyNoMoreInteractions(nodeService);
    }

    // Mock application
    @Configuration
    @EnableAutoConfiguration
    @Import({
        NodeController.class,
        ResponseBodyDecorator.class,
        GlobalExceptionHandler.class,
        WebMvcConfigDev.class,
    })
    static class Config {
    }
}
