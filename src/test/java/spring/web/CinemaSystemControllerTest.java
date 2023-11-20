package spring.web;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import io.restassured.response.Response;
import model.Cinema;
import spring.main.Main;

@SpringBootTest(classes = Main.class, webEnvironment = WebEnvironment.DEFINED_PORT)
// define a test profile to create the in memory database and sample data
// see spring.main.AppConfiguration and spring.main.PersistenceConfiguration
// classes
@ActiveProfiles(value = "test")
public class CinemaSystemControllerTest {

	private static final String JSON_ROOT = "$";
	private static final String MOVIE_NAME_KEY = "name";
	private static final String MOVIE_ACTORS_KEY = "actors";
	private static final String MOVIE_RATING_TOTAL_VOTES_KEY = "ratingTotalVotes";
	private static final String MOVIE_RATING_VALUE_KEY = "ratingValue";
	private static final String MOVIE_RELEASE_DATE_KEY = "releaseDate";
	private static final String MOVIE_DIRECTORS_KEY = "directorNames";
	private static final String MOVIE_DURATION_KEY = "duration";
	private static final String MOVIE_PLOT_KEY = "plot";
	private static final String MOVIE_GENRES_KEY = "genres";
	private static final String SHOW_MOVIE_NAME_KEY = "movieName";
	private static final String ROCK_IN_THE_SCHOOL_MOVIE_NAME = "Rock in the School";
	private static final String RUNNING_FAR_AWAY_MOVIE_NAME = "Running far Away";
	private static final String SMALL_FISH_MOVIE_NAME = "Small Fish";
	private static final String CRASH_TEA_MOVIE_NAME = "Crash Tea";
	private static final String PASSWORD_JOSE = "123456789012";
	private static final String USERNAME_JOSE = "jsimini";
	private static final String ERROR_MESSAGE_KEY = "message";
	private static final String TOKEN_COOKIE_NAME = "token";
	private static final String JSON_CONTENT_TYPE = "application/json";
	private static String URL = "http://localhost:8080";

	@Test
	public void loginOk() throws JSONException {
		var response = loginAsJosePost();

		response.then().body("fullname", is("Josefina Simini"))
				.body("username", is(USERNAME_JOSE))
				.body("email", is("jsimini@mymovies.com"))
				.body("points", equalTo(0))
				.cookie(TOKEN_COOKIE_NAME, containsString("v2.local"));
	}

	private Response loginAsJosePost() {
		JSONObject loginRequestBody = new JSONObject();
		try {
			loginRequestBody.put("username", USERNAME_JOSE);
			loginRequestBody.put("password", PASSWORD_JOSE);
		} catch (JSONException e) {
			throw new RuntimeException(e);
		}

		var response = given().contentType(JSON_CONTENT_TYPE)
				.body(loginRequestBody.toString())
				.post(URL + "/login");
		return response;
	}

	@Test
	public void loginFail() throws JSONException {
		JSONObject loginRequestBody = new JSONObject();
		loginRequestBody.put("username", USERNAME_JOSE);
		loginRequestBody.put("password", "44446789012");

		var response = given().contentType(JSON_CONTENT_TYPE)
				.body(loginRequestBody.toString())
				.post(URL + "/login");

		response.then().body(ERROR_MESSAGE_KEY,
				is(Cinema.USER_OR_PASSWORD_ERROR));
		assertFalse(response.cookies().containsKey(TOKEN_COOKIE_NAME));

	}

	@Test
	public void playingNowShowsOk() {
		var response = get(URL + "/shows");

		response.then().body(SHOW_MOVIE_NAME_KEY,
				hasItems(CRASH_TEA_MOVIE_NAME, SMALL_FISH_MOVIE_NAME,
						ROCK_IN_THE_SCHOOL_MOVIE_NAME,
						RUNNING_FAR_AWAY_MOVIE_NAME));

		response.then().body(MOVIE_DURATION_KEY,
				hasItems("1hr 49mins", "2hrs 05mins", "1hr 45mins",
						"1hr 45mins"));
	}

	@Test
	public void moviesOk() {
		var response = get(URL + "/movies");

		response.then().body(MOVIE_NAME_KEY,
				hasItems(CRASH_TEA_MOVIE_NAME,
						ROCK_IN_THE_SCHOOL_MOVIE_NAME));

		assertOnMovies(response);
	}

	@Test
	public void moviesSortedRateOk() {
		var response = get(URL + "/movies/sorted/rate");
		response.then().body("[0].name", is(ROCK_IN_THE_SCHOOL_MOVIE_NAME));
		response.then().body("[1].name", is(SMALL_FISH_MOVIE_NAME));
		assertOnMovies(response);
	}

	@Test
	public void moviesSortedReleaseDateOk() {
		var response = get(URL + "/movies/sorted/releasedate");
		response.then().body("[0].name", is(RUNNING_FAR_AWAY_MOVIE_NAME));
		response.then().body("[1].name", is(ROCK_IN_THE_SCHOOL_MOVIE_NAME));
		assertOnMovies(response);
	}
	private void assertOnMovies(Response response) {
		response.then().body(JSON_ROOT, hasItem(hasKey(MOVIE_GENRES_KEY)));
		response.then().body(JSON_ROOT, hasItem(hasKey(MOVIE_PLOT_KEY)));
		response.then().body(JSON_ROOT, hasItem(hasKey(MOVIE_DURATION_KEY)));
		response.then().body(JSON_ROOT, hasItem(hasKey(MOVIE_DIRECTORS_KEY)));
		response.then().body(JSON_ROOT,
				hasItem(hasKey(MOVIE_RELEASE_DATE_KEY)));
		response.then().body(JSON_ROOT,
				hasItem(hasKey(MOVIE_RATING_VALUE_KEY)));
		response.then().body(JSON_ROOT,
				hasItem(hasKey(MOVIE_RATING_TOTAL_VOTES_KEY)));
		response.then().body(JSON_ROOT, hasItem(hasKey(MOVIE_ACTORS_KEY)));
	}

	@Test
	public void moviesOneOk() {
		var response = get(URL + "/movies/1");

		response.then().body(MOVIE_NAME_KEY,
				is(oneOf(SMALL_FISH_MOVIE_NAME, ROCK_IN_THE_SCHOOL_MOVIE_NAME,
						RUNNING_FAR_AWAY_MOVIE_NAME, CRASH_TEA_MOVIE_NAME)));

		response.then().body(JSON_ROOT, hasKey(MOVIE_GENRES_KEY));
		response.then().body(JSON_ROOT, hasKey(MOVIE_PLOT_KEY));
		response.then().body(JSON_ROOT, hasKey(MOVIE_DURATION_KEY));
		response.then().body(JSON_ROOT, hasKey(MOVIE_DIRECTORS_KEY));
		response.then().body(JSON_ROOT, hasKey(MOVIE_RELEASE_DATE_KEY));
		response.then().body(JSON_ROOT, hasKey(MOVIE_RATING_VALUE_KEY));
		response.then().body(JSON_ROOT, hasKey(MOVIE_RATING_TOTAL_VOTES_KEY));
		response.then().body(JSON_ROOT, hasKey(MOVIE_ACTORS_KEY));
	}

	@Test
	public void moviesOneRateOk() {
		var response = get(URL + "/movies/1/rate");

		response.then().body(JSON_ROOT,
				hasItem(allOf(both(hasEntry("username", "lucia")).and(
						(hasEntry("comment",
								"I really enjoy the movie")))
						.and(hasEntry("rateValue", 4)))));

		response.then().body(JSON_ROOT,
				hasItem(allOf(both(hasEntry("username", "nico")).and(
						(hasEntry("comment",
								"Fantastic! The actors, the music, everything is fantastic!")))
						.and(hasEntry("rateValue", 5)))));
	}

	@Test
	public void showOneOk() {
		var response = get(URL + "/shows/1");
		// To avoid fragile tests, I use oneOf, as the movie assigned to show 1
		// might change
		response.then().body("info." + SHOW_MOVIE_NAME_KEY,
				is(oneOf(SMALL_FISH_MOVIE_NAME, ROCK_IN_THE_SCHOOL_MOVIE_NAME,
						RUNNING_FAR_AWAY_MOVIE_NAME, CRASH_TEA_MOVIE_NAME)));
		response.then().body("info.showId", is(1));
		response.then().body(JSON_ROOT, hasKey("currentSeats"));
		response.then().body("info", hasKey("movieDuration"));
	}

	@Test
	public void rateMovieOk() throws JSONException {
		var loginResponse = loginAsJosePost();
		var token = loginResponse.getCookie(TOKEN_COOKIE_NAME);

		JSONObject rateRequestBody = new JSONObject();
		rateRequestBody.put("rateValue", 4);
		rateRequestBody.put("comment", "a comment...");

		var response = given().contentType(JSON_CONTENT_TYPE)
				.cookie(TOKEN_COOKIE_NAME, token)
				.body(rateRequestBody.toString())
				.post(URL + "/movies/1/rate");

		response.then().body("username", is(USERNAME_JOSE))
				.body("rateValue", is(4))
				.body("comment", is("a comment..."));
	}

	@Test
	public void rateMovieInvalidCookie() throws JSONException {
		JSONObject rateRequestBody = new JSONObject();
		rateRequestBody.put("rateValue", 4);
		rateRequestBody.put("comment", "a comment...");

		var response = given().contentType(JSON_CONTENT_TYPE)
				.cookie(TOKEN_COOKIE_NAME, "non sense cookie value")
				.body(rateRequestBody.toString())
				.post(URL + "/movies/1/rate");

		response.then().body("message",
				is(CinemaSystemController.AUTHENTICATION_REQUIRED));
	}
}