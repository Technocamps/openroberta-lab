package de.fhg.iais.roberta.visitor.collect;

import de.fhg.iais.roberta.bean.NewUsedHardwareBean;
import de.fhg.iais.roberta.components.UsedConfigurationComponent;
import de.fhg.iais.roberta.syntax.action.generic.PinWriteValueAction;
import de.fhg.iais.roberta.syntax.action.light.LightAction;
import de.fhg.iais.roberta.syntax.action.light.LightStatusAction;
import de.fhg.iais.roberta.syntax.sensor.generic.CompassSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.LightSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.SoundSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TemperatureSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.UltrasonicSensor;

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

    @Override
    public Void visitSoundSensor(SoundSensor<Void> soundSensor) {
        this.builder.addUsedConfigurationComponent(new UsedConfigurationComponent(soundSensor.getPort(), soundSensor.getKind(), soundSensor.getMode()));
        return ICollectorVisitor.super.visitSoundSensor(soundSensor);
    }

    @Override
    public Void visitLightSensor(LightSensor<Void> lightSensor) {
        this.builder.addUsedConfigurationComponent(new UsedConfigurationComponent(lightSensor.getPort(), lightSensor.getKind(), lightSensor.getMode()));
        return ICollectorVisitor.super.visitLightSensor(lightSensor);
    }

    @Override
    public Void visitCompassSensor(CompassSensor<Void> compassSensor) {
        this.builder.addUsedConfigurationComponent(new UsedConfigurationComponent(compassSensor.getPort(), compassSensor.getKind(), compassSensor.getMode()));
        return ICollectorVisitor.super.visitCompassSensor(compassSensor);
    }

    @Override
    public Void visitTemperatureSensor(TemperatureSensor<Void> temperatureSensor) {
        this.builder.addUsedConfigurationComponent(new UsedConfigurationComponent(temperatureSensor.getPort(), temperatureSensor.getKind(), temperatureSensor.getMode()));
        return ICollectorVisitor.super.visitTemperatureSensor(temperatureSensor);
    }

    @Override
    public Void visitUltrasonicSensor(UltrasonicSensor<Void> ultrasonicSensor) {
        this.builder.addUsedConfigurationComponent(new UsedConfigurationComponent(ultrasonicSensor.getPort(), ultrasonicSensor.getKind(), ultrasonicSensor.getMode()));
        return ICollectorVisitor.super.visitUltrasonicSensor(ultrasonicSensor);
    }

    @Override
    public Void visitLightAction(LightAction<Void> lightAction) {
        this.builder.addUsedConfigurationComponent(new UsedConfigurationComponent(lightAction.getPort(), lightAction.getKind(), lightAction.getMode().toString()));
        return ICollectorVisitor.super.visitLightAction(lightAction);
    }

    @Override
    public Void visitLightStatusAction(LightStatusAction<Void> lightStatusAction) {
        this.builder.addUsedConfigurationComponent(new UsedConfigurationComponent(lightStatusAction.getPort(), lightStatusAction.getKind(), lightStatusAction.getStatus().toString()));
        return ICollectorVisitor.super.visitLightStatusAction(lightStatusAction);
    }
}
