package de.fhg.iais.roberta.visitor.collect;
import de.fhg.iais.roberta.bean.NewUsedHardwareBean;
import de.fhg.iais.roberta.components.UsedConfigurationComponent;
import de.fhg.iais.roberta.syntax.action.generic.PinWriteValueAction;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;

public class AbstractNewUsedHardwareCollectorVisitor implements ICollectorVisitor {

    protected NewUsedHardwareBean.Builder builder;

    public AbstractNewUsedHardwareCollectorVisitor(NewUsedHardwareBean.Builder builder) {
        this.builder = builder;
    }

    @Override
    public Void visitPinWriteValueAction(PinWriteValueAction<Void> pinWriteValueAction) {
        this.builder.addUsedConfigurationComponent(new UsedConfigurationComponent(pinWriteValueAction.getPort(), pinWriteValueAction.getKind(), pinWriteValueAction.getMode()));
        return ICollectorVisitor.super.visitPinWriteValueAction(pinWriteValueAction);
    }

    @Override
    public Void visitKeysSensor(KeysSensor<Void> keysSensor) {
        this.builder.addUsedConfigurationComponent(new UsedConfigurationComponent(keysSensor.getPort(), keysSensor.getKind(), keysSensor.getMode()));
        return ICollectorVisitor.super.visitKeysSensor(keysSensor);
    }
}
