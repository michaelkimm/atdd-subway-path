package nextstep.subway.acceptance;

import static nextstep.subway.acceptance.LineSteps.*;
import static nextstep.subway.acceptance.PathSteps.*;
import static nextstep.subway.acceptance.SectionAcceptanceTest.*;
import static nextstep.subway.acceptance.StationSteps.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class PathAcceptanceTest extends AcceptanceTest {
	private Long 교대역;
	private Long 강남역;
	private Long 양재역;
	private Long 남부터미널역;
	private Long 이호선;
	private Long 신분당선;
	private Long 삼호선;

	/**
	 * 교대역    --- *2호선* ---   강남역
	 * |                        |
	 * *3호선*                   *신분당선*
	 * |                        |
	 * 남부터미널역  --- *3호선* ---   양재역
	 */
	@BeforeEach
	public void setUp() {
		super.setUp();

		교대역 = 지하철역_생성_요청("교대역").jsonPath().getLong("id");
		강남역 = 지하철역_생성_요청("강남역").jsonPath().getLong("id");
		양재역 = 지하철역_생성_요청("양재역").jsonPath().getLong("id");
		남부터미널역 = 지하철역_생성_요청("남부터미널역").jsonPath().getLong("id");

		이호선 = 지하철_노선_생성_요청("2호선", "green", 교대역, 강남역, 10);
		신분당선 = 지하철_노선_생성_요청("신분당선", "red", 강남역, 양재역, 10);
		삼호선 = 지하철_노선_생성_요청("3호선", "orange", 교대역, 남부터미널역, 2);

		지하철_노선에_지하철_구간_생성_요청(삼호선, createSectionCreateParams(남부터미널역, 양재역, 3));
	}

	/**
	 * Given 지하철 노선을 추가하고
	 * When 출발역과 도착역으로 경로를 조회하면
	 * Then 경로에 있는 역 목록과 거리를 응답한다.
	 */
	@DisplayName("경로 조회 성공")
	@Test
	void getPaths_성공() {
		// given 지하철 노선 생성됨

		// when 교대역-양재역 경로 조회
		ExtractableResponse<Response> response = 경로_조회_요청(교대역, 양재역);

		// then 교대역-남부터미널역-양재역
		assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.jsonPath().getList("stations.id", Long.class)).containsExactly(교대역, 남부터미널역, 양재역);
		assertThat(response.jsonPath().getInt("distance")).isEqualTo(5);
	}

	/**
	 * Given 지하철 노선을 추가하고
	 * When 출발역과 도착역을 동일한 역으로 경로를 조회하면
	 * Then 경로조회에 실패한다.
	 */
	@DisplayName("출발역과 도착역이 같은 경우")
	@Test
	void getPaths_실패1() {
		// given 지하철 노선 생성됨

		// when 교대역-양재역 경로 조회
		ExtractableResponse<Response> response = 경로_조회_요청(교대역, 교대역);

		// then 실패
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * Given 서로 연결되어있지 않는 지하철 노선을 추가하고
	 * When 연결되지 않은 역끼리 경로를 조회하면
	 * Then 경로조회에 실패한다.
	 */
	@DisplayName("출발역과 도착역이 연결이 되어 있지 않은 경우")
	@Test
	void getPaths_실패2() {
		// given 연결되지 않은 지하철 노선 추가 생성
		Long 신도림역 = 지하철역_생성_요청("신도림역").jsonPath().getLong("id");
		Long 영등포역 = 지하철역_생성_요청("영등포역").jsonPath().getLong("id");
		지하철_노선_생성_요청("1호선", "blue", 신도림역, 영등포역, 10);

		// when 신도림역-교대역 경로 조회
		ExtractableResponse<Response> response = 경로_조회_요청(신도림역, 교대역);

		// then 실패
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}

	/**
	 * Given 지하철 노선을 추가하고
	 * When 존재하지 않는 역의 경로를 조회하면
	 * Then 경로조회에 실패한다.
	 */
	@DisplayName("존재하지 않는 출발역이나 도착역을 조회 할 경우")
	@Test
	void getPaths_실패3() {
		// given 지하철 노선 생성됨

		// when 존재하지 않는 역으로 경로 조회
		ExtractableResponse<Response> response = 경로_조회_요청(교대역, 999L);

		// then 실패
		assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
	}
}
