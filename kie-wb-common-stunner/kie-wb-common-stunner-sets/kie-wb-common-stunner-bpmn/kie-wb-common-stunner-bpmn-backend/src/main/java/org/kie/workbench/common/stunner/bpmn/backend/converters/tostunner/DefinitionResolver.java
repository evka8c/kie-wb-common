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

package org.kie.workbench.common.stunner.bpmn.backend.converters.tostunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import bpsim.BPSimDataType;
import bpsim.BpsimPackage;
import bpsim.ElementParameters;
import bpsim.Scenario;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.Relationship;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.Signal;
import org.eclipse.bpmn2.di.BPMNEdge;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BPMNShape;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * An object that resolves child definitions.
 * <p>
 * Because the Stunner model aggregates different aspects of a node,
 * while the Eclipse model keeps them in separate sections, we use this
 * object to access such aspects, namely
 *
 * <ul>
 * <li>shapes</li>
 * <li>simulation parameters</li>
 * </ul>.
 *
 * <em>signal</em> concern is due to a bug in current Eclipse BPMN2 implementation,
 * which is outdated w.r.t. upstream.
 */
public class DefinitionResolver {

    private final Map<String, Signal> signals;
    private final Map<String, ElementParameters> simulationParameters;
    private final Definitions definitions;
    private final Process process;
    private final BPMNPlane plane;

    public DefinitionResolver(Definitions definitions) {
        this.definitions = definitions;
        this.signals = initSignals(definitions);
        this.simulationParameters = initSimulationParameters(definitions);
        this.process = findProcess();
        this.plane = findPlane();
    }

    public BPMNPlane getPlane() {
        return plane;
    }

    public Definitions getDefinitions() {
        return definitions;
    }

    public Process getProcess() {
        return process;
    }

    /**
     * A helper method to return a Signal instance for a given Signal ID.
     * <p>
     * The reason we only have Signal resolution and we do not have
     * Message, Error, Timer resolution, is that Message, Error, Timer
     * instances are usually attached to the events that refer them,
     * so that we can do:
     *
     * <code><pre>
     *     Message mySignal = myEvent.getMessageRef()
     * </pre></code>
     * <p>
     * this isn't so for Signal, due to a bug in the method signature/impl.
     * <code>getSignalRef()</code> actually returns the <em>reference</em> (the String ID)
     * to the Signal instead of resolving it to the Signal instnace.
     * <p>
     * This method can be safely dropped as soon as we find a way to upgrade
     * the underlying BPMN2 Eclipse parser without breaking the rest of the code base.
     */
    public Optional<Signal> resolveSignal(String id) {
        return Optional.ofNullable(signals.get(id));
    }

    /**
     * A utility to return Signal#getName for a given Signal ID.
     */
    public String resolveSignalName(String id) {
        return resolveSignal(id).map(Signal::getName).orElse("");
    }

    /**
     * Returns the simulation parameters attached to a given ID
     */
    public Optional<ElementParameters> resolveSimulationParameters(String id) {
        return Optional.ofNullable(simulationParameters.get(id));
    }

    private Map<String, Signal> initSignals(Definitions definitions) {
        Map<String, Signal> signals = new HashMap<>();
        for (RootElement el : definitions.getRootElements()) {
            if (el instanceof Signal) {
                signals.put(el.getId(), (Signal) el);
            }
        }
        return signals;
    }

    private Map<String, ElementParameters> initSimulationParameters(Definitions definitions) {
        Map<String, ElementParameters> simulationParameters = new HashMap<>();
        List<Relationship> relationships = definitions.getRelationships();
        if (relationships.isEmpty()) {
            return Collections.emptyMap();
        }
        FeatureMap value =
                relationships.get(0)
                        .getExtensionValues().get(0)
                        .getValue();

        Object simData =
                value.get(BpsimPackage.Literals.DOCUMENT_ROOT__BP_SIM_DATA, true);
        List<BPSimDataType> bpsimExtensions = (List<BPSimDataType>) simData;

        Scenario scenario = bpsimExtensions.get(0).getScenario().get(0);

        for (ElementParameters parameters : scenario.getElementParameters()) {
            simulationParameters.put(parameters.getElementRef(), parameters);
        }

        return simulationParameters;
    }

    private Process findProcess() {
        return (Process) definitions.getRootElements().stream()
                .filter(el -> el instanceof Process)
                .findFirst().get();
    }

    private BPMNPlane findPlane() {
        return definitions.getDiagrams().get(0).getPlane();
    }

    public BPMNShape getShape(String elementId) {
        return plane.getPlaneElement().stream()
                .filter(dia -> dia instanceof BPMNShape)
                .map(shape -> (BPMNShape) shape)
                .filter(shape -> shape.getBpmnElement().getId().equals(elementId))
                .findFirst().orElse(null);
    }

    public BPMNEdge getEdge(String elementId) {
        return plane.getPlaneElement().stream()
                .filter(dia -> dia instanceof BPMNEdge)
                .map(edge -> (BPMNEdge) edge)
                .filter(edge -> edge.getBpmnElement().getId().equals(elementId))
                .findFirst().get();
    }
}
