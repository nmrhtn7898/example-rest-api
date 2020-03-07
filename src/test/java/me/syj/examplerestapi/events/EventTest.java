package me.syj.examplerestapi.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import me.syj.examplerestapi.accounts.AccountRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void builder() {
        // given
        String name = "syj";
        String description = "test is success?";
        // when
        Event event = Event.builder()
                .name(name)
                .description(description)
                .build();
        // then
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBean() {
        // given
        String name = "syj";
        String description = "test is success?";
        // when
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        // then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);

    }

    private Object[] parametersForTestFree() {
        return new Object[] {
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {0, 100, false},
                new Object[] {100, 200, false}
        };
    }

    @Test
    @Parameters(method = "parametersForTestFree") // convention -> parametersFor + method name 생략 가능
    public void testFree(int basePrice, int maxPrice, boolean isFree) {
        // given
        Event event = Event.builder()
                .basePrice(basePrice)
                .maxPrice(maxPrice)
                .build();
        // when
        event.update();
        // then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private Object[] parametersForTestOffline() {
        return new Object[] {
                new Object[] {"강남", true},
                new Object[] {null, false},
                new Object[] {"       ", false},
        };
    }

    @Test
    @Parameters(method = "parametersForTestOffline")
    public void testOffline(String location, boolean isOffline) {
        // given
        Event event = Event.builder()
                .location(location)
                .build();
        // when
        event.update();
        // then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

}