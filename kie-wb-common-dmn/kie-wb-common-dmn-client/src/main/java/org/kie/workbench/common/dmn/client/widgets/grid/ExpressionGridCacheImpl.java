/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.widgets.grid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ExpressionGridCacheImpl implements ExpressionGridCache {

    private Map<String, Optional<BaseExpressionGrid>> cache = new HashMap<>();

    @Override
    public Optional<BaseExpressionGrid> getExpressionGrid(final String nodeUUID) {
        return cache.containsKey(nodeUUID) ? cache.get(nodeUUID) : Optional.empty();
    }

    @Override
    public void putExpressionGrid(final String nodeUUID,
                                  final Optional<BaseExpressionGrid> gridWidget) {
        if (gridWidget.isPresent()) {
            if (gridWidget.get().isCacheable()) {
                cache.put(nodeUUID, gridWidget);
            }
        }
    }

    @Override
    public void removeExpressionGrid(final String nodeUUID) {
        cache.remove(nodeUUID);
    }

    //Package-protected for Unit Tests
    Map<String, Optional<BaseExpressionGrid>> getContent() {
        return cache;
    }
}
