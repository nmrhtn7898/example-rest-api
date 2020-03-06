package me.syj.examplerestapi.accounts;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Autowired
    AccountService accountService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    public void findByUserName() {
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
        // when
        UserDetailsService userDetailsService =  accountService;
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        // then
        assertThat(passwordEncoder.matches(password, userDetails.getPassword()));
    }

    @Test
    public void findByUsernameFail() {
        // given
        String username = "nmrhtn7898@naver.com";
        expectedException.expect(UsernameNotFoundException.class); // 예측하는 예외
        expectedException.expectMessage(Matchers.containsString(username)); // 예측하는 예외의 메세지
        // when
        accountService.loadUserByUsername(username);
    }

}