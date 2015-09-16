package com.jaspersoft.jasperserver.jaxrs.client.core;//package com.jaspersoft.jasperserver.jaxrs.client.core;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.testng.PowerMockTestCase;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.suppress;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * Unit tests for {@link com.jaspersoft.jasperserver.jaxrs.client.core.SessionStorage}
 */
@PrepareForTest({SessionStorage.class, SSLContext.class, EncryptionUtils.class, ClientBuilder.class})
public class SessionStorageTest extends PowerMockTestCase {

    @Mock
    private ClientBuilder builderMock;
    @Mock
    private RestClientConfiguration configurationMock;
    @Mock
    private AuthenticationCredentials credentialsMock;
    @Mock
    private SSLContext ctxMock;
    @Mock
    private Client clientMock;
    @Mock
    private WebTarget targetMock;
    @Mock
    private Invocation.Builder invocationBuilderMock;
    @Mock
    private Response responseMock;
    @Mock
    public SSLContext sslContextMock;
    @Mock
    public Response.StatusType statusTypeMock;


    @BeforeMethod
    public void before() {
        initMocks(this);
    }

    @Test
    public void should_init_ssl() {

        /** - mock for static method **/
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newBuilder()).thenReturn(builderMock);

        /** - mocks for {@link SessionStorage#init()} method (no SSL) **/
        doReturn("https://54.83.98.156/jasperserver-pro").when(configurationMock).getJasperReportsServerUrl();
        doReturn(10000).when(configurationMock).getConnectionTimeout();
        doReturn(8000).when(configurationMock).getReadTimeout();
        doReturn(clientMock).when(builderMock).build();
        doReturn(targetMock).when(clientMock).target("http://54.83.98.156/jasperserver-pro");
        try {
            new SessionStorage(configurationMock, credentialsMock);
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

    @Test
    public void should_invoke_init_method_with_default_configuration() throws Exception {
        //given
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newBuilder()).thenReturn(builderMock);
        doReturn("http://54.83.98.156/jasperserver-pro").when(configurationMock).getJasperReportsServerUrl();
        doReturn(clientMock).when(builderMock).build();
        doReturn(null).when(configurationMock).getConnectionTimeout();
        doReturn(null).when(configurationMock).getReadTimeout();
        doReturn(targetMock).when(clientMock).target(anyString());
        doReturn(targetMock).when(targetMock).register(JacksonFeature.class);
        doReturn(targetMock).when(targetMock).register(any(JacksonJaxbJsonProvider.class));
        doReturn(false).when(configurationMock).getLogHttp();
        //when
        SessionStorage sessionStorage = new SessionStorage(configurationMock, credentialsMock);
        //then
        assertEquals(Whitebox.getInternalState(sessionStorage, "configuration"), configurationMock);
        assertEquals(Whitebox.getInternalState(sessionStorage, "credentials"), credentialsMock);
        verify(configurationMock, times(2)).getJasperReportsServerUrl();
        verify(builderMock).build();
        verify(configurationMock).getConnectionTimeout();
        verify(configurationMock).getReadTimeout();
        verify(clientMock).target("http://54.83.98.156/jasperserver-pro");
        verify(targetMock).register(JacksonFeature.class);
        verify(targetMock).register(isA(JacksonJaxbJsonProvider.class));
        verify(configurationMock).getLogHttp();
        verify(targetMock, never()).register(LoggingFilter.class);
    }

    @Test
    public void should_invoke_init_method_with_custom_configuration() throws Exception {
        //given
        mockStatic(ClientBuilder.class);
        when(ClientBuilder.newBuilder()).thenReturn(builderMock);
        doReturn("http://54.83.98.156/jasperserver-pro").when(configurationMock).getJasperReportsServerUrl();
        doReturn(clientMock).when(builderMock).build();
        doReturn(1000).when(configurationMock).getConnectionTimeout();
        doReturn(clientMock).when(clientMock).property("jersey.config.client.connectTimeout", 1000);
        doReturn(200).when(configurationMock).getReadTimeout();
        doReturn(clientMock).when(clientMock).property("jersey.config.client.readTimeout", 200);
        doReturn(targetMock).when(clientMock).target(anyString());
        doReturn(targetMock).when(targetMock).register(JacksonFeature.class);
        doReturn(targetMock).when(targetMock).register(any(JacksonJaxbJsonProvider.class));
        doReturn(true).when(configurationMock).getLogHttp();
        doReturn(targetMock).when(targetMock).register(any(LoggingFilter.class));
        //when
        SessionStorage sessionStorage = new SessionStorage(configurationMock, credentialsMock);
        //then
        assertEquals(Whitebox.getInternalState(sessionStorage, "configuration"), configurationMock);
        assertEquals(Whitebox.getInternalState(sessionStorage, "credentials"), credentialsMock);
        verify(configurationMock, times(2)).getJasperReportsServerUrl();
        verify(builderMock).build();
        verify(configurationMock).getConnectionTimeout();
        verify(configurationMock).getReadTimeout();
        verify(clientMock).property("jersey.config.client.connectTimeout", 1000);
        verify(clientMock).property("jersey.config.client.readTimeout", 200);
        verify(clientMock).target("http://54.83.98.156/jasperserver-pro");
        verify(targetMock).register(JacksonFeature.class);
        verify(targetMock).register(isA(JacksonJaxbJsonProvider.class));
        verify(configurationMock).getLogHttp();
        verify(targetMock).register(isA(LoggingFilter.class));
    }

    @Test
    public void should_create_new_instance_session_storage() throws Exception {

        // Given
        suppress(method(SessionStorage.class, "init"));

        // When
        SessionStorage sessionStorageSpy = new SessionStorage(configurationMock, credentialsMock);

        // Then
        assertNotNull(sessionStorageSpy);
        assertNotNull(Whitebox.getInternalState(sessionStorageSpy, "configuration"));
        assertNotNull(Whitebox.getInternalState(sessionStorageSpy, "credentials"));
        assertEquals(Whitebox.getInternalState(sessionStorageSpy, "rootTarget"), null);
        assertEquals(Whitebox.getInternalState(sessionStorageSpy, "sessionId"), null);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void should_throw_an_exception_when_unable_to_init_SSL_context() throws Exception {

        // Given
        TrustManager[] managers = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }};

        mockStatic(ClientBuilder.class, SSLContext.class);
        when(ClientBuilder.newBuilder()).thenReturn(builderMock);
        when(SSLContext.getInstance("SSL")).thenReturn(ctxMock);

        PowerMockito.when(ctxMock, "init", null, managers, new SecureRandom()).thenThrow(new RuntimeException());

        doReturn("https://abc").when(configurationMock).getJasperReportsServerUrl();
        doReturn(managers).when(configurationMock).getTrustManagers();
        doReturn(100L).when(configurationMock).getReadTimeout();

        // When
        new SessionStorage(configurationMock, credentialsMock);

        // Then throw an exception
    }

    @Test
    public void should_set_and_get_state_for_object() {

        // Given
        suppress(method(SessionStorage.class, "init"));
        doReturn("http").when(configurationMock).getJasperReportsServerUrl();

        SessionStorage sessionStorage = new SessionStorage(configurationMock, credentialsMock);

        // When
        Whitebox.setInternalState(sessionStorage, "rootTarget", targetMock);
        Whitebox.setInternalState(sessionStorage, "sessionId", "sessionId");
        // Then
        assertNotNull(sessionStorage.getConfiguration());
        assertNotNull(sessionStorage.getCredentials());
        assertNotNull(sessionStorage.getRootTarget());
        assertNotNull(sessionStorage.getSessionId());
    }

    @AfterMethod
    public void after() {
        reset(builderMock, configurationMock, credentialsMock, invocationBuilderMock, responseMock, ctxMock, clientMock, targetMock);
    }

}