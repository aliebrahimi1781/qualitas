package com.google.code.qualitas.engines.api.factory;

import com.google.code.qualitas.engines.api.component.Component;
import com.google.code.qualitas.engines.api.core.Bundle;

/**
 * A factory for creating ProcessBundle objects.
 */
public interface BundleFactory extends Component {

    /**
     * Creates a new ProcessBundle object.
     * 
     * @param contents
     *            the contents
     * @return the process bundle
     * @throws BundleCreationException
     *             the bundle creation exception
     */
    Bundle createProcessBundle(byte[] contents) throws BundleCreationException;

}
