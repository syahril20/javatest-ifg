package org.acme.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.json.JsonObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.ws.rs.core.Response;
import org.acme.models.UserModels;
import org.acme.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

//@QuarkusTest
@Extensions({
        @ExtendWith(MockitoExtension.class)
})
public class UserTest {

    private EntityManager em;

    @Mock
    UserService userService;

    @Test
    public void testGetSuccess() {
        UserModels user = new UserModels();
        user.setId(1L);
        user.setName("Syahril");
        user.setCity("Bandung");

        JsonObject expectedResult = new JsonObject();
        expectedResult.put("status", 200);
        expectedResult.put("message", "SUCCESS");
        expectedResult.put("payload", user);
        System.out.println(expectedResult);

        when(userService.getAllUser()).thenReturn(Response.ok(expectedResult).build());

        Response response = userService.getAllUser();

        assertEquals(expectedResult, response.getEntity());
    }

}
