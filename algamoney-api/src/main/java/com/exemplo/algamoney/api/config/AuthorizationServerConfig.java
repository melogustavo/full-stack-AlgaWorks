package com.exemplo.algamoney.api.config;

import antlr.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;


    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("angular")
                .secret("$2a$10$G1j5Rf8aEEiGc/AET9BA..xRR.qCpOUzBZoJd8ygbGy6tb3jsMT9G")
                .scopes("read", "write")   //Com esse escopo vc consegue limitar o que o tal cliente vai conseguir acessar
                .authorizedGrantTypes("password", "refresh_token") //esse refresh token eh o que eh usado para dar um novo access token
                .accessTokenValiditySeconds(60 * 15)
                .refreshTokenValiditySeconds(3600 * 24)
            .and()
                .withClient("mobile")
                .secret("$2a$10$KJRZ.d9lgifvJU420wX7Oeb2sA3mgnGjv9iyUWNqcN1RxjXnKfcKK") // m0b1l30
                .scopes("read") //vc ta definindo que o escopo para mobile é apenas de leitura, então mesmo se tiver com o user admin ele não vai deixar vc adicionar dados... o escopo está listado lá no controller
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(1800)
                .refreshTokenValiditySeconds(3600 * 24);

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .tokenStore(tokenStore())  //Lugar para armazenar o token que vai retornar
                .accessTokenConverter(accessTokenConverter())
                .reuseRefreshTokens(false) //enquanto o usuario estiver usando a aplicação todo dia, ele não vai ser deslogado
                .userDetailsService(this.userDetailsService)
                .authenticationManager(authenticationManager);

    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setSigningKey("algaworks"); //essa é a chave que valida o token
        return accessTokenConverter;
    }

    //PARA GUARDAR O TOKEN
    @Bean
    public TokenStore tokenStore(){
        return new JwtTokenStore(accessTokenConverter());
    }



}
