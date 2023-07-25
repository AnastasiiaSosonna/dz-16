package apitests;

import apitests.BookingDates;
import apitests.CreateBookingBody;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.collections.CollectionUtils;

import java.util.List;

public class RestAssuredTests {

    public static String TOKEN_VALUE;
    public static final String TOKEN = "token";
    public int bookingList;

    @BeforeClass
    public void generateToken() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        JSONObject body = new JSONObject();
        body.put("username", "admin");
        body.put("password", "password123");

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body.toString())
                .post("/auth");
        response.prettyPrint();

        TOKEN_VALUE = response.then().extract().jsonPath().get(TOKEN);
    }

    @Test(description = "This test checks positive case of getting all booking ids")

    public void getAllIds() {
        Response response = RestAssured.get("/booking");
        response.then().statusCode(200);
    }

    public List<Integer> getAllIds1() {
        Response response = RestAssured.get("/booking");
        response.then().statusCode(200);
        List<Integer> bookingList = response.jsonPath().get("bookingid");
        return bookingList;
    }

    @Test(description = "This test checks positive case of booking creation")
    public void createBooking() {
        BookingDates bookingdates = BookingDates.builder()
                .checkin("2022-07-04")
                .checkout("2022-08-09")
                .build();

        CreateBookingBody body = new CreateBookingBody().builder()
                .firstname("Ted")
                .lastname("Black")
                .totalprice(200)
                .depositpaid(true)
                .additionalneeds("no needs")
                .bookingdates(bookingdates)
                .build();

        Response response = RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(body)
                .post("/booking");

        response.then().statusCode(200);
        response.prettyPrint();

    }

    @Test(description = "This test checks positive case of partial update")
    public void partialUpdateBooking() {

        List<Integer> bookingList = getAllIds1();

        if (CollectionUtils.hasElements(bookingList)) {
            int bookingIdToUpdate = bookingList.get(0); // Обираємо перший ідентифікатор зі списку для оновлення

            JSONObject body = new JSONObject();
            body.put("totalprice", 700);
            body.put("bookingdates.checkin", "2022-07-04");

            Response partialUpdateBooking = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .cookie(TOKEN, TOKEN_VALUE)
                    .body(body.toString())
                    .patch("/booking/{id}", bookingIdToUpdate);

            partialUpdateBooking.then().statusCode(200);
            partialUpdateBooking.prettyPrint();
        } else {
            // Обробка випадку, коли список ідентифікаторів бронювань порожній
            System.out.println("No bookings found for partial update.");
        }
    }

    @Test(description = "This test checks positive case of update")

    public void updateBooking() {

        List<Integer> bookingList = getAllIds1();

        if (bookingList.size() >= 2) {
            int bookingIdToUpdate = bookingList.get(1);

            JSONObject body = new JSONObject();
            body.put("firstname", "Alina");
            body.put("lastname", "Black");
            body.put("totalprice", 700);
            body.put("depositpaid", true);

            JSONObject Dates = new JSONObject();
            Dates.put("checkin", "2022-07-04");
            Dates.put("checkout", "2019-01-01");
            body.put("bookingdates", Dates);
            body.put("additionalneeds", "Breakfast");
        /*
        body.put("bookingdates.checkin","2022-07-04");
        body.put("bookingdates.checkout","2022-08-09");
        body.put("additionalneeds","Breakfast");

*/

            Response updateBooking = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .cookie(TOKEN, TOKEN_VALUE)
                    .body(body.toString())
                    .put("/booking/{id}", bookingIdToUpdate);

            updateBooking.then().statusCode(200);
            updateBooking.prettyPrint();
        } else {
            // Обробка випадку, коли список ідентифікаторів бронювань порожній
            System.out.println("No bookings found for partial update.");
        }
    }


    @Test(description = "This test checks positive case of delete booking")
    public void deleteBooking() {

        List<Integer> bookingList = getAllIds1();

        if (bookingList.size() >= 3) {
            int bookingIdToUpdate = bookingList.get(2);

            Response deleteBooking = RestAssured.given()
                    .header("Content-Type", "application/json")
                    .cookie(TOKEN, TOKEN_VALUE)
                    .delete("/booking/{id}", bookingIdToUpdate);

            deleteBooking.then().statusCode(201);
            deleteBooking.prettyPrint();
        } else {
            // Обробка випадку, коли список ідентифікаторів бронювань порожній
            System.out.println("No bookings found for partial update.");
        }
    }
}
