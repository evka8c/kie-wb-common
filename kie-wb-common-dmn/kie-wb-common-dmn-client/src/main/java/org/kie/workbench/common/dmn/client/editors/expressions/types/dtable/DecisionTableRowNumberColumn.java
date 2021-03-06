/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.dtable;

import java.util.Collections;
import java.util.function.Supplier;

import org.kie.workbench.common.dmn.api.definition.v1_1.BuiltinAggregator;
import org.kie.workbench.common.dmn.api.definition.v1_1.HitPolicy;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.hitpolicy.HitPolicyEditorView;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.container.CellEditorControlsView;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

public class DecisionTableRowNumberColumn extends RowNumberColumn {

    public DecisionTableRowNumberColumn(final Supplier<HitPolicy> hitPolicySupplier,
                                        final Supplier<BuiltinAggregator> builtinAggregatorSupplier,
                                        final CellEditorControlsView.Presenter cellEditorControls,
                                        final HitPolicyEditorView.Presenter editor,
                                        final DecisionTableGrid gridWidget) {
        super(Collections.singletonList(new RowNumberColumnHeaderMetaData(hitPolicySupplier,
                                                                          builtinAggregatorSupplier,
                                                                          cellEditorControls,
                                                                          editor,
                                                                          gridWidget)));
    }
}