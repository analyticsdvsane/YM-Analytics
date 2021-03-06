package com.yuktamedia.analytics;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Options let you control behaviour for a specific analytics action, including setting a custom
 * timestamp and disabling integrations on demand.
 */
public class Options {

    /**
     * A special key, whose value which is respected for all integrations, a "default" value, unless
     * explicitly overridden. See the documentation for {@link #setIntegration(String, boolean)} on
     * how to use this key.
     */
    public static final String ALL_INTEGRATIONS_KEY = "All";

    private final Map<String, Object> integrations; // passed in by the user
    private final Map<String, Object> context;

    public Options() {
        integrations = new ConcurrentHashMap<>();
        context = new ConcurrentHashMap<>();
    }

    /**
     * Sets whether an action will be sent to the target integration.
     *
     * <p>By default, all integrations are sent a payload, and the value for the {@link
     * #ALL_INTEGRATIONS_KEY} is {@code true}. You can disable specific payloads.
     *
     * <p>Example: <code>options.setIntegration("Google Analytics", false).setIntegration("Countly",
     * false)</code> will send the event to ALL integrations, except Google Analytic and Countly.
     *
     * <p>If you want to enable only specific integrations, first override the defaults and then
     * enable specific integrations.
     *
     * <p>Example: <code>options.setIntegration(Options.ALL_INTEGRATIONS_KEY,
     * false).setIntegration("Countly", true).setIntegration("Google Analytics", true)</code> will
     * only send events to ONLY Countly and Google Analytics.
     *
     * @param integrationKey The integration key
     * @param enabled <code>true</code> for enabled, <code>false</code> for disabled
     * @return This options object for chaining
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Options setIntegration(String integrationKey, boolean enabled) {
        if (AnalyticsIntegration.SEGMENT_KEY.equals(integrationKey)) {
            throw new IllegalArgumentException("Segment integration cannot be enabled or disabled.");
        }
        integrations.put(integrationKey, enabled);
        return this;
    }

    /**
     * Sets whether an action will be sent to the target integration. Same as {@link
     * #setIntegration(String, boolean)} but type safe for bundled integrations.
     *
     * @param bundledIntegration The target integration
     * @param enabled <code>true</code> for enabled, <code>false</code> for disabled
     * @return This options object for chaining
     * @see #setIntegration(String, boolean)
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public Options setIntegration(Analytics.BundledIntegration bundledIntegration, boolean enabled) {
        setIntegration(bundledIntegration.key, enabled);
        return this;
    }

    /**
     * Attach some integration specific options for this call.
     *
     * @param integrationKey The target integration key
     * @param options A map of data that will be used by the integration
     * @return This options object for chaining
     */
    public Options setIntegrationOptions(String integrationKey, Map<String, Object> options) {
        integrations.put(integrationKey, options);
        return this;
    }

    /**
     * Attach some integration specific options for this call. Same as {@link
     * #setIntegrationOptions(String, Map)} but type safe for bundled integrations.
     *
     * @param bundledIntegration The target integration
     * @param options A map of data that will be used by the integration
     * @return This options object for chaining
     */
    public Options setIntegrationOptions(
            Analytics.BundledIntegration bundledIntegration, Map<String, Object> options) {
        integrations.put(bundledIntegration.key, options);
        return this;
    }

    /**
     * Attach some additional context information. Unlike with {@link
     * com.yuktamedia.analytics.Analytics#getAnalyticsContext()}, this only has effect for this call.
     *
     * @param key The key of the extra context data
     * @param value The value of the extra context data
     * @return This options object for chaining
     */
    public Options putContext(String key, Object value) {
        context.put(key, value);
        return this;
    }

    /** Returns a copy of settings for integrations. */
    public Map<String, Object> integrations() {
        return new LinkedHashMap<>(integrations);
    }

    /** Returns a copy of the context. */
    public Map<String, Object> context() {
        return new LinkedHashMap<>(context);
    }
}
