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

import io.github.datacanvasio.schetau.db.mapper.ActivityMapper;
import io.github.datacanvasio.schetau.service.ActivityService;
import io.github.datacanvasio.schetau.service.NodeService;
import io.github.datacanvasio.schetau.service.dto.ActivityDto;
import io.github.datacanvasio.schetau.service.dto.NodeDto;
import io.github.datacanvasio.schetau.service.mapper.ActivityDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {
    @Value("#{${schetau.server.inactive.seconds:2}*1000}")
    private long maxInactiveTime;

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private NodeService nodeService;

    @Transactional
    @Override
    public boolean check(long currentTime) {
        ActivityDto activity = ActivityDtoMapper.MAPPER.fromModel(activityMapper.findMaxSeqNo());
        final NodeDto me = nodeService.me();
        if (activity == null) {
            activity = new ActivityDto();
            activity.setSeqNo(0L);
        } else if (activity.getNodeId().equals(me.getId())) {
            activity.setLastUpdatingTime(currentTime);
            if (activityMapper.update(ActivityDtoMapper.MAPPER.toModel(activity)) > 0) {
                return true;
            }
        } else if (maxInactiveTime == 0 || currentTime - activity.getLastUpdatingTime() <= maxInactiveTime) {
            return false;
        }
        nodeService.save();
        activity.setSeqNo(activity.getSeqNo() + 1);
        activity.setNodeId(me.getId());
        activity.setLastUpdatingTime(currentTime);
        if (activityMapper.insert(ActivityDtoMapper.MAPPER.toModel(activity)) > 0) {
            log.info("Active node changed to: {}", me);
            return true;
        }
        return false;
    }
}
