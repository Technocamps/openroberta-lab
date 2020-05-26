package de.fhg.iais.roberta.visitor.collect;

import de.fhg.iais.roberta.bean.NewUsedHardwareBean;
import de.fhg.iais.roberta.components.UsedConfigurationComponent;
import de.fhg.iais.roberta.syntax.action.mbed.LedOnAction;
import de.fhg.iais.roberta.syntax.action.mbed.PinSetPullAction;

public class MbedNewUsedHardwareCollectorVisitor extends AbstractNewUsedHardwareCollectorVisitor implements IMbedCollectorVisitor {
    public MbedNewUsedHardwareCollectorVisitor(NewUsedHardwareBean.Builder builder) {
        super(builder);
    }

    @Override
    public Void visitPinSetPullAction(PinSetPullAction<Void> pinSetPullAction) {
        this.builder.addUsedConfigurationComponent(new UsedConfigurationComponent(pinSetPullAction.getPort(), pinSetPullAction.getKind(), pinSetPullAction.getMode()));
        return IMbedCollectorVisitor.super.visitPinSetPullAction(pinSetPullAction);
    }

    @Override
    public Void visitLedOnAction(LedOnAction<Void> ledOnAction) {
        this.builder.addUsedConfigurationComponent(new UsedConfigurationComponent(ledOnAction.getPort(), ledOnAction.getKind(), ""));
        return IMbedCollectorVisitor.super.visitLedOnAction(ledOnAction);
    }
}
