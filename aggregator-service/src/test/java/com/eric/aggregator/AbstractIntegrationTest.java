package com.eric.aggregator;

import org.mockserver.client.MockServerClient;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@MockServerTest
@AutoConfigureWebTestClient
@SpringBootTest(properties = {
        "customer.service.url=http://localhost:{mockServerPort}",
        "stock.service.url=http://localhost:{mockServerPort}"
})
public abstract class AbstractIntegrationTest {

    // set by @MockServerTest automatically
    protected MockServerClient mockServerClient;

    @Autowired
    protected WebTestClient client;


}
