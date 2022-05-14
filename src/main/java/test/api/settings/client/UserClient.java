package test.api.settings.client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserClient extends ApiClient {

    @Step("Отправка запроса на создание пользователя")
    public static ValidatableResponse create(String email, String password, String name) {
        return given()
                .spec(getBaseSpec())
                .body("{\"email\": \"" + email + "\","
                        + "\"password\": \"" + password + "\","
                        + "\"name\": \"" + name + "\"}")
                .when()
                .post("/api/auth/register")
                .then();
    }

    @Step("Отправка запроса на удаление пользователя")
    public static ValidatableResponse delete(String auth) {
        return given()
                .spec(getAuthSpec(auth))
                .when()
                .delete("/api/auth/user")
                .then();
    }

    @Step("Отправка запроса на логин")
    public static ValidatableResponse login(String email, String password) {
        return given()
                .spec(getBaseSpec())
                .body("{\"email\":\"" + email + "\"," + "\"password\":\"" + password + "\"}")
                .when()
                .post("/api/auth/login")
                .then();
    }

    @Step("Отправка запроса на обновление всей информации о клиенте")
    public static ValidatableResponse updateAll(String token, String email, String password, String name) {
        return given()
                .spec(getAuthSpec(token))
                .body("{\"email\": \"" + email + "\","
                        + "\"password\": \"" + password + "\","
                        + "\"name\": \"" + name + "\"}")
                .when()
                .patch("/api/auth/user")
                .then();
    }

    @Step("Отправка запроса на обновление эмейла клиента")
    public static ValidatableResponse updateEmail(String token, String email) {
        return given()
                .spec(getAuthSpec(token))
                .body("{\"email\": \"" + email + "\"}")
                .when()
                .patch("/api/auth/user")
                .then();
    }

    @Step("Отправка запроса на обновление пароля клиента")
    public static ValidatableResponse updatePassword(String token, String password) {
        return given()
                .spec(getAuthSpec(token))
                .body("{\"password\": \"" + password + "\"}")
                .when()
                .patch("/api/auth/user")
                .then();
    }

    @Step("Отправка запроса на обновление имени клиента")
    public static ValidatableResponse updateName(String token, String name) {
        return given()
                .spec(getAuthSpec(token))
                .body("{\"name\": \"" + name + "\"}")
                .when()
                .patch("/api/auth/user")
                .then();
    }

    @Step("Сравниваем код ответа и успешный статус в теле")
    public static void assertStatusAndBodyTrue(ValidatableResponse response, int status) {
        response.statusCode(status).and().assertThat().body("success", equalTo(true));
    }

    @Step("Сравниваем код ответа и неуспешный статус в теле")
    public static void assertStatusAndBodyFalse(ValidatableResponse response, int status) {
        response.statusCode(status).and().assertThat().body("success", equalTo(false));
    }

    @Step("Проверка обновления данных пользователя в теле ответа")
    public static void assertNewData(ValidatableResponse response, String key, String value) {
        response.assertThat().body("user." + key, equalTo(value));
    }
}