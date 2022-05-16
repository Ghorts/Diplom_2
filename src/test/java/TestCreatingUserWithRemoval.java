import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Test;
import test.api.settings.client.UserClient;
import test.data.GeneratorTestData;

public class TestCreatingUserWithRemoval {
    ValidatableResponse response;
    String token;
    String userMail = GeneratorTestData.getRandomMail();
    String userPassword = GeneratorTestData.getRandomString();
    String userName = GeneratorTestData.getRandomString();

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
        response = UserClient.assert429Error(response);
        ValidatableResponse secondResponse = UserClient.create(userMail, userPassword, userName);
        UserClient.assertStatusAndBodyFalse(secondResponse, 403);
    }

}