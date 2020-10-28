package com.stripe.sample;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import com.stripe.Stripe;
import io.github.cdimascio.dotenv.Dotenv;
import com.stripe.model.PaymentIntent;

public class RequiresCapture {
    public static void main(String[] args) {
        try {
            // List Payment Intents: https://stripe.com/docs/api/payment_intents/list
            // Auto pagination docs: https://stripe.com/docs/api/pagination/auto
            Dotenv dotenv = Dotenv.load();
            Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");

            long greaterThan = Instant.now().minus(4, ChronoUnit.DAYS).getEpochSecond();

            // You don't want to accidentally capture a payment that was going to get captured
            // successfully anyway. So we wait for things which are at least 1 hour old.
            long lessThan = Instant.now().minus(1, ChronoUnit.HOURS).getEpochSecond();

            System.out.println("greaterThan: " + greaterThan);
            System.out.println("lessThan: " + lessThan);
            Map<String, Object> created = new HashMap<>();
            created.put("gt", greaterThan);
            created.put("lt", lessThan);
            Map<String, Object> params = new HashMap<>();
            params.put("created", created);
            params.put("limit", 100);

            Iterable<PaymentIntent> paymentIntents = PaymentIntent.list(params).autoPagingIterable();
            for (PaymentIntent paymentIntent : paymentIntents) {
                if ("requires_capture".equals(paymentIntent.getStatus())) {
                    System.out.println(paymentIntent);
                    // Capture Payment Intent: https://stripe.com/docs/api/payment_intents/capture?lang=java
                    paymentIntent.capture();
                }
            }
        } catch (Exception exception) {
            System.out.println(exception);
        }
    }
}

// Create a Payment Intent with status: requires_capture
// curl https://api.stripe.com/v1/payment_intents \
// -u sk_test_51HhDUiCiYmz4DIqXzWIY7cSdgXxMSjdoLlUcFaIguYDx2Q9unccK0dPo8DtSHqrUasFdn5ZuvEjbqUKZXdL5lVXy00AfFgsg3Y: \
// -d amount=2000 \
// -d currency=usd \
// -d "payment_method_types[]"=card \
// -d payment_method=pm_card_visa \
// -d capture_method=manual \
// -d confirm=true
