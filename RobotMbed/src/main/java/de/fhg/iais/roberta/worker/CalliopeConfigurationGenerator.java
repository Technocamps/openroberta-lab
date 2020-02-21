package de.fhg.iais.roberta.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.fhg.iais.roberta.bean.NewUsedHardwareBean;
import de.fhg.iais.roberta.components.Category;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.ConfigurationComponent;
import de.fhg.iais.roberta.components.Project;
import de.fhg.iais.roberta.components.UsedConfigurationComponent;

public class CalliopeConfigurationGenerator implements IWorker {
    @Override
    public void execute(Project project) {
        if ( project.isDefaultConfiguration() ) {
            NewUsedHardwareBean usedHardwareBean = project.getWorkerResult(NewUsedHardwareBean.class);

            Map<String, ConfigurationComponent> components = new HashMap<>(project.getConfigurationAst().getConfigurationComponents());
            for ( UsedConfigurationComponent configurationComponent : usedHardwareBean.getUsedConfigurationComponents() ) {
                ConfigurationComponent defaultComponent = components.get(configurationComponent.getPort());
                if ( defaultComponent != null ) { // TODO every component needs a default value, location, etc -> for now loaded from default configuration
                    components
                        .put(
                            configurationComponent.getPort(),
                            new ConfigurationComponent(
                                defaultComponent.getComponentType(),
                                configurationComponent.getType().getCategory() == Category.ACTOR,
                                configurationComponent.getPort(),
                                configurationComponent.getPort(),
                                defaultComponent.getComponentProperties(),
                                defaultComponent.getProperty(),
                                defaultComponent.getComment(),
                                defaultComponent.getX(),
                                defaultComponent.getY()));
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
