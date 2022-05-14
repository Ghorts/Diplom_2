import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Test;
import test.api.settings.client.UserClient;

import java.util.concurrent.TimeUnit;

public class TestCreatingUserWithRemoval {
    ValidatableResponse response;
    String token;
    String userMail = RandomStringUtils.randomAlphabetic(10) + "@mail.ru";
    String userPassword = RandomStringUtils.randomAlphabetic(10);
    String userName = RandomStringUtils.randomAlphabetic(10);

    @After
    @DisplayName("Удаляем созданного пользователя")
    public void deleteUser() {
        token = new StringBuilder(response.extract().path("accessToken")).delete(0, 6).toString();
        ValidatableResponse deleteResponse = UserClient.delete(token);
        UserClient.assertStatusAndBodyTrue(deleteResponse, 202);
    }

    @Test
    @DisplayName("Создание пользователя с валидными данными успешно")
    public void createNewUserWithValidCredentialsSuccessful() {
        response = UserClient.create(userMail, userPassword, userName);
        UserClient.assertStatusAndBodyTrue(response, 200);
    }

    @Test
    @DisplayName("Cоздание пользователя, который уже зарегистрирован - неуспешно")
    public void createUserWithExistingCredentialsFalse() throws InterruptedException {
        response = UserClient.create(userMail, userPassword, userName);
        TimeUnit.SECONDS.sleep(1);
        ValidatableResponse secondResponse = UserClient.create(userMail, userPassword, userName);
        UserClient.assertStatusAndBodyFalse(secondResponse, 403);
    }

}