package evoila.cf.broker.openid;

import de.evoila.cf.broker.controller.AuthenticationController;
import de.evoila.cf.broker.controller.utils.ApiLocationInfo;
import de.evoila.cf.broker.exception.PlatformException;
import de.evoila.cf.broker.model.DashboardClient;
import de.evoila.cf.broker.model.ServiceInstance;
import de.evoila.cf.broker.model.oauth.CompositeAccessToken;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Map;

/**
 * @author Johannes Hiemer.
 */
public class OpenIdAuthenticationUtils {
    private final static Logger log = LoggerFactory.getLogger(OpenIdAuthenticationUtils.class);
    private final static String AUTH_CODE = "code";


    private static String getAuthCode(String location) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(location);
        String authCode = null;
        for (NameValuePair queryParam : uriBuilder.getQueryParams())
            if (queryParam.getName().equals(AUTH_CODE))
                authCode = queryParam.getValue();

        return authCode;
    }

    public static CompositeAccessToken getAccessAndRefreshToken(ServiceInstance instance,
                                                                ApiLocationInfo info,
                                                                String code,
                                                                DashboardClient dashboardClient,
                                                                String redirectUri
                                                                ) throws RestClientException {
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " +code);

        MultiValueMap<String, String> form = getFormBody(code, dashboardClient, redirectUri);

        String oauthEndpoint = info.getTokenEndpoint();
        ResponseEntity<CompositeAccessToken> token;
        try {
            token = template.exchange(oauthEndpoint,
                                      HttpMethod.POST,
                                      new HttpEntity<>(form, headers),
                                      CompositeAccessToken.class
            );
        } catch (HttpClientErrorException ex){
            log.info(dashboardClient.getRedirectUri());
            log.info("Failed aquirering access-token.");
            log.info(ex.getMessage());
            log.info(ex.getResponseBodyAsString());
            throw ex;
        }
        if (token != null) {
            return token.getBody();
        } else
            return null;
    }

    private static MultiValueMap<String, String> getFormBody (String code, DashboardClient dashboardClient, String redirectUri) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", dashboardClient.getId());
        form.add("client_secret", dashboardClient.getSecret());
        form.add("code", code);
        form.add("redirect_uri", redirectUri);
        return form;
    }

    public static CompositeAccessToken getAccessAndRefreshToken(String oauthEndpoint, String code, DashboardClient dashboardClient,
                                                          String redirectUri) throws RestClientException {
        String clientBasicAuth = getClientBasicAuthHeader(dashboardClient.getId(),  dashboardClient.getSecret());
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, clientBasicAuth);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = getFormBody(code, dashboardClient, redirectUri);

        form.add("response_type", "token");

        ResponseEntity<CompositeAccessToken> token = template.exchange(oauthEndpoint + "/token",
                HttpMethod.POST, new HttpEntity<>(form, headers), CompositeAccessToken.class);

        if (token != null)
            return token.getBody();
        else
            return null;
    }

    private static String getClientBasicAuthHeader(String clientId, String clientSecret) {
        try {
            byte[] autbytes = Base64.encode(String.format("%s:%s", clientId, clientSecret).getBytes("UTF-8"));
            String base64 = new String(autbytes);
            return String.format("Basic %s", base64);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static boolean hasPermissions (ServiceInstance serviceInstance, CompositeAccessToken token) throws URISyntaxException {
        log.info("checking user permission");
        Assert.notNull(serviceInstance, "The service instance may not be null");
        Assert.notNull(token, "The access-token may not be null");

        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.AUTHORIZATION, "Bearer "+ token.getAccessToken());
        URI uri = new URI(serviceInstance.getContext().get(AuthenticationController.PERMISSION_URL));

        ResponseEntity<SSOPermissions> permissions = template.exchange(
                            new RequestEntity<Object>(headers,HttpMethod.GET,uri),
                            SSOPermissions.class);

        log.info("Could get permission");
        log.info(permissions.getBody().toString());
        return permissions.getBody().getPermission().equals(SSOPermissions.PermissionType.USER);
    }


}
