package net.n2oapp.framework.config.metadata.pack;

import net.n2oapp.framework.api.pack.MetadataPack;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.io.action.*;
import net.n2oapp.framework.config.io.action.v2.*;
import net.n2oapp.framework.config.io.action.v2.ifelse.ElseBranchActionElementIOV2;
import net.n2oapp.framework.config.io.action.v2.ifelse.ElseIfBranchActionElementIOV2;
import net.n2oapp.framework.config.io.action.v2.ifelse.IfBranchActionElementIOV2;
import net.n2oapp.framework.config.metadata.compile.action.*;
import net.n2oapp.framework.config.metadata.compile.action.condition.ConditionActionBinder;
import net.n2oapp.framework.config.metadata.compile.action.condition.ElseBranchActionCompiler;
import net.n2oapp.framework.config.metadata.compile.action.condition.ElseIfBranchActionCompiler;
import net.n2oapp.framework.config.metadata.compile.action.condition.IfBranchActionCompiler;
import net.n2oapp.framework.config.metadata.compile.cell.ToolbarCellBinder;
import net.n2oapp.framework.config.metadata.compile.control.CustomFieldBinder;

public class N2oActionsPack implements MetadataPack<N2oApplicationBuilder> {
    @Override
    public void build(N2oApplicationBuilder b) {
        b.ios(new InvokeActionElementIOV1(),
                new ShowModalElementIOV1(),
                new OpenPageElementIOV1(),
                new OpenDrawerElementIOV1(),
                new AnchorElementIOV1(),
                new CloseActionElementIOV1(),
                new SetValueElementIOV1(),
                new PerformElementIOv1(),
                new CopyActionElementIOV1(),
                new ClearActionElementIOV1(),
                new PrintActionElementIOV1(),
                new RefreshActionElementIOV1(),
                new InvokeActionElementIOV2(),
                new ShowModalElementIOV2(),
                new OpenPageElementIOV2(),
                new OpenDrawerElementIOV2(),
                new AnchorElementIOV2(),
                new CloseActionElementIOV2(),
                new SetValueElementIOV2(),
                new PerformElementIOV2(),
                new CopyActionElementIOV2(),
                new ClearActionElementIOV2(),
                new PrintActionElementIOV2(),
                new RefreshActionElementIOV2(),
                new AlertActionElementIOV2(),
                new SubmitActionElementIOV2(),
                new CustomActionElementIOV2(),
                new SwitchActionElementIOV2(),
                new IfBranchActionElementIOV2(),
                new ElseIfBranchActionElementIOV2(),
                new ElseBranchActionElementIOV2());
        b.compilers(new PerformCompiler(),
                new ShowModalCompiler(),
                new OpenDrawerCompiler(),
                new InvokeActionCompiler(),
                new CloseActionCompiler(),
                new RefreshActionCompiler(),
                new OpenPageCompiler(),
                new AnchorCompiler(),
                new ClearActionCompiler(),
                new CopyActionCompiler(),
                new SetValueActionCompiler(),
                new PrintActionCompiler(),
                new AlertActionCompiler(),
                new SubmitActionCompiler(),
                new CustomActionCompiler(),
                new SwitchActionCompiler(),
                new IfBranchActionCompiler(),
                new ElseIfBranchActionCompiler(),
                new ElseBranchActionCompiler());
        b.binders(new InvokeActionBinder(),
                new ReduxActionBinder(),
                new LinkActionBinder(),
                new ShowModalBinder(),
                new OpenDrawerBinder(),
                new MultiActionBinder(),
                new SwitchActionBinder(),
                new ConditionActionBinder(),
                new PerformButtonBinder(),
                new CustomFieldBinder(),
                new ToolbarCellBinder(),
                new SubMenuBinder(),
                new PrintActionBinder(),
                new ButtonFieldBinder(),
                new ActionComponentBinder());
    }
}
