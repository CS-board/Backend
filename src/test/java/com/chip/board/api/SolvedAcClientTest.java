package com.chip.board.api;

import com.chip.board.solvedac.application.port.cooldown.ExternalApiCooldownPort;
import com.chip.board.solvedac.infrastructure.api.SolvedAcClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

class SolvedAcClientTest {

    private StringRedisTemplate redis;
    private ValueOperations<String, String> valueOps;
    private ExternalApiCooldownPort externalApiCooldownPort;

    private SolvedAcClient solvedAcClient;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        redis = mock(StringRedisTemplate.class);
        valueOps = mock(ValueOperations.class);
        externalApiCooldownPort = mock(ExternalApiCooldownPort.class);

        when(redis.opsForValue()).thenReturn(valueOps);

        // gate 미활성 상태(즉시 호출 가능)로 설정
        when(valueOps.get("solvedac:gate:next_allowed_at_ms")).thenReturn("0");

        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();

        Clock clock = Clock.fixed(
                Instant.parse("2026-02-09T11:14:23Z"),
                ZoneId.of("Asia/Seoul")
        );

        solvedAcClient = new SolvedAcClient(
                builder,
                redis,
                "https://solved.ac",
                externalApiCooldownPort,
                clock
        );
    }

    @Test
    @DisplayName("searchSolvedProblemsSafe: 429 응답 시 cooldown 포트에 solved.ac로 1회 기록한다")
    void searchSolvedProblemsSafe_on429_recordsCooldown() {
        server.expect(requestTo("https://solved.ac/search/problem?query=solved_by:testUser&page=1&sort=id&direction=asc"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON));

        var result = solvedAcClient.searchSolvedProblemsSafe("testUser", 1);

        assertThat(result).isNull();

        ArgumentCaptor<String> apiKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(externalApiCooldownPort, times(1))
                .upsert429Cooldown(apiKeyCaptor.capture(), timeCaptor.capture());

        assertThat(apiKeyCaptor.getValue()).isEqualTo("solved.ac");
        assertThat(timeCaptor.getValue()).isNotNull();

        // 429 후 gate set 호출 확인
        verify(valueOps, atLeastOnce()).set(eq("solvedac:gate:next_allowed_at_ms"), anyString());

        server.verify();
    }

    @Test
    @DisplayName("userShowSafe: 429 응답 시 cooldown 포트에 solved.ac로 1회 기록한다")
    void userShowSafe_on429_recordsCooldown() {
        server.expect(requestTo("https://solved.ac/user/show?handle=testUser"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(TOO_MANY_REQUESTS)
                        .contentType(MediaType.APPLICATION_JSON));

        var result = solvedAcClient.userShowSafe("testUser");

        assertThat(result).isNull();

        ArgumentCaptor<String> apiKeyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<LocalDateTime> timeCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(externalApiCooldownPort, times(1))
                .upsert429Cooldown(apiKeyCaptor.capture(), timeCaptor.capture());

        assertThat(apiKeyCaptor.getValue()).isEqualTo("solved.ac");
        assertThat(timeCaptor.getValue()).isNotNull();

        verify(valueOps, atLeastOnce()).set(eq("solvedac:gate:next_allowed_at_ms"), anyString());

        server.verify();
    }

    @Test
    @DisplayName("200 응답 시에는 cooldown 포트를 호출하지 않는다")
    void on200_doesNotRecordCooldown() {
        server.expect(requestTo("https://solved.ac/user/show?handle=testUser"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess(
                        """
                        {"handle":"testUser","solvedCount":123}
                        """,
                        MediaType.APPLICATION_JSON
                ));

        solvedAcClient.userShowSafe("testUser");

        verify(externalApiCooldownPort, never()).upsert429Cooldown(anyString(), any(LocalDateTime.class));
        server.verify();
    }
}
