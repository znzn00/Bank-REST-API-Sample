package com.zhen.backend.model.enumerables.convertidores;

import com.zhen.backend.model.enumerables.Etiquetable;
import org.springframework.core.GenericTypeResolver;

import javax.persistence.AttributeConverter;
import java.util.HashMap;
import java.util.Map;

/* Esta clase esta realizada para automatizar el convertidor de JPA para aquellos enumerables
* que sean guardado como STRING pero mediante etiquetas.
* */
public abstract class BaseConverter<T extends Enum<T> & Etiquetable> implements AttributeConverter<T, String> {
    @SuppressWarnings("unchecked")
    public BaseConverter() {
        Class<T> t = (Class<T>) GenericTypeResolver.resolveTypeArgument(getClass(), BaseConverter.class);
        assert t != null;
        for (T e : t.getEnumConstants()) {
            ETIQUETAS.put(e.getEtiqueta(), e);
        }
    }

    private final Map<String, T> ETIQUETAS = new HashMap<>();


    @Override
    public String convertToDatabaseColumn(T t) {
        return t.getEtiqueta();
    }

    @Override
    public T convertToEntityAttribute(String s) {
        return this.ETIQUETAS.get(s);
    }
}
