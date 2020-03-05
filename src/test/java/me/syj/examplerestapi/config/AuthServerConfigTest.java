package me.syj.examplerestapi.config;

import me.syj.examplerestapi.accounts.Account;
import me.syj.examplerestapi.accounts.AccountRole;
import me.syj.examplerestapi.accounts.AccountService;
import me.syj.examplerestapi.common.BaseControllerTest;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @DisplayName("인증 토근을 발급 받는 테스트")
    public void getAUthToken() throws Exception {
        // given
        String username = "nmrhtn7898@naver.com";
        String password = "1234";
        HashSet<AccountRole> set = new HashSet<>();
        set.add(AccountRole.ADMIN);
        set.add(AccountRole.USER);
        Account account = Account.builder()
                .email(username)
                .password(password)
                .roles(set)
                .build();
        accountService.join(account);

        String clientId = "myApp";
        String clientSecret = "pass";

        mockMvc.perform(post("/oauth/token")
                .with(httpBasic(clientId, clientSecret))
                .param("username", username)
                .param("password", password)
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists())
        ;
    }

}