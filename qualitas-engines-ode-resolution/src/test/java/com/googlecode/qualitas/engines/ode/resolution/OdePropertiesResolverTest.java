package com.googlecode.qualitas.engines.ode.resolution;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.googlecode.qualitas.engines.api.configuration.ProcessType;
import com.googlecode.qualitas.engines.api.configuration.QualitasConfiguration;
import com.googlecode.qualitas.engines.api.core.Entry;
import com.googlecode.qualitas.engines.api.resolution.ResolutionException;
import com.googlecode.qualitas.engines.ode.core.OdeBundle;

@RunWith(MockitoJUnitRunner.class)
public class OdePropertiesResolverTest {

    @Mock
    private OdePropertiesResolver odePropertiesResolver;

    @Mock
    private OdeBundle bundle;

    @Before
    public void setUp() throws Exception {
        byte[] contents = IOUtils.toByteArray(this.getClass().getResourceAsStream("/deploy.xml"));
        Mockito.when(bundle.getProcessType()).thenReturn(ProcessType.WS_BPEL_2_0_APACHE_ODE);
        Mockito.when(bundle.getOdeDescriptor()).thenReturn(new Entry("deploy.xml", contents));

        Mockito.when(odePropertiesResolver.getQualitasConfiguration(bundle)).thenReturn(
                new QualitasConfiguration());
        Mockito.when(odePropertiesResolver.resolve(bundle)).thenCallRealMethod();
    }

    @Test
    public void testResolve() throws ResolutionException {
        odePropertiesResolver.resolve(bundle);
    }

}
