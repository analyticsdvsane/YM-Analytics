package com.yuktamedia.analytics.integrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yuktamedia.analytics.Traits;
import com.yuktamedia.analytics.internal.Private;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.yuktamedia.analytics.internal.Utils.assertNotNull;
import static com.yuktamedia.analytics.internal.Utils.isNullOrEmpty;

public class IdentifyPayload extends BasePayload {
    static final String TRAITS_KEY = "traits";

    IdentifyPayload(
            @NonNull String messageId,
            @NonNull Date timestamp,
            @NonNull Map<String, Object> context,
            @NonNull Map<String, Object> integrations,
            @Nullable String userId,
            @NonNull String anonymousId,

            @NonNull Map<String, Object> traits) {
        super(Type.identify, messageId, timestamp, context, integrations, userId, anonymousId);
        put(TRAITS_KEY, traits);
    }

    /**
     * A dictionary of traits you know about a user, for example email or name. We have a collection
     * of special traits that we recognize with semantic meaning, which you should always use when
     * recording that information. You can also add any custom traits that are specific to your
     * project to the dictionary, like friendCount or subscriptionType.
     */
    @NonNull
    public Traits traits() {
        return getValueMap(TRAITS_KEY, Traits.class);
    }

    @Override
    public String toString() {
        return "IdentifyPayload{\"userId=\"" + userId() + "\"}";
    }

    @NonNull
    @Override
    public IdentifyPayload.Builder toBuilder() {
        return new Builder(this);
    }

    /** Fluent API for creating {@link IdentifyPayload} instances. */
    public static class Builder extends BasePayload.Builder<IdentifyPayload, Builder> {

        private Map<String, Object> traits;

        public Builder() {
            // Empty constructor.
        }

        @Private
        Builder(IdentifyPayload identify) {
            super(identify);
            traits = identify.traits();
        }

        @NonNull
        public Builder traits(@NonNull Map<String, ?> traits) {
            assertNotNull(traits, "traits");
            this.traits = Collections.unmodifiableMap(new LinkedHashMap<>(traits));
            return this;
        }

        @Override
        IdentifyPayload realBuild(
                @NonNull String messageId,
                @NonNull Date timestamp,
                @NonNull Map<String, Object> context,
                @NonNull Map<String, Object> integrations,
                String userId,
                @NonNull String anonymousId) {
            if (isNullOrEmpty(userId) && isNullOrEmpty(traits)) {
                throw new NullPointerException("either userId or traits are required");
            }

            return new IdentifyPayload(
                    messageId, timestamp, context, integrations, userId, anonymousId, traits);
        }

        @Override
        Builder self() {
            return this;
        }
    }
}
