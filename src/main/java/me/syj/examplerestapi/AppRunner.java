package me.syj.examplerestapi;

import lombok.RequiredArgsConstructor;
import me.syj.examplerestapi.accounts.Account;
import me.syj.examplerestapi.accounts.AccountRole;
import me.syj.examplerestapi.accounts.AccountService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@RequiredArgsConstructor
public class AppRunner implements ApplicationRunner {

    private final AccountService accountService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        HashSet<AccountRole> set = new HashSet<>();
        set.add(AccountRole.ADMIN);
        set.add(AccountRole.USER);
        accountService.join(Account.builder()
                .email("nmrhtn7898@naver.com")
                .password("1234")
                .roles(set)
                .build());
    }
}
