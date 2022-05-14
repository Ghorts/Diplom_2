import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import test.api.settings.client.UserClient;

import java.util.concurrent.TimeUnit;

public class TestCreatingUser {
    ValidatableResponse response;
    String userMail = RandomStringUtils.randomAlphabetic(10) + "@mail.ru";
    String userPassword = RandomStringUtils.randomAlphabetic(10);
    String userName = RandomStringUtils.randomAlphabetic(10);

    @Test
    @DisplayName("Создать пользователя и не заполнить email - неуспешно")
    public void createUserWithoutEmailFalse() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(500);
        response = UserClient.create("", userPassword, userName);
        UserClient.assertStatusAndBodyFalse(response, 403);
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить password - неуспешно")
    public void createUserWithoutPasswordFalse() {
        response = UserClient.create(userMail, "", userName);
        UserClient.assertStatusAndBodyFalse(response, 403);
    }

    @Test
    @DisplayName("Создать пользователя и не заполнить name - неуспешно")
    public void createUserWithoutNameFalse() {
        response = UserClient.create(userMail, userPassword, "");
        UserClient.assertStatusAndBodyFalse(response, 403);
    }
}