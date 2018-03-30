package springbackend;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Adds the Config object to the model for all requests.
 *
 * @author
 */
public class ConfigHandlerInterceptor extends HandlerInterceptorAdapter {
    private ConfigView configView;

    public ConfigHandlerInterceptor(@Autowired Config config) {
        configView = new ConfigView();
        configView.serverUrl = config.getServerUrl();
    }

    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler, final ModelAndView modelAndView) throws Exception {
        if (modelAndView != null)
            modelAndView.getModelMap().addAttribute("config", configView);
    }


    /**
     * All the config variables we actually want to send to the UI.
     */
    public static class ConfigView {
        public String serverUrl;

        /**
         * @return the serverUrl
         */
        public String getServerUrl() {
            return serverUrl;
        }
    }
}
