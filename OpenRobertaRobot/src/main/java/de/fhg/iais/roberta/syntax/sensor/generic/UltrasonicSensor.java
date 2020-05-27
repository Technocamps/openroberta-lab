package de.fhg.iais.roberta.syntax.sensor.generic;

import de.fhg.iais.roberta.blockly.generated.Block;
import de.fhg.iais.roberta.syntax.BlockTypeContainer;
import de.fhg.iais.roberta.syntax.BlocklyBlockProperties;
import de.fhg.iais.roberta.syntax.BlocklyComment;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.IActionBuilder;
import de.fhg.iais.roberta.syntax.sensor.ExternalSensor;
import de.fhg.iais.roberta.syntax.sensor.SensorMetaDataBean;
import de.fhg.iais.roberta.transformer.AbstractJaxb2Ast;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.visitor.IVisitor;
import de.fhg.iais.roberta.visitor.hardware.sensor.ISensorVisitor;

/**
 * This class represents the <b>robSensors_ultrasonic_getMode</b>, <b>robSensors_ultrasonic_getSample</b> and <b>robSensors_ultrasonic_setMode</b> blocks from
 * Blockly into the AST (abstract syntax tree). Object from this class will generate code for setting the mode of the sensor or getting a sample from the
 * sensor.<br/>
 * <br>
 * The client must provide the {@link SensorPort} and {@link UltrasonicSensorMode}. See enum {@link UltrasonicSensorMode} for all possible modes of the sensor.
 * <br>
 * <br>
 * To create an instance from this class use the method {@link #make(UltrasonicSensorMode, SensorPort, BlocklyBlockProperties, BlocklyComment)}.<br>
 */
public class UltrasonicSensor<V> extends ExternalSensor<V> {

    public static class Builder<V> implements IActionBuilder<UltrasonicSensor<V>> {
        private String port = null;
        private String mode = null;
        private String slot = null;
        private boolean isPortInMutation = false;
        private BlocklyBlockProperties properties = null;
        private BlocklyComment comment = null;

        public Builder<V> setOriginal(UltrasonicSensor<V> original) {
            this.port = original.getPort();
            this.mode = original.getMode();
            this.slot = original.getSlot();
            this.isPortInMutation = true; // TODO
            this.properties = original.getProperty();
            this.comment = original.getComment();
            return this;
        }

        public Builder<V> setPort(String port) {
            this.port = port;
            return this;
        }

        public UltrasonicSensor<V> build() {
            Assert.notNull(this.port);
            Assert.notNull(this.mode);
            Assert.notNull(this.slot);
            Assert.notNull(this.properties);
            // comment can be null

            return new UltrasonicSensor<V>(new SensorMetaDataBean(this.port, this.mode, this.slot, this.isPortInMutation), this.properties, this.comment);
        }
    }

    private UltrasonicSensor(SensorMetaDataBean sensorMetaDataBean, BlocklyBlockProperties properties, BlocklyComment comment) {
        super(sensorMetaDataBean, BlockTypeContainer.getByName("ULTRASONIC_SENSING"), properties, comment);
        setReadOnly();
    }

    /**
     * Create object of the class {@link UltrasonicSensor}.
     *
     * @param mode in which the sensor is operating; must be <b>not</b> null; see enum {@link UltrasonicSensorMode} for all possible modes that the sensor have,
     * @param port on where the sensor is connected; must be <b>not</b> null; see enum {@link SensorPort} for all possible sensor ports,
     * @param properties of the block (see {@link BlocklyBlockProperties}),
     * @param comment added from the user,
     * @return read only object of {@link UltrasonicSensor}
     */
    public static <V> UltrasonicSensor<V> make(SensorMetaDataBean sensorMetaDataBean, BlocklyBlockProperties properties, BlocklyComment comment) {
        return new UltrasonicSensor<V>(sensorMetaDataBean, properties, comment);
    }

    @Override
    protected V acceptImpl(IVisitor<V> visitor) {
        return ((ISensorVisitor<V>) visitor).visitUltrasonicSensor(this);
    }

    /**
     * Transformation from JAXB object to corresponding AST object.
     *
     * @param block for transformation
     * @param helper class for making the transformation
     * @return corresponding AST object
     */
    public static <V> Phrase<V> jaxbToAst(Block block, AbstractJaxb2Ast<V> helper) {
        SensorMetaDataBean sensorData = extractPortAndModeAndSlot(block, helper);
        return UltrasonicSensor.make(sensorData, helper.extractBlockProperties(block), helper.extractComment(block));
    }

}
