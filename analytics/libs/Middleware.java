package com.yuktamedia.analytics;

import com.yuktamedia.analytics.integrations.BasePayload;

public interface Middleware {
    /** Called for every message. This will be called on the same thread the request was made. */
    void intercept(Chain chain);

    interface Chain {

        BasePayload payload();

        void proceed(BasePayload payload);
    }
}
