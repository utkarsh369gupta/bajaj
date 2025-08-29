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
        // 🔑 Replace with your actual details
        String name  = "John Doe";
        String regNo = "REG12347"; // Change here for your registration number
        String email = "john@example.com"; // Change to your email

        System.out.println("🚀 Starting Bajaj Finserv Qualifier flow...");

        // 1) Generate webhook & token
        GenerateWebhookResponse resp = service.generateWebhook(
                new GenerateWebhookRequest(name, regNo, email)
        );
        System.out.println("✅ Webhook URL: " + resp.webhook());
        System.out.println("✅ AccessToken received.");

        // 2) Pick SQL query based on regNo
        String finalSql = service.pickFinalQueryFromResources(regNo);
        System.out.println("✅ Final SQL:\n" + finalSql);

        // 3) Submit solution
        service.submitFinalQuery(resp.webhook(), resp.accessToken(), finalSql);

        System.out.println("🎉 Flow completed successfully.");
    }
}
