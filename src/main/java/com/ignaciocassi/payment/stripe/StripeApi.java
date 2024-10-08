package com.ignaciocassi.payment.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.net.RequestOptions;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class StripeApi {

    public Charge charge(Map<String, Object> params, RequestOptions options) throws StripeException {
        return Charge.create(params, options);
    }

}
