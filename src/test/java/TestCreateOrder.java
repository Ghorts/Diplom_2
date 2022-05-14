import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import test.api.settings.client.OrderClient;
import test.api.settings.client.UserClient;

import java.util.concurrent.TimeUnit;

public class TestCreateOrder {
    String token;
    ValidatableResponse response;
    String userMail = RandomStringUtils.randomAlphabetic(10) + "@mail.ru";
    String userPassword = RandomStringUtils.randomAlphabetic(10);
    String userName = RandomStringUtils.randomAlphabetic(10);
    String ingredient = "61c0c5a71d1f82001bdaaa6d";

    @Before
    @DisplayName("Создаём пользователя")
    public void createUser() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(1000);
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
    public void createOrderWithoutIngredientFailed(){
        ValidatableResponse orderResponse = OrderClient.createOrderWithoutIngredient(token);
        UserClient.assertStatusAndBodyFalse(orderResponse, 400);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиента - неуспешно")
    public void createOrderWithWrongIngredientHashFailed() {
        OrderClient.createOrder(token, "dsfddfg34").statusCode(500);
    }
}