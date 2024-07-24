package com.eric.customerportfolio;

import com.eric.customerportfolio.domain.Ticker;
import com.eric.customerportfolio.domain.TradeAction;
import com.eric.customerportfolio.dto.StockTradeRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;

@SpringBootTest
@AutoConfigureWebTestClient
class CustomerServiceIntegrationTests {

	private static final Logger log = LoggerFactory.getLogger(CustomerServiceIntegrationTests.class);

	@Autowired
	private WebTestClient client;

	@Test
	public void customerInformationTest() {
		getCustomer(1, HttpStatus.OK)
				.jsonPath("$.id").isEqualTo(1)
				.jsonPath("$.name").isEqualTo("Sam")
				.jsonPath("$.balance").isEqualTo(10000)
				.jsonPath("$.holdings").isEmpty();
	}

	@Test
	public void customerINotFoundTest() {
		getCustomer(999, HttpStatus.NOT_FOUND)
				.jsonPath("$.detail").isEqualTo("Customer [id=999] not found");
	}

	@Test
	public void buyAndSellTest() {

		int expectedBalance = 10000;
		final Integer customerId = 1;

		// buy
		StockTradeRequest buyRequest = new StockTradeRequest(Ticker.AMAZON, 10, 10, TradeAction.BUY);
		expectedBalance -= buyRequest.totalPrice();
		postTrade(customerId, HttpStatus.OK, buyRequest)
				.jsonPath("$.customerId").isEqualTo(customerId)
				.jsonPath("$.ticker").isEqualTo(buyRequest.ticker().toString())
				.jsonPath("$.price").isEqualTo(buyRequest.price())
				.jsonPath("$.action").isEqualTo(buyRequest.action().toString())
				.jsonPath("$.quantity").isEqualTo(buyRequest.quantity())
				.jsonPath("$.totalPrice").isEqualTo(buyRequest.totalPrice())
				.jsonPath("$.balance").isEqualTo(expectedBalance);

		getCustomer(1, HttpStatus.OK)
				.jsonPath("$.id").isEqualTo(customerId)
				.jsonPath("$.name").isEqualTo("Sam")
				.jsonPath("$.balance").isEqualTo(expectedBalance)
				.jsonPath("$.holdings").isNotEmpty()
				.jsonPath("$.holdings.length()").isEqualTo(1)
				.jsonPath("$.holdings[0].ticker").isEqualTo(buyRequest.ticker().toString())
				.jsonPath("$.holdings[0].quantity").isEqualTo(buyRequest.quantity());

		// sell
		StockTradeRequest sellRequest = new StockTradeRequest(Ticker.AMAZON, 20, 5, TradeAction.SELL);
		expectedBalance += sellRequest.totalPrice();
		postTrade(customerId, HttpStatus.OK, sellRequest)
				.jsonPath("$.customerId").isEqualTo(customerId)
				.jsonPath("$.ticker").isEqualTo(sellRequest.ticker().toString())
				.jsonPath("$.price").isEqualTo(sellRequest.price())
				.jsonPath("$.action").isEqualTo(sellRequest.action().toString())
				.jsonPath("$.quantity").isEqualTo(sellRequest.quantity())
				.jsonPath("$.totalPrice").isEqualTo(sellRequest.totalPrice())
				.jsonPath("$.balance").isEqualTo(expectedBalance);

	}

	@Test
	public void insufficientBalanceTest() {

		// buy
		StockTradeRequest buyRequest = new StockTradeRequest(Ticker.AMAZON, 10, 10000, TradeAction.BUY);
		postTrade(1, HttpStatus.BAD_REQUEST, buyRequest)
				.jsonPath("$.detail").isEqualTo("Customer [id=1] does not have enough funds to complete the transaction");

	}

	@Test
	public void insufficientSharesTest() {

		// buy
		StockTradeRequest buyRequest = new StockTradeRequest(Ticker.AMAZON, 10, 10, TradeAction.BUY);
		postTrade(1, HttpStatus.OK, buyRequest)
				.jsonPath("$.balance").isEqualTo(10000 - buyRequest.totalPrice());

		// sell
		StockTradeRequest sellRequest = new StockTradeRequest(Ticker.AMAZON, 10, 20, TradeAction.SELL);
		postTrade(1, HttpStatus.BAD_REQUEST, sellRequest)
				.jsonPath("$.detail").isEqualTo("Customer [id=1] does not have enough shares to complete the transaction");

	}

	private WebTestClient.BodyContentSpec getCustomer(Integer customerId, HttpStatus expectedStatus) {
		return client.get()
				.uri("/customers/{id}", customerId)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody()
				.consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
	}

	private WebTestClient.BodyContentSpec postTrade(Integer customerId, HttpStatus expectedStatus, StockTradeRequest request) {
		return client.post()
				.uri("/customers/{id}", customerId)
				.bodyValue(request)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody()
				.consumeWith(e -> log.info("{}", new String(Objects.requireNonNull(e.getResponseBody()))));
	}

}
