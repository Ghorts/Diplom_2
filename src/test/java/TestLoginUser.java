import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.api.settings.client.UserClient;

import java.util.concurrent.TimeUnit;

public class TestLoginUser {
    ValidatableResponse response;
    String token;
    String userMail = RandomStringUtils.randomAlphabetic(10) + "@mail.ru";
    String userPassword = RandomStringUtils.randomAlphabetic(10);
    String userName = RandomStringUtils.randomAlphabetic(10);

    @Before
    @DisplayName("Создаём пользователя")
    public void createUser() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1000);
        response = UserClient.create(userMail, userPassword, userName);
        UserClient.assertStatusAndBodyTrue(response, 200);
    }

    @After
    @DisplayName("Удаляем созданного пользователя")
    public void deleteUser() {
        token = new StringBuilder(response.extract().path("accessToken")).delete(0, 6).toString();
        ValidatableResponse deleteResponse = UserClient.delete(token);
        UserClient.assertStatusAndBodyTrue(deleteResponse, 202);
    }

    @Test
    @DisplayName("Логин под существующим пользователем - успешно")
    public void loginWhenUserExistSuccess() {
        ValidatableResponse loginResponse = UserClient.login(userMail, userPassword);
        UserClient.assertStatusAndBodyTrue(loginResponse, 200);
    }

    @Test
    @DisplayName("Логин с неверным логином - неуспешно")
    public void loginWhenIncorrectLoginFailed() {
        ValidatableResponse loginResponse = UserClient.login("fg3h@mail.ru", userPassword);
        UserClient.assertStatusAndBodyFalse(loginResponse, 401);
    }

    @Test
    @DisplayName("Логин с неверным паролем - неуспешно")
    public void loginWhenIncorrectPasswordFailed() {
        ValidatableResponse loginResponse = UserClient.login(userMail, "123");
        UserClient.assertStatusAndBodyFalse(loginResponse, 401);
    }

    @Test
    @DisplayName("Логин с неверным логином и паролем - неуспешно")
    public void loginWhenIncorrectLoginAndPasswordFailed() {
        ValidatableResponse loginResponse = UserClient.login("fg3h@mail.ru", "123");
        UserClient.assertStatusAndBodyFalse(loginResponse, 401);
    }
}