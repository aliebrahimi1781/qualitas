package com.google.code.qualitas.internal.installation.core;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Processor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.google.code.qualitas.engines.api.component.Component;
import com.google.code.qualitas.engines.api.core.ProcessType;

/**
 * The Class AbstractProcessor.
 */
public abstract class AbstractProcessor implements Processor {

    /** The Constant LOG. */
    private static final Log LOG = LogFactory.getLog(AbstractProcessor.class);

    /** The context. */
    @Autowired
    private ApplicationContext context;

    /**
     * Finds qualitas component.
     * 
     * @param <T>
     *            the generic type
     * @param type
     *            the type
     * @param processType
     *            the process type
     * @return the T
     * @throws ComponentNotFound
     *             the component not found
     */
    protected <T extends Component> T findQualitasComponent(Class<T> type, ProcessType processType)
            throws ComponentNotFound {
        Map<String, T> components = context.getBeansOfType(type);

        for (String key : components.keySet()) {
            T component = components.get(key);
            if (component.isSupported(processType)) {
                return component;
            }
        }

        throw new ComponentNotFound("Could not find a component of type " + type
                + " for process bundle of type " + processType);
    }

    /**
     * Find qualitas components.
     * 
     * @param <T>
     *            the generic type
     * @param type
     *            the type
     * @param processType
     *            the process type
     * @return the list
     * @throws ComponentNotFound
     *             the component not found
     */
    protected <T extends Component> List<T> findQualitasComponents(Class<T> type,
            ProcessType processType) throws ComponentNotFound {
        Map<String, T> allComponents = context.getBeansOfType(type);

        List<T> components = new ArrayList<T>();

        for (String key : allComponents.keySet()) {
            T component = allComponents.get(key);
            if (component.isSupported(processType)) {
                components.add(component);
            }
        }

        if (components.size() == 0) {
            throw new ComponentNotFound("Could not find a component of type " + type
                    + " for process bundle of type " + processType);
        }

        return components;
    }

    /**
     * Find qualitas component.
     * 
     * @param <T>
     *            the generic type
     * @param <A>
     *            the generic type
     * @param type
     *            the type
     * @param processType
     *            the process type
     * @param annotationType
     *            the annotation type
     * @return the T
     * @throws ComponentNotFound
     *             the component not found
     */
    protected <T extends Component, A extends Annotation> T findQualitasComponent(Class<T> type,
            ProcessType processType, Class<A> annotationType) throws ComponentNotFound {
        List<T> components = findQualitasComponents(type, processType);

        T component = null;
        A annotation = null;

        for (T c : components) {
            annotation = c.getClass().getAnnotation(annotationType);
            if (annotation != null) {
                component = c;
                break;
            }
        }

        if (component == null) {
            throw new ComponentNotFound("Could not find a component of type " + type
                    + " for process bundle of type " + processType + " and annotation of type "
                    + annotationType);
        }

        return component;
    }

}
