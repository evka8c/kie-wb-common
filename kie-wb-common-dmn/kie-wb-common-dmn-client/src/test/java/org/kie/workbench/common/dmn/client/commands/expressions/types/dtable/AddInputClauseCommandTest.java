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

package org.kie.workbench.common.dmn.client.commands.expressions.types.dtable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionRule;
import org.kie.workbench.common.dmn.api.definition.v1_1.DecisionTable;
import org.kie.workbench.common.dmn.api.definition.v1_1.InputClause;
import org.kie.workbench.common.dmn.api.definition.v1_1.UnaryTests;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.DecisionTableUIModelMapperHelper;
import org.kie.workbench.common.dmn.client.editors.expressions.types.dtable.InputClauseColumn;
import org.kie.workbench.common.dmn.client.widgets.grid.controls.list.ListSelectorView;
import org.kie.workbench.common.dmn.client.widgets.grid.model.DMNGridData;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.wires.core.grids.client.model.GridData;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridRow;
import org.uberfire.ext.wires.core.grids.client.widget.grid.columns.RowNumberColumn;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AddInputClauseCommandTest {

    @Mock
    private RowNumberColumn uiRowNumberColumn;

    @Mock
    private InputClauseColumn uiInputClauseColumn;

    @Mock
    private ListSelectorView.Presenter listSelector;

    @Mock
    private AbstractCanvasHandler canvasHandler;

    @Mock
    private GraphCommandExecutionContext graphCommandExecutionContext;

    private DecisionTable dtable;

    private InputClause inputClause;

    private GridData uiModel;

    private DecisionTableUIModelMapper uiModelMapper;

    private AddInputClauseCommand command;

    @Mock
    private org.uberfire.mvp.Command canvasOperation;

    @Before
    public void setUp() throws Exception {
        this.dtable = new DecisionTable();
        this.uiModel = new DMNGridData();
        this.uiModel.appendColumn(uiRowNumberColumn);
        this.inputClause = new InputClause();
        this.uiModelMapper = new DecisionTableUIModelMapper(() -> uiModel,
                                                            () -> Optional.of(dtable),
                                                            listSelector);

        doReturn(0).when(uiRowNumberColumn).getIndex();
        doReturn(1).when(uiInputClauseColumn).getIndex();
    }

    private void makeCommand(final int index) {
        this.command = spy(new AddInputClauseCommand(dtable,
                                                     inputClause,
                                                     uiModel,
                                                     uiInputClauseColumn,
                                                     index,
                                                     uiModelMapper,
                                                     canvasOperation));
    }

    @Test
    public void testGraphCommandAllow() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.allow(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandExecute() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        dtable.getRule().add(new DecisionRule());
        dtable.getRule().add(new DecisionRule());
        assertEquals(0, dtable.getInput().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        // one new input column
        assertEquals(1, dtable.getInput().size());

        // first rule
        assertEquals(1, dtable.getRule().get(0).getInputEntry().size());
        assertEquals(AddInputClauseCommand.INPUT_CLAUSE_DEFAULT_VALUE, dtable.getRule().get(0).getInputEntry().get(0).getText());

        // second rule
        assertEquals(1, dtable.getRule().get(1).getInputEntry().size());
        assertEquals(AddInputClauseCommand.INPUT_CLAUSE_DEFAULT_VALUE, dtable.getRule().get(1).getInputEntry().get(0).getText());
    }

    @Test
    public void testGraphCommandExecuteExistingNotAffected() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final String ruleOneOldInput = "old rule 1";
        final String ruleTwoOldInput = "old rule 2";

        dtable.getInput().add(new InputClause());
        addRuleWithInputClauseValues(ruleOneOldInput);
        addRuleWithInputClauseValues(ruleTwoOldInput);

        assertEquals(1, dtable.getInput().size());

        //Graph command will insert new InputClause at index 0 of the InputEntries
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertEquals(2, dtable.getInput().size());

        // first rule
        assertEquals(2, dtable.getRule().get(0).getInputEntry().size());
        assertEquals(ruleOneOldInput, dtable.getRule().get(0).getInputEntry().get(1).getText());
        assertEquals(AddInputClauseCommand.INPUT_CLAUSE_DEFAULT_VALUE, dtable.getRule().get(0).getInputEntry().get(0).getText());

        // second rule
        assertEquals(2, dtable.getRule().get(1).getInputEntry().size());
        assertEquals(ruleTwoOldInput, dtable.getRule().get(1).getInputEntry().get(1).getText());
        assertEquals(AddInputClauseCommand.INPUT_CLAUSE_DEFAULT_VALUE, dtable.getRule().get(1).getInputEntry().get(0).getText());
    }

    @Test
    public void testGraphCommandExecuteInsertMiddle() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT + 1);

        final String ruleInputOne = "rule in 1";
        final String ruleInputTwo = "rule in 2";

        dtable.getInput().add(new InputClause());
        dtable.getInput().add(new InputClause());
        addRuleWithInputClauseValues(ruleInputOne, ruleInputTwo);

        assertEquals(2, dtable.getInput().size());

        //Graph command will insert new InputClause at index 1 of the InputEntries
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertEquals(3, dtable.getInput().size());

        final List<UnaryTests> ruleInputs = dtable.getRule().get(0).getInputEntry();

        // first rule
        assertEquals(3, dtable.getRule().get(0).getInputEntry().size());
        assertEquals(ruleInputOne, ruleInputs.get(0).getText());
        assertEquals(AddInputClauseCommand.INPUT_CLAUSE_DEFAULT_VALUE, ruleInputs.get(1).getText());
        assertEquals(ruleInputTwo, ruleInputs.get(2).getText());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testGraphCommandUndoNoInputClauseColumns() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        dtable.getRule().add(new DecisionRule());

        assertEquals(0, dtable.getInput().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));
    }

    @Test
    public void testGraphCommandUndoJustLastInputClauseColumn() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final String ruleOneOldInput = "old rule 1";
        final String ruleTwoOldInput = "old rule 2";

        dtable.getInput().add(new InputClause());
        addRuleWithInputClauseValues(ruleOneOldInput);
        addRuleWithInputClauseValues(ruleTwoOldInput);

        assertEquals(1, dtable.getInput().size());

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.execute(graphCommandExecutionContext));

        assertEquals(GraphCommandResultBuilder.SUCCESS,
                     graphCommand.undo(graphCommandExecutionContext));

        assertEquals(1, dtable.getInput().size());

        // first rule
        assertEquals(1, dtable.getRule().get(0).getInputEntry().size());
        assertEquals(ruleOneOldInput, dtable.getRule().get(0).getInputEntry().get(0).getText());

        // second rule
        assertEquals(1, dtable.getRule().get(1).getInputEntry().size());
        assertEquals(ruleTwoOldInput, dtable.getRule().get(1).getInputEntry().get(0).getText());
    }

    @Test
    public void testCanvasCommandAllow() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasCommand = command.newCanvasCommand(canvasHandler);

        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasCommand.allow(canvasHandler));
    }

    @Test
    public void testCanvasCommandAddRuleAndThenUndo() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final String ruleOneInputValue = "one";
        final String ruleTwoInputValue = "two";

        dtable.getRule().add(new DecisionRule());
        dtable.getRule().add(new DecisionRule());
        uiModel.appendRow(new BaseGridRow());
        uiModel.appendRow(new BaseGridRow());

        //Graph command populates InputEntries so overwrite with test values
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = command.newGraphCommand(canvasHandler);
        graphCommand.execute(graphCommandExecutionContext);
        dtable.getRule().get(0).getInputEntry().get(0).setText(ruleOneInputValue);
        dtable.getRule().get(1).getInputEntry().get(0).setText(ruleTwoInputValue);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddInputClauseCommand = command.newCanvasCommand(canvasHandler);
        canvasAddInputClauseCommand.execute(canvasHandler);

        // first rule
        assertEquals(ruleOneInputValue, uiModel.getRow(0).getCells().get(1).getValue().getValue());

        // second rule
        assertEquals(ruleTwoInputValue, uiModel.getRow(1).getCells().get(1).getValue().getValue());

        assertEquals(2, uiModel.getColumnCount());
        assertEquals(CanvasCommandResultBuilder.SUCCESS,
                     canvasAddInputClauseCommand.undo(canvasHandler));
        assertEquals(1, uiModel.getColumnCount());

        // one time in execute(), one time in undo()
        verify(canvasOperation, times(2)).execute();
        verify(command, times(2)).updateParentInformation();
    }

    @Test
    public void testCanvasCommandUndoWhenNothingBefore() throws Exception {
        makeCommand(DecisionTableUIModelMapperHelper.ROW_INDEX_COLUMN_COUNT);

        final Command<AbstractCanvasHandler, CanvasViolation> canvasAddInputClauseCommand = command.newCanvasCommand(canvasHandler);

        canvasAddInputClauseCommand.undo(canvasHandler);
        // just row number column
        assertEquals(1, uiModel.getColumnCount());

        verify(canvasOperation).execute();
        verify(command).updateParentInformation();
    }

    private void addRuleWithInputClauseValues(String... inputClauseValues) {
        dtable.getRule().add(new DecisionRule() {{
            Stream.of(inputClauseValues).forEach(iClause -> {
                getInputEntry().add(new UnaryTests() {{
                    setText(iClause);
                }});
            });
        }});
    }
}
