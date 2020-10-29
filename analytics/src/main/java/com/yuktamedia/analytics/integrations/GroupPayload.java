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
import static com.yuktamedia.analytics.internal.Utils.assertNotNullOrEmpty;
import static com.yuktamedia.analytics.internal.Utils.isNullOrEmpty;

public class GroupPayload extends BasePayload {
    static final String GROUP_ID_KEY = "groupId";
    static final String TRAITS_KEY = "traits";

    @Private
    public GroupPayload(
            @NonNull String messageId,
            @NonNull Date timestamp,
            @NonNull Map<String, Object> context,
            @NonNull Map<String, Object> integrations,
            @Nullable String userId,
            @NonNull String anonymousId,
            @NonNull String groupId,
            @NonNull Map<String, Object> traits) {
        super(Type.group, messageId, timestamp, context, integrations, userId, anonymousId);
        put(GROUP_ID_KEY, groupId);
        put(TRAITS_KEY, traits);
    }

    /**
     * A unique identifier that refers to the group in your database. For example, if your product
     * groups people by "organization" you would use the organization's ID in your database as the
     * group ID.
     */
    @NonNull
    public String groupId() {
        return getString(GROUP_ID_KEY);
    }

    /** The group method also takes a traits dictionary, just like identify. */
    @NonNull
    public Traits traits() {
        return getValueMap(TRAITS_KEY, Traits.class);
    }

    @Override
    public String toString() {
        return "GroupPayload{groupId=\"" + groupId() + "\"}";
    }

    @NonNull
    @Override
    public GroupPayload.Builder toBuilder() {
        return new Builder(this);
    }

    /** Fluent API for creating {@link GroupPayload} instances. */
    public static class Builder extends BasePayload.Builder<GroupPayload, Builder> {

        private String groupId;
        private Map<String, Object> traits;

        public Builder() {
            // Empty constructor.
        }

        @Private
        Builder(GroupPayload group) {
            super(group);
            groupId = group.groupId();
            traits = group.traits();
        }

        @NonNull
        public Builder groupId(@NonNull String groupId) {
            this.groupId = assertNotNullOrEmpty(groupId, "groupId");
            return this;
        }

        @NonNull
        public Builder traits(@NonNull Map<String, ?> traits) {
            assertNotNull(traits, "traits");
            this.traits = Collections.unmodifiableMap(new LinkedHashMap<>(traits));
            return this;
        }

        @Override
        protected GroupPayload realBuild(
                @NonNull String messageId,
                @NonNull Date timestamp,
                @NonNull Map<String, Object> context,
                @NonNull Map<String, Object> integrations,
                @Nullable String userId,
                @NonNull String anonymousId) {
            assertNotNullOrEmpty(groupId, "groupId");

            Map<String, Object> traits = this.traits;
            if (isNullOrEmpty(traits)) {
                traits = Collections.emptyMap();
            }

            return new GroupPayload(
                    messageId, timestamp, context, integrations, userId, anonymousId, groupId, traits);
        }

        @Override
        Builder self() {
            return this;
        }
    }
}
