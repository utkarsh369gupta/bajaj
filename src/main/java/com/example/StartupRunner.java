package com.example;

import com.example.dto.GenerateWebhookRequest;
import com.example.dto.GenerateWebhookResponse;
import com.example.service.QualifierService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements ApplicationRunner {

    private final QualifierService service;

    public StartupRunner(QualifierService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Replace with your real details
        String name  = "John Doe";
        String regNo = "REG12347";  // your regNo
        String email = "john@example.com"; // your email

        System.out.println("Starting Qualifier flow...");

        // 1) Generate webhook
        GenerateWebhookResponse resp = service.generateWebhook(
                new GenerateWebhookRequest(name, regNo, email)
        );
        System.out.println("Webhook URL = " + resp.webhook());
        System.out.println("AccessToken received.");

        // 2) Load SQL solution
        String finalSql = service.pickFinalQueryFromResources();
        System.out.println("Final SQL = " + finalSql);

        // 3) Submit solution
        service.submitFinalQuery(resp.webhook(), resp.accessToken(), finalSql);

        System.out.println("Flow completed ✅");
    }
}
