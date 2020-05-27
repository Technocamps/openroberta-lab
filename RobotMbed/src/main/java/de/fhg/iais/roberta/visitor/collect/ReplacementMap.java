package de.fhg.iais.roberta.visitor.collect;

import java.util.HashMap;
import java.util.Map;

import de.fhg.iais.roberta.components.UsedConfigurationComponent;

public class ReplacementMap {
    private Map<UsedConfigurationComponent, Map<String, Object>> replacementsForComponents = new HashMap<>();

    public void addReplacement(UsedConfigurationComponent component, String fieldName, Object replacement) {
        if ( this.replacementsForComponents.get(component) == null) {
            // Create new map for type
            Map<String, Object> replacements = new HashMap<>();

            this.replacementsForComponents.put(component, replacements);
        }

        this.replacementsForComponents.get(component).put(fieldName, replacement);
    }

    public Map<String, Object> getReplacement(UsedConfigurationComponent component) {
        return this.replacementsForComponents.get(component);
    }
}
