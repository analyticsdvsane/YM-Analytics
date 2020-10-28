package com.yuktamedia.analytics.integrations;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yuktamedia.analytics.internal.Private;

import java.util.Date;
import java.util.Map;

import static com.yuktamedia.analytics.internal.Utils.assertNotNullOrEmpty;

public class AliasPayload extends BasePayload {

    static final String PREVIOUS_ID_KEY = "previousId";

    @Private
    AliasPayload(
            @NonNull String messageId,
            @NonNull Date timestamp,
            @NonNull Map<String, Object> context,
            @NonNull Map<String, Object> integrations,
            @Nullable String userId,
            @NonNull String anonymousId,
            @NonNull String previousId) {
        super(Type.alias, messageId, timestamp, context, integrations, userId, anonymousId);
        put(PREVIOUS_ID_KEY, previousId);
    }

    /**
     * The previous ID for the user that you want to alias from, that you previously called identify
     * with as their user ID, or the anonymous ID if you haven't identified the user yet.
     */
    public String previousId() {
        return getString(PREVIOUS_ID_KEY);
    }

    @Override
    public String toString() {
        return "AliasPayload{userId=\"" + userId() + ",previousId=\"" + previousId() + "\"}";
    }

    @NonNull
    @Override
    public AliasPayload.Builder toBuilder() {
        return new Builder(this);
    }

    /** Fluent API for creating {@link AliasPayload} instances. */
    public static final class Builder extends BasePayload.Builder<AliasPayload, Builder> {

        private String previousId;

        public Builder() {
            // Empty constructor.
        }

        @Private
        Builder(AliasPayload alias) {
            super(alias);
            this.previousId = alias.previousId();
        }

        @NonNull
        public Builder previousId(@NonNull String previousId) {
            this.previousId = assertNotNullOrEmpty(previousId, "previousId");
            return this;
        }

        @Override
        protected AliasPayload realBuild(
                @NonNull String messageId,
                @NonNull Date timestamp,
                @NonNull Map<String, Object> context,
                @NonNull Map<String, Object> integrations,
                @Nullable String userId,
                @NonNull String anonymousId) {
            assertNotNullOrEmpty(userId, "userId");
            assertNotNullOrEmpty(previousId, "previousId");

            return new AliasPayload(
                    messageId, timestamp, context, integrations, userId, anonymousId, previousId);
        }

        @Override
        Builder self() {
            return this;
        }
    }
}
