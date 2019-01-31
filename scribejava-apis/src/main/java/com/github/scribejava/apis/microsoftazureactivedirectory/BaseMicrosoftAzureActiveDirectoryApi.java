package com.github.scribejava.apis.microsoftazureactivedirectory;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignature;
import com.github.scribejava.core.oauth2.clientauthentication.ClientAuthentication;
import com.github.scribejava.core.oauth2.clientauthentication.RequestBodyAuthenticationScheme;

public abstract class BaseMicrosoftAzureActiveDirectoryApi extends DefaultApi20 {

    private static final String MSFT_LOGIN_URL = "https://login.microsoftonline.com";
    private static final String SLASH = "/";
    private static final String COMMON = "common";
    private final String tokenUri;
    private final String tenantId;
    private final MicrosoftAzureActiveDirectoryVersion activeDirectoryVersion;

    protected BaseMicrosoftAzureActiveDirectoryApi(MicrosoftAzureActiveDirectoryVersion version) {
        this(version, COMMON);
    }

    protected BaseMicrosoftAzureActiveDirectoryApi(MicrosoftAzureActiveDirectoryVersion version, String tenantId) {
        this.activeDirectoryVersion = version;
        this.tenantId = (tenantId != null && !tenantId.isEmpty()) ? tenantId : COMMON;
        this.tokenUri = "oauth2" + version.getEndpointVersionPath() + "/token";
    }

    @Override
    public String getAccessTokenEndpoint() {
        return MSFT_LOGIN_URL + SLASH + tenantId + SLASH + tokenUri;
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        String authotizationBaseUrl =  MSFT_LOGIN_URL + SLASH + tenantId + SLASH + "oauth2" +
                activeDirectoryVersion.getEndpointVersionPath() + "/authorize?";

        if (activeDirectoryVersion == MicrosoftAzureActiveDirectoryVersion.V_1_0) {
            authotizationBaseUrl += "resource=" + activeDirectoryVersion.getResource();
        }

        return authotizationBaseUrl;
    }

    @Override
    public ClientAuthentication getClientAuthentication() {
        return RequestBodyAuthenticationScheme.instance();
    }

    @Override
    public BearerSignature getBearerSignature() {
        return activeDirectoryVersion.getBearerSignature();
    }

    protected enum MicrosoftAzureActiveDirectoryVersion {
        V_1_0("https://graph.windows.net", MicrosoftAzureActiveDirectoryBearerSignature.instance(), ""),
        V_2_0("https://graph.microsoft.com", MicrosoftAzureActiveDirectory20BearerSignature.instance(), "/v2.0");

        private final String resource;
        private final BaseMicrosoftAzureActiveDirectoryBearerSignature bearerSignature;
        private final String endpointVersionPath;

        MicrosoftAzureActiveDirectoryVersion(String resource,
                BaseMicrosoftAzureActiveDirectoryBearerSignature bearerSignature, String endpointVersionPath) {
            this.resource = resource;
            this.bearerSignature = bearerSignature;
            this.endpointVersionPath = endpointVersionPath;
        }

        protected String getResource() {
            return resource;
        }

        protected BaseMicrosoftAzureActiveDirectoryBearerSignature getBearerSignature() {
            return bearerSignature;
        }

        protected String getEndpointVersionPath() {
            return endpointVersionPath;
        }
    }
}
