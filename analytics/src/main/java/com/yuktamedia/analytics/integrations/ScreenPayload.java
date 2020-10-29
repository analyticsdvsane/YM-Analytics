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
import static com.yuktamedia.analytics.internal.Utils.isNullOrEmpty;

public class ScreenPayload extends BasePayload {
    static final String CATEGORY_KEY = "category";
    static final String NAME_KEY = "name";
    static final String PROPERTIES_KEY = "properties";

    @Private
    ScreenPayload(
            @NonNull String messageId,
            @NonNull Date timestamp,
            @NonNull Map<String, Object> context,
            @NonNull Map<String, Object> integrations,
            @Nullable String userId,
            @NonNull String anonymousId,
            @Nullable String name,
            @Nullable String category,
            @NonNull Map<String, Object> properties) {
        super(Type.screen, messageId, timestamp, context, integrations, userId, anonymousId);
        if (!isNullOrEmpty(name)) {
            put(NAME_KEY, name);
        }
        if (!isNullOrEmpty(category)) {
            put(CATEGORY_KEY, category);
        }
        put(PROPERTIES_KEY, properties);
    }

    /** The category of the page or screen. We recommend using title case, like "Docs". */
    @Nullable
    @Deprecated
    public String category() {
        return getString(CATEGORY_KEY);
    }

    /** The name of the page or screen. We recommend using title case, like "About". */
    @Nullable
    public String name() {
        return getString(NAME_KEY);
    }

    /** Either the name or category of the screen payload. */
    @NonNull
    public String event() {
        String name = name();
        if (!isNullOrEmpty(name)) {
            return name;
        }
        return category();
    }

    /** The page and screen methods also take a properties dictionary, just like track. */
    @NonNull
    public Properties properties() {
        return getValueMap(PROPERTIES_KEY, Properties.class);
    }

    @Override
    public String toString() {
        return "ScreenPayload{name=\"" + name() + ",category=\"" + category() + "\"}";
    }

    @NonNull
    @Override
    public ScreenPayload.Builder toBuilder() {
        return new Builder(this);
    }

    /** Fluent API for creating {@link ScreenPayload} instances. */
    public static class Builder extends BasePayload.Builder<ScreenPayload, Builder> {

        private String name;
        private String category;
        private Map<String, Object> properties;

        public Builder() {
            // Empty constructor.
        }

        @Private
        Builder(ScreenPayload screen) {
            super(screen);
            name = screen.name();
            properties = screen.properties();
        }

        @NonNull
        public Builder name(@Nullable String name) {
            this.name = name;
            return this;
        }

        @NonNull
        @Deprecated
        public Builder category(@Nullable String category) {
            this.category = category;
            return this;
        }

        @NonNull
        public Builder properties(@NonNull Map<String, ?> properties) {
            assertNotNull(properties, "properties");
            this.properties = Collections.unmodifiableMap(new LinkedHashMap<>(properties));
            return this;
        }

        @Override
        protected ScreenPayload realBuild(
                @NonNull String messageId,
                @NonNull Date timestamp,
                @NonNull Map<String, Object> context,
                @NonNull Map<String, Object> integrations,
                @Nullable String userId,
                @NonNull String anonymousId) {
            if (isNullOrEmpty(name) && isNullOrEmpty(category)) {
                throw new NullPointerException("either name or category is required");
            }

            Map<String, Object> properties = this.properties;
            if (isNullOrEmpty(properties)) {
                properties = Collections.emptyMap();
            }

            return new ScreenPayload(
                    messageId,
                    timestamp,
                    context,
                    integrations,
                    userId,
                    anonymousId,
                    name,
                    category,
                    properties);
        }

        @Override
        Builder self() {
            return this;
        }
    }
}
