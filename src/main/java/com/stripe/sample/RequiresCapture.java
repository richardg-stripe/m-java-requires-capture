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
            // Docs: https://stripe.com/docs/api/payment_intents/list
            // Auto pagination docs: https://stripe.com/docs/api/pagination/auto
            Dotenv dotenv = Dotenv.load();
            Stripe.apiKey = dotenv.get("STRIPE_SECRET_KEY");

            long unixTime = Instant.now().minus(4, ChronoUnit.DAYS).getEpochSecond();

            System.out.println("Unit Time: " + unixTime);
            Map<String, Object> created = new HashMap<>();
            created.put("gt", unixTime);
            Map<String, Object> params = new HashMap<>();
            params.put("created", created);
            params.put("limit", 100);

            Iterable<PaymentIntent> paymentIntents = PaymentIntent.list(params).autoPagingIterable();
            for (PaymentIntent paymentIntent : paymentIntents) {
                if ("requires_capture".equals(paymentIntent.getStatus())) {
                    System.out.println(paymentIntent);
                    // Capture the payment!
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
