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
package org.kie.workbench.common.dmn.api.definition.v1_1;

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.dmn.api.definition.HasExpression;
import org.kie.workbench.common.dmn.api.property.dmn.Description;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

@Portable
public class FunctionDefinition extends Expression implements HasExpression {

    public static final String DROOLS_PREFIX = "drools";

    public static final String KIND_LOCAL_PART = "kind";

    public static final QName KIND_QNAME = new QName(Namespace.KIE.getUri(),
                                                     KIND_LOCAL_PART);

    private Expression expression;

    private List<InformationItem> formalParameter;

    public FunctionDefinition() {
        this(new Id(),
             new Description(),
             new QName(),
             null);
    }

    public FunctionDefinition(final @MapsTo("id") Id id,
                              final @MapsTo("description") Description description,
                              final @MapsTo("typeRef") QName typeRef,
                              final @MapsTo("expression") Expression expression) {
        super(id,
              description,
              typeRef);
        this.expression = expression;
    }

    // -----------------------
    // DMN properties
    // -----------------------

    @Override
    public Expression getExpression() {
        return expression;
    }

    @Override
    public void setExpression(final Expression expression) {
        this.expression = expression;
    }

    public List<InformationItem> getFormalParameter() {
        if (formalParameter == null) {
            formalParameter = new ArrayList<>();
        }
        return this.formalParameter;
    }

    @Portable
    public enum Kind {
        FEEL("F"),
        JAVA("J"),
        PMML("P");

        private final String code;

        Kind(final String code) {
            this.code = code;
        }

        public String code() {
            return code;
        }

        public static FunctionDefinition.Kind determineFromString(final String code) {
            return code == null ? null : (FEEL.code.equals(code) ? FEEL : (JAVA.code.equals(code) ? JAVA : (PMML.code.equals(code) ? PMML : null)));
        }
    }
}
