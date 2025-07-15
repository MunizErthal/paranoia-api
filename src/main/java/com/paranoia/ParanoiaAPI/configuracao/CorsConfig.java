package com.paranoia.ParanoiaAPI.configuracao;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration config = new CorsConfiguration();

		// Permitir apenas origens específicas
		//config.setAllowedOrigins(List.of("https://paranoiajogos.com.br", "capacitor://localhost", "ionic://localhost"));
		config.addAllowedOrigin("*");

		// Permitir apenas cabeçalhos específicos
		config.setAllowedHeaders(List.of("Authorization", "token", "Cache-Control", "Content-Type"));

		// Permitir apenas métodos específicos
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));

		// Permitir credenciais (cookies, autorização via cabeçalhos)
		//config.setAllowCredentials(true);

		source.registerCorsConfiguration("/**", config);
		FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
		bean.setOrder(0);
		return bean;
	}

    /*
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        // Permitir apenas o frontend local
        config.addAllowedOrigin("https://localhost:4200");

        // Permitir todos os cabeçalhos
        config.addAllowedHeader("*");

        // Permitir todos os métodos
        config.addAllowedMethod("*");

        source.registerCorsConfiguration("/**", config);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }*/
}
