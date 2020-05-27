package de.fhg.iais.roberta.visitor.collect;

import java.util.Map;

import de.fhg.iais.roberta.components.UsedConfigurationComponent;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.generic.PinWriteValueAction;
import de.fhg.iais.roberta.syntax.action.light.LightAction;
import de.fhg.iais.roberta.syntax.action.light.LightStatusAction;
import de.fhg.iais.roberta.syntax.action.mbed.LedOnAction;
import de.fhg.iais.roberta.syntax.action.mbed.PinSetPullAction;
import de.fhg.iais.roberta.syntax.sensor.generic.UltrasonicSensor;
import de.fhg.iais.roberta.util.dbc.Assert;

public class MbedReplaceVisitor implements IMbedCollectorVisitor {

    private final ReplacementMap replacementMap;

    private Phrase<Void> lastReplacedPhrase = null;

    public MbedReplaceVisitor(ReplacementMap replacementMap) {
        this.replacementMap = replacementMap;
    }

    public boolean wasPhraseReplaced() {
        return this.lastReplacedPhrase != null;
    }

    public Phrase<Void> popLastReplacedPhrase() {
        Assert.notNull(this.lastReplacedPhrase, "No phrase was replaced beforehand!");
        Phrase<Void> phrase = this.lastReplacedPhrase;
        this.lastReplacedPhrase = null;
        return phrase;
    }

    @Override
    public Void visitPinWriteValueAction(PinWriteValueAction<Void> pinWriteValueAction) {
        PinWriteValueAction<Void> phrase = pinWriteValueAction;
        Map<String, Object>
            replacements =
            this.replacementMap.getReplacement(new UsedConfigurationComponent(pinWriteValueAction.getPort(),
                                                                              pinWriteValueAction.getKind(),
                                                                              pinWriteValueAction.getMode()));
        if (replacements != null) {
            PinWriteValueAction.Builder<Void> builder = new PinWriteValueAction.Builder<>();
            builder.setOriginal(pinWriteValueAction);
            replacements.forEach(builder::set);
            phrase = builder.build();
            this.lastReplacedPhrase = phrase;
        }
        return IMbedCollectorVisitor.super.visitPinWriteValueAction(phrase);
    }

    @Override
    public Void visitPinSetPullAction(PinSetPullAction<Void> pinSetPullAction) {
        PinSetPullAction<Void> phrase = pinSetPullAction;
        Map<String, Object>
            replacements =
            this.replacementMap.getReplacement(new UsedConfigurationComponent(pinSetPullAction.getPort(),
                                                                              pinSetPullAction.getKind(),
                                                                              pinSetPullAction.getMode()));
        if (replacements != null) {
            PinSetPullAction.Builder<Void> builder = new PinSetPullAction.Builder<>();
            builder.setOriginal(pinSetPullAction);
            replacements.forEach(builder::set);
            phrase = builder.build();
            this.lastReplacedPhrase = phrase;
        }
        return IMbedCollectorVisitor.super.visitPinSetPullAction(phrase);
    }

    @Override
    public Void visitLedOnAction(LedOnAction<Void> ledOnAction) {
        LedOnAction<Void> phrase = ledOnAction;
        Map<String, Object>
            replacements =
            this.replacementMap.getReplacement(new UsedConfigurationComponent(ledOnAction.getPort(),
                                                                              ledOnAction.getKind(),
                                                                              ""));
        if (replacements != null) {
            LedOnAction.Builder<Void> builder = new LedOnAction.Builder<>();
            builder.setOriginal(ledOnAction);
            replacements.forEach(builder::set);
            phrase = builder.build();
            this.lastReplacedPhrase = phrase;
        }
        return IMbedCollectorVisitor.super.visitLedOnAction(phrase);
    }

    @Override
    public Void visitLightAction(LightAction<Void> lightAction) {
        LightAction<Void> phrase = lightAction;
        Map<String, Object>
            replacements =
            this.replacementMap.getReplacement(new UsedConfigurationComponent(lightAction.getPort(),
                                                                              lightAction.getKind(),
                                                                              lightAction.getMode().toString()));
        if (replacements != null) {
            LightAction.Builder<Void> builder = new LightAction.Builder<>();
            builder.setOriginal(lightAction);
            replacements.forEach(builder::set);
            phrase = builder.build();
            this.lastReplacedPhrase = phrase;
        }
        return IMbedCollectorVisitor.super.visitLightAction(phrase);
    }

    @Override
    public Void visitLightStatusAction(LightStatusAction<Void> lightStatusAction) {
        LightStatusAction<Void> phrase = lightStatusAction;
        Map<String, Object>
            replacements =
            this.replacementMap.getReplacement(new UsedConfigurationComponent(lightStatusAction.getPort(),
                                                                              lightStatusAction.getKind(),
                                                                              lightStatusAction.getStatus().toString()));
        if (replacements != null) {
            LightStatusAction.Builder<Void> builder = new LightStatusAction.Builder<>();
            builder.setOriginal(lightStatusAction);
            replacements.forEach(builder::set);
            phrase = builder.build();
            this.lastReplacedPhrase = phrase;
        }
        return IMbedCollectorVisitor.super.visitLightStatusAction(phrase);
    }

    @Override
    public Void visitUltrasonicSensor(UltrasonicSensor<Void> ultrasonicSensor) {
        UltrasonicSensor<Void> phrase = ultrasonicSensor;
        Map<String, Object>
            replacements =
            this.replacementMap.getReplacement(new UsedConfigurationComponent(ultrasonicSensor.getPort(),
                                                                              ultrasonicSensor.getKind(),
                                                                              ultrasonicSensor.getMode()));
        if (replacements != null) {
            UltrasonicSensor.Builder<Void> builder = new UltrasonicSensor.Builder<>();
            builder.setOriginal(ultrasonicSensor);
            replacements.forEach(builder::set);
            phrase = builder.build();
            this.lastReplacedPhrase = phrase;
        }
        return IMbedCollectorVisitor.super.visitUltrasonicSensor(phrase);
    }
}
