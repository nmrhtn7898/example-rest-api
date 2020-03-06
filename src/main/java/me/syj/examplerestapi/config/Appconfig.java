package me.syj.examplerestapi.config;

import me.syj.examplerestapi.accounts.Account;
import me.syj.examplerestapi.accounts.AccountRole;
import me.syj.examplerestapi.accounts.AccountService;
import me.syj.examplerestapi.common.AppProperties;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

import java.util.HashSet;

@Configuration
public class Appconfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return new ApplicationRunner() {

            @Autowired
            AccountService accountService;

            @Autowired
            AppProperties appProperties;

            @Override
            public void run(ApplicationArguments args) throws Exception {
                HashSet<AccountRole> adminRoles = new HashSet<>();
                adminRoles.add(AccountRole.ADMIN);
                adminRoles.add(AccountRole.USER);
                Account admin = Account.builder()
                        .email(appProperties.getAdminUsername())
                        .password(appProperties.getAdminPassword())
                        .roles(adminRoles)
                        .build();
                accountService.join(admin);

                HashSet<AccountRole> userRoles = new HashSet<>();
                userRoles.add(AccountRole.USER);
                Account user = Account.builder()
                        .email(appProperties.getUserUsername())
                        .password(appProperties.getUserPassword())
                        .roles(userRoles)
                        .build();
                accountService.join(user);
            }
        };
    }


}
