package springbackend;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    /**
     * Can't connect to the server.
     * @author levimiller
     *
     */
    private static class ServerAuthenticationException extends AuthenticationException {
        private static final long serialVersionUID = 1L;
        public ServerAuthenticationException(String msg) {
            super(msg);
        }
        public ServerAuthenticationException(String msg, Throwable t) {
            super(msg, t);
        }
    }
    
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);
    @Autowired
    private Config config;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new AuthenticationProvider() {
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String email = authentication.getName();
                String password = authentication.getCredentials().toString();
                // contact the server, and try to authenticate
                try {
                    // use the credentials
                    // and authenticate against the third-party system
                   Response r = Request.Post(String.format("%s/authenticate-login", config.getServerUrl()))
                            .bodyForm(Form.form().add("email", email).add("password", password).build())
                            .execute();
                   HttpResponse httpResponse = r.returnResponse();
                   log.debug("{} - {}", email, httpResponse.getStatusLine().getStatusCode());
                   // throw exception if not successful
                   if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                       throw new ServerAuthenticationException("Error connecting to the server.");
                   
                   // convert response to json
                   String json = EntityUtils.toString(httpResponse.getEntity());
                   if(json == null) 
                       throw new BadCredentialsException("Invalid credentials.");
                   
                   JSONObject user = new JSONObject(json);
                   int id = user.getInt("id");
                   
                   return new UsernamePasswordAuthenticationToken(id, password, new ArrayList<>());
                } catch (IOException e) {
                     throw new ServerAuthenticationException("Can't connect to the server.", e);
                }
            }

            @Override
            public boolean supports(Class<?> authentication) {
                return authentication.equals(UsernamePasswordAuthenticationToken.class);
            }
        });
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // set authorization for some pages.
        http.authorizeRequests()
            .antMatchers("/login*")
            .anonymous().anyRequest().authenticated()
            .and().formLogin().loginPage("/login")
            .defaultSuccessUrl("/")
            .failureUrl("/login?error=true")
            .and().logout()
            .logoutSuccessUrl("/login");
    }
}
