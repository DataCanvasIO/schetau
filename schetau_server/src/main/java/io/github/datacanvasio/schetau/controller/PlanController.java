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

import io.github.datacanvasio.schetau.controller.mapper.PlanRequestMapper;
import io.github.datacanvasio.schetau.controller.mapper.PlanResponseMapper;
import io.github.datacanvasio.schetau.controller.request.PlanRequest;
import io.github.datacanvasio.schetau.controller.response.PlanResponse;
import io.github.datacanvasio.schetau.service.PlanService;
import io.github.datacanvasio.schetau.service.dto.PlanDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Plan APIs")
@RestController
@RequestMapping("/plans")
public class PlanController {
    @Autowired
    private PlanService planService;

    @Operation(summary = "List all plans.")
    @GetMapping("")
    public List<PlanResponse> listAll() {
        return PlanResponseMapper.MAPPER.fromPlans(planService.listAll());
    }

    @Operation(summary = "Create a plan.")
    @PostMapping("")
    public PlanResponse create(@RequestBody PlanRequest planRequest) {
        PlanDto plan = PlanRequestMapper.MAPPER.toPlan(planRequest);
        plan = planService.create(plan);
        return PlanResponseMapper.MAPPER.fromPlan(plan);
    }

    @Operation(summary = "Update a plan.")
    @PutMapping("/{id}")
    public void update(@PathVariable("id") Long id, @RequestBody PlanRequest planRequest) {
        PlanDto plan = PlanRequestMapper.MAPPER.toPlan(planRequest);
        plan.setId(id);
        planService.update(plan);
    }

    @Operation(summary = "Delete a plan")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        planService.delete(id);
    }
}
