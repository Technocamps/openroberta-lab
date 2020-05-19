package de.fhg.iais.roberta.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import de.fhg.iais.roberta.bean.NewUsedHardwareBean;
import de.fhg.iais.roberta.components.Category;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.ConfigurationComponent;
import de.fhg.iais.roberta.components.Project;
import de.fhg.iais.roberta.components.UsedConfigurationComponent;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.generic.PinWriteValueAction;
import de.fhg.iais.roberta.util.dbc.DbcException;

public class CalliopeConfigurationGenerator implements IWorker {

    private static final Map<String, String> pinMapping = new HashMap<>();
    static {
        pinMapping.put("0", "P0");
        pinMapping.put("1", "P1");
        pinMapping.put("2", "P2");
        pinMapping.put("3", "P3");
        pinMapping.put("4", "A0");
        pinMapping.put("5", "A1");
        pinMapping.put("C04", "C04");
        pinMapping.put("C05", "C05");
        pinMapping.put("C06", "C06");
        pinMapping.put("C07", "C07");
        pinMapping.put("C08", "C08");
        pinMapping.put("C09", "C09");
        pinMapping.put("C10", "C10");
        pinMapping.put("C11", "C11");
        pinMapping.put("C12", "C12");
        pinMapping.put("C16", "C16");
        pinMapping.put("C17", "C17");
        pinMapping.put("C18", "C18");
        pinMapping.put("C19", "C19");
    }

    static String getNewUserDefinedName(String port, String mode) {
        if (mode.equals("DIGITAL")) {
            return pinMapping.get(port) + "_D";
        } else if (mode.equals("ANALOG")) {
            return pinMapping.get(port) + "_A";
        } else {
            throw new DbcException("Mode not supported!");
        }
    }

    @Override
    public void execute(Project project) {
        if ( project.isDefaultConfiguration() ) { // TODO is this still needed?
            NewUsedHardwareBean usedHardwareBean = project.getWorkerResult(NewUsedHardwareBean.class);

            Map<String, ConfigurationComponent> components = new HashMap<>();
            for ( UsedConfigurationComponent usedConfComp : usedHardwareBean.getUsedConfigurationComponents() ) {
                ConfigurationComponent defaultComponent = project.getConfigurationAst().getConfigurationComponents().get(usedConfComp.getPort());
                if (defaultComponent == null) { // for pins
                    defaultComponent = project.getConfigurationAst().getConfigurationComponents().values().stream().filter(cc -> {
                        String input = cc.getOptProperty("INPUT");
                        if (input != null) {
                            return input.equals(usedConfComp.getPort()) && cc.getComponentType().contains(usedConfComp.getMode());
                        }
                        return false;
                    }).findFirst().get();
                }
                if ( defaultComponent != null ) { // TODO every component needs a default value, location, etc -> for now loaded from default configuration
                    components
                        .put(
                            usedConfComp.getPort(),
                            new ConfigurationComponent(
                                defaultComponent.getComponentType(),
                                usedConfComp.getType().getCategory() == Category.ACTOR,
                                usedConfComp.getPort(),
                                getNewUserDefinedName(usedConfComp.getPort(), usedConfComp.getMode()),
                                defaultComponent.getComponentProperties(),
                                defaultComponent.getProperty(),
                                defaultComponent.getComment(),
                                defaultComponent.getX(),
                                defaultComponent.getY()));
                } else {
                    throw new DbcException("A default component is needed!");
                }

                // TODO totally rework
                // this replaces the names of the write to pin blocks with the new ones
                List<List<Phrase<Void>>> tree = project.getProgramAst().getTree();
                for ( List<Phrase<Void>> phrases : tree ) {
                    for ( ListIterator<Phrase<Void>> iterator = phrases.listIterator(); iterator.hasNext(); ) {
                        Phrase<Void> phrase = iterator.next();
                        if (phrase.getKind().equals(usedConfComp.getType())) {
                            PinWriteValueAction<Void> action = (PinWriteValueAction<Void>) phrase;
                            if (action.getPort().equals(usedConfComp.getPort()) && action.getMode().equals(usedConfComp.getMode())) {
                                iterator.set(PinWriteValueAction.make(action.getMode(), getNewUserDefinedName(usedConfComp.getPort(), usedConfComp.getMode()), action.getValue(), false, action.getProperty(), action.getComment()));
                            }
                        }
                    }
                }
            }

            ConfigurationAst.Builder builder = new ConfigurationAst.Builder();
            builder.addComponents(new ArrayList<>(components.values()));
            builder.setXmlVersion(project.getConfigurationAst().getXmlVersion());
            builder.setTags(project.getConfigurationAst().getTags());
            builder.setRobotType(project.getConfigurationAst().getRobotType());
            builder.setDescription(project.getConfigurationAst().getDescription());
            ConfigurationAst configuration = builder.build();
            project.addConfigurationAst(configuration);
        }
    }
}
