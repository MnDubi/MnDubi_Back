package festival.dev;

import festival.dev.domain.gorupTDL.presentation.GroupController;
import festival.dev.domain.gorupTDL.service.GroupService;
import festival.dev.domain.user.service.UserService;
import festival.dev.global.security.jwt.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest
@WebMvcTest(controllers = GroupController.class)
class DevApplicationTests {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    GroupService groupService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    UserService userService;

    @Test
    @WithMockUser(username = "김더미",roles = {"USER"})
    void testEndpointReturnsSuccess() throws Exception {
        mockMvc.perform(get("/group/toDoList/test"))
                .andExpect(status().isOk())
                .andExpect(content().string("success"));
    }

}
