package de.fhg.iais.roberta.bean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.fhg.iais.roberta.components.UsedConfigurationComponent;

public class NewUsedHardwareBean implements IProjectBean {
    private final List<UsedConfigurationComponent> usedConfigurationComponents = new ArrayList<>();

    public List<UsedConfigurationComponent> getUsedConfigurationComponents() {
        return Collections.unmodifiableList(usedConfigurationComponents);
    }

    public static class Builder implements IBuilder<NewUsedHardwareBean> {
        private final NewUsedHardwareBean usedHardwareBean = new NewUsedHardwareBean();

        public NewUsedHardwareBean.Builder addUsedConfigurationComponent(UsedConfigurationComponent actor) {
            this.usedHardwareBean.usedConfigurationComponents.add(actor);
            return this;
        }

        public NewUsedHardwareBean build() {
            return this.usedHardwareBean;
        }
    }
}
