import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.api.settings.client.UserClient;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TestUpdateUser {
    ValidatableResponse response;
    String token;
    String userMail = RandomStringUtils.randomAlphabetic(10) + "@mail.ru";
    String userPassword = RandomStringUtils.randomAlphabetic(10);
    String userName = RandomStringUtils.randomAlphabetic(10);
    String newUserMail = RandomStringUtils.randomAlphabetic(10) + "@mail.ru";
    String newUserPassword = RandomStringUtils.randomAlphabetic(10);
    String newUserName = RandomStringUtils.randomAlphabetic(10);

    @Before
    @DisplayName("Создаём пользователя")
    public void createUser() throws InterruptedException {
        TimeUnit.SECONDS.sleep(1);
        response = UserClient.create(userMail, userPassword, userName);
        UserClient.assertStatusAndBodyTrue(response, 200);
        token = new StringBuilder(response.extract().path("accessToken")).delete(0, 6).toString();
    }

    @After
    @DisplayName("Удаляем созданного пользователя")
    public void deleteUser() {
        ValidatableResponse deleteResponse = UserClient.delete(token);
        UserClient.assertStatusAndBodyTrue(deleteResponse, 202);
    }

    @Test
    @DisplayName("Обновляем все данные пользователя с авторизацией - успешно (С проверкой логина под новым паролем т.к. его нет в ответе")
    public void updateAllUserDataWhenAuthorizedSuccess() {
        ValidatableResponse updateResponse = UserClient.updateAll(token, newUserMail, newUserPassword, newUserName);
        UserClient.assertStatusAndBodyTrue(updateResponse, 200);
        UserClient.assertNewData(updateResponse, "email", newUserMail.toLowerCase(Locale.ROOT));
        UserClient.assertNewData(updateResponse, "name", newUserName);
        UserClient.login(newUserMail, newUserPassword).statusCode(200);
    }

    @Test
    @DisplayName("Обновляем эмейл пользователя с авторизацией - успешно")
    public void updateEmailUserWhenAuthorizedSuccess() {
        ValidatableResponse updateResponse = UserClient.updateEmail(token, newUserMail);
        UserClient.assertStatusAndBodyTrue(updateResponse, 200);
        UserClient.assertNewData(updateResponse, "email", newUserMail.toLowerCase(Locale.ROOT));
    }

    @Test
    @DisplayName("Обновляем пароль пользователя с авторизацией - успешно (С проверкой логина под новым паролем т.к. его нет в ответ")
    public void updatePasswordUserWhenAuthorizedSuccess() {
        ValidatableResponse updateResponse = UserClient.updatePassword(token, newUserPassword);
        UserClient.assertStatusAndBodyTrue(updateResponse, 200);
        UserClient.login(userMail, newUserPassword).statusCode(200);
    }

    @Test
    @DisplayName("Обновляем имя пользователя с авторизацией - успешно")
    public void updateNameUserWhenAuthorizedSuccess() {
        ValidatableResponse updateResponse = UserClient.updateName(token, newUserName);
        UserClient.assertStatusAndBodyTrue(updateResponse, 200);
        UserClient.assertNewData(updateResponse, "name", newUserName);
    }

    @Test
    @DisplayName("Обновляем все данные пользователя БЕЗ авторизации - неуспешно")
    public void updateAllUserDataWhenUnauthorizedFailed() {
        ValidatableResponse updateResponse = UserClient.updateAll("", newUserMail, newUserPassword, newUserName);
        UserClient.assertStatusAndBodyFalse(updateResponse, 401);
    }

    @Test
    @DisplayName("Обновляем эмейл пользователя БЕЗ авторизации - неуспешно")
    public void updateEmailUserWhenUnauthorizedFailed() {
        ValidatableResponse updateResponse = UserClient.updateEmail("", newUserMail);
        UserClient.assertStatusAndBodyFalse(updateResponse, 401);
    }

    @Test
    @DisplayName("Обновляем пароль пользователя БЕЗ авторизации - неуспешно")
    public void updatePasswordDataWhenUnauthorizedFailed() {
        ValidatableResponse updateResponse = UserClient.updatePassword("", newUserPassword);
        UserClient.assertStatusAndBodyFalse(updateResponse, 401);
    }

    @Test
    @DisplayName("Обновляем имя пользователя БЕЗ авторизации - неуспешно")
    public void updateNameUserWhenUnauthorizedFailed() {
        ValidatableResponse updateResponse = UserClient.updateName("", newUserName);
        UserClient.assertStatusAndBodyFalse(updateResponse, 401);
    }
}
