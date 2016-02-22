/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.projecteditor.client.forms.dependencies;

import org.guvnor.common.services.project.model.Dependency;
import org.kie.workbench.common.screens.projecteditor.client.resources.ProjectEditorResources;
import org.kie.workbench.common.services.shared.dependencies.EnhancedDependency;

public class ArtifactIdColumn
        extends com.google.gwt.user.cellview.client.Column<EnhancedDependency, String> {

    public ArtifactIdColumn( DependencyGridViewImpl.RedrawCommand redrawCommand ) {
        super( new WaterMarkEditTextCell( ProjectEditorResources.CONSTANTS.EnterAnArtifactID() ) );

        setFieldUpdater( new ArtifactIdDependencyFieldUpdater( (WaterMarkEditTextCell) getCell(),
                                                               redrawCommand ) );
    }

    @Override
    public String getValue( EnhancedDependency dependency ) {
        if ( dependency.getDependency().getArtifactId() != null ) {
            return dependency.getDependency().getArtifactId();
        } else {
            return "";
        }
    }

}
