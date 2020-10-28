package com.yuktamedia.analytics.integrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yuktamedia.analytics.Properties;
import com.yuktamedia.analytics.internal.Private;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.yuktamedia.analytics.internal.Utils.assertNotNull;
import static com.yuktamedia.analytics.internal.Utils.assertNotNullOrEmpty;
import static com.yuktamedia.analytics.internal.Utils.isNullOrEmpty;

public class AdsPayload extends BasePayload {
    static final String EVENT_KEY = "event";
    static final String PROPERTIES_KEY = "properties";

    @Private
    AdsPayload(
            @NonNull String messageId,
            @NonNull Date timestamp,
            @NonNull Map<String, Object> context,
            @NonNull Map<String, Object> integrations,
            @Nullable String userId,
            @NonNull String anonymousId,
            @NonNull String event,
            @NonNull Map<String, Object> properties) {
        super(Type.track, messageId, timestamp, context, integrations, userId, anonymousId);
        put(EVENT_KEY, event);
        put(PROPERTIES_KEY, properties);
    }

    /**
     * The name of the event. We recommend using title case and past tense for event names, like
     * "Signed Up".
     */
    @NonNull
    public String event() {
        return getString(EVENT_KEY);
    }

    /**
     * A dictionary of properties that give more information about the event. We have a collection of
     * special properties that we recognize with semantic meaning. You can also add your own custom
     * properties.
     */
    @NonNull
    public Properties properties() {
        return getValueMap(PROPERTIES_KEY, Properties.class);
    }

    @Override
    public String toString() {
        return "AdsPayload{ADevent=\"" + event() + "\"}";
    }

    @NonNull
    @Override
    public AdsPayload.Builder toBuilder() {
        return new Builder(this);
    }

    /** Fluent API for creating {@link TrackPayload} instances. */
    public static class Builder extends BasePayload.Builder<AdsPayload, Builder> {

        private String event;
        private Map<String, Object> properties;

        public Builder() {
            // Empty constructor.
        }

        @Private
        Builder(AdsPayload track) {
            super(track);
            event = track.event();
            properties = track.properties();
        }

        @NonNull
        public Builder event(@NonNull String event) {
            this.event = assertNotNullOrEmpty(event, "event");
            return this;
        }

        @NonNull
        public Builder properties(@NonNull Map<String, ?> properties) {
            assertNotNull(properties, "properties");
            this.properties = Collections.unmodifiableMap(new LinkedHashMap<>(properties));
            return this;
        }

        @Override
        protected AdsPayload realBuild(
                @NonNull String messageId,
                @NonNull Date timestamp,
                @NonNull Map<String, Object> context,
                @NonNull Map<String, Object> integrations,
                String userId,
                @NonNull String anonymousId) {
            assertNotNullOrEmpty(event, "event");

            Map<String, Object> properties = this.properties;
            if (isNullOrEmpty(properties)) {
                properties = Collections.emptyMap();
            }

            return new AdsPayload(
                    messageId, timestamp, context, integrations, userId, anonymousId, event, properties);
        }

        @Override
        Builder self() {
            return this;
        }
    }
}
