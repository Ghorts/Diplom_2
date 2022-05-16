import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.api.settings.client.UserClient;
import test.data.GeneratorTestData;

import java.util.Locale;

public class TestUpdateUser {
    ValidatableResponse response;
    String token;
    String userMail = GeneratorTestData.getRandomMail();
    String userPassword = GeneratorTestData.getRandomString();
    String userName = GeneratorTestData.getRandomString();
    String newUserMail = GeneratorTestData.getRandomMail();
    String newUserPassword = GeneratorTestData.getRandomString();
    String newUserName = GeneratorTestData.getRandomString();

    @Before
    @DisplayName("Создаём пользователя")
    public void createUser() throws InterruptedException {
        response = UserClient.create(userMail, userPassword, userName);
        response = UserClient.assert429Error(response);
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
