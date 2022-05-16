import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.api.settings.client.OrderClient;
import test.api.settings.client.UserClient;
import test.data.GeneratorTestData;

public class TestCreateOrder {
    String token;
    ValidatableResponse response;
    String userMail = GeneratorTestData.getRandomMail();
    String userPassword = GeneratorTestData.getRandomString();
    String userName = GeneratorTestData.getRandomString();
    String ingredient = GeneratorTestData.getIngredient();

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
    @DisplayName("Создание заказа с авторизацией - успешно")
    public void createOrderWhenAuthorizedSuccess() {
        ValidatableResponse orderResponse = OrderClient.createOrder(token, ingredient);
        UserClient.assertStatusAndBodyTrue(orderResponse, 200);
        OrderClient.assertOrderNumber(orderResponse);
    }

    @Test
    @DisplayName("Создание заказа без авторизации - неуспешно")
    public void createOrderWhenUnauthorizedFailed() {
        ValidatableResponse orderResponse = OrderClient.createOrder("", ingredient);
        UserClient.assertStatusAndBodyFalse(orderResponse, 401);
    }

    @Test
    @DisplayName("Создание заказа с ингредиентом - успешно")
    public void createOrderWithIngredientSuccess() {
        ValidatableResponse orderResponse = OrderClient.createOrder(token, ingredient);
        UserClient.assertStatusAndBodyTrue(orderResponse, 200);
        OrderClient.assertOrderNumber(orderResponse);
    }

    @Test
    @DisplayName("Создание заказа без ингредиента - неуспешно")
    public void createOrderWithoutIngredientFailed() {
        ValidatableResponse orderResponse = OrderClient.createOrderWithoutIngredient(token);
        UserClient.assertStatusAndBodyFalse(orderResponse, 400);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента - неуспешно")
    public void createOrderWithWrongIngredientHashFailed() {
        OrderClient.createOrder(token, "dsfddfg34").statusCode(500);
    }
}