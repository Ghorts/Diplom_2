import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.api.settings.client.OrderClient;
import test.api.settings.client.UserClient;
import test.data.GeneratorTestData;

public class TestGetOrders {
    String token;
    ValidatableResponse response;
    String userMail = GeneratorTestData.getRandomMail();
    String userPassword = GeneratorTestData.getRandomString();
    String userName = GeneratorTestData.getRandomString();
    String ingredient = GeneratorTestData.getIngredient();

    @Before
    @DisplayName("Создаём пользователя и заказы")
    public void createUser() throws InterruptedException {
        response = UserClient.create(userMail, userPassword, userName);
        response = UserClient.assert429Error(response);
        token = new StringBuilder(response.extract().path("accessToken")).delete(0, 6).toString();
        OrderClient.createOrder(token, ingredient).statusCode(200);
    }

    @After
    @DisplayName("Удаляем созданного пользователя")
    public void deleteUser() {
        ValidatableResponse deleteResponse = UserClient.delete(token);
        UserClient.assertStatusAndBodyTrue(deleteResponse, 202);
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя - успешно")
    public void getOrdersWhenAuthorizedSuccess() {
        ValidatableResponse getResponse = OrderClient.getOrders(token);
        UserClient.assertStatusAndBodyTrue(getResponse, 200);
        OrderClient.assertGetOrders(getResponse);
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя - неуспешно")
    public void getOrdersWhenUnauthorizedFailed() {
        ValidatableResponse getResponse = OrderClient.getOrders("");
        UserClient.assertStatusAndBodyFalse(getResponse, 401);
    }
}