package test.api.settings.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrderClient extends ApiClient {

    @Step("Отправка запроса на создание заказа")
    public static ValidatableResponse createOrder(String token, String ingredient) {
        return given()
                .spec(getAuthSpec(token))
                .body("{\"ingredients\": [\"" + ingredient + "\"]}")
                .when()
                .post("/api/orders")
                .then();
    }

    @Step("Отправка запроса на создание заказа без ингредиента")
    public static ValidatableResponse createOrderWithoutIngredient(String token) {
        return given()
                .spec(getAuthSpec(token))
                .body("{\"ingredients\": []}")
                .when()
                .post("/api/orders")
                .then();
    }

    @Step("Отправка запроса на получение заказов конкретного пользователя")
    public static ValidatableResponse getOrders(String token) {
        return given()
                .spec(getAuthSpec(token))
                .when()
                .get("/api/orders")
                .then();
    }

    @Step("Проверка создания номера заказа")
    public static void assertOrderNumber(ValidatableResponse response) {
        response.assertThat().body("order.number", notNullValue());
    }

    @Step("Проверка обновления данных пользователя в теле ответа")
    public static void assertGetOrders(ValidatableResponse response) {
        response.assertThat().body("orders", notNullValue());
    }
}