package me.syj.examplerestapi.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocsConfiguration.class)
@ActiveProfiles("test")
@Ignore // 테스트 코드가 없는 클래스이므로 테스트를 실행하지 않도록
public class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected ModelMapper modelMapper;

}

/*같은 클래스나 동일 클래스를 상속 받은 클래스들은 동일한 애플리케이션 컨텍스트를 사용하는 것 같음
그래서 BaseController 를 상속 받은 클래스들은 테스트시 애플리케이션 컨텍스트를 동일한 것을 사용하기 때문에
컨텍스트가 테스트 클래스마다 매번 만들어지지 않기 때문에 빈들을 매번 생성하지 않음 하지만 다른 클래스의 테스트 수행시 다른 컨텍스트 사용을 위해 생성 과정에서
ApplicationRunner 또한 빈으로 생성되는 과정에서 중복 데이터베이스 삽입으로 인해 컨텍스트 생성에 실패하면서 테스트가 깨짐
테스트 클래스들의 테스트 순서와는 상관없이 해당 클래스를 상속받아서 사용하면 동일 컨텍스트 사용하게 되면서 러너가 1번 수행되면서 깨지지 않음
현재 모두 상속받지 않고 before, after 애노테이션으로 수행마다 DB 정보 제거하고 있는 상태
다른 애플리케이션 컨텍스트를 사용한다는 것은 매번 DB를 초기화 한다는 것은 아님 빈들만 새로 만든다는 것
단일 메소드 테스트는 수행 후 자동 롤백이지만, 여러개를 한번에 실행하면 상태가 유지 되다가 모든 테스트 종료 후 롤백되기 때문에
중복 데이터 존재로 인해 발생하는 문제임*/

