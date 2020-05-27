package de.fhg.iais.roberta.syntax.action;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import de.fhg.iais.roberta.util.dbc.DbcException;

public interface IActionBuilder<C> {

    IActionBuilder<C> setOriginal(C original);

    default IActionBuilder<C> set(String fieldName, Object value) {
        try {
            Method method = this.getClass().getMethod("set" + fieldName.substring(0, 1).toUpperCase(Locale.ENGLISH) + fieldName.substring(1), value.getClass());
            method.invoke(this, value);
        } catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException e ) {
            throw new DbcException("The setter for field " + fieldName + " does not exist!", e);
        }
        return this;
    }

    C build();
}
