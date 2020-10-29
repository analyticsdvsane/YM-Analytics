package com.yuktamedia.analytics;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.yuktamedia.analytics.integrations.AdsPayload;
import com.yuktamedia.analytics.integrations.AliasPayload;
import com.yuktamedia.analytics.integrations.GroupPayload;
import com.yuktamedia.analytics.integrations.IdentifyPayload;
import com.yuktamedia.analytics.integrations.Integration;
import com.yuktamedia.analytics.integrations.ScreenPayload;
import com.yuktamedia.analytics.integrations.TrackPayload;
import com.yuktamedia.analytics.internal.Private;

import static android.content.ContentValues.TAG;
import static com.yuktamedia.analytics.Options.ALL_INTEGRATIONS_KEY;
import static com.yuktamedia.analytics.internal.Utils.isNullOrEmpty;

public abstract class IntegrationOperation {
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Private
    static boolean isIntegrationEnabled(ValueMap integrations, String key) {
        if (isNullOrEmpty(integrations)) {
            return true;
        }
        if (AnalyticsIntegration.SEGMENT_KEY.equals(key)) {
            return true; // Leave Segment integration enabled.
        }
        boolean enabled = true;
        if (integrations.containsKey(key)) {
            enabled = integrations.getBoolean(key, true);
        } else if (integrations.containsKey(ALL_INTEGRATIONS_KEY)) {
            enabled = integrations.getBoolean(ALL_INTEGRATIONS_KEY, true);
        }
        return enabled;
    }

    static IntegrationOperation onActivityCreated(final Activity activity, final Bundle bundle) {
        return new IntegrationOperation() {
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                integration.onActivityCreated(activity, bundle);
            }

            @Override
            public String toString() {
                return "Activity Created";
            }
        };
    }

    static IntegrationOperation onActivityStarted(final Activity activity) {
        return new IntegrationOperation() {
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                integration.onActivityStarted(activity);
            }

            @Override
            public String toString() {
                return "Activity Started";
            }
        };
    }

    static IntegrationOperation onActivityResumed(final Activity activity) {
        return new IntegrationOperation() {
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                integration.onActivityResumed(activity);
            }

            @Override
            public String toString() {
                return "Activity Resumed";
            }
        };
    }

    static IntegrationOperation onActivityPaused(final Activity activity) {
        return new IntegrationOperation() {
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                integration.onActivityPaused(activity);
            }

            @Override
            public String toString() {
                return "Activity Paused";
            }
        };
    }

    static IntegrationOperation onActivityStopped(final Activity activity) {
        return new IntegrationOperation() {
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                integration.onActivityStopped(activity);
            }

            @Override
            public String toString() {
                return "Activity Stopped";
            }
        };
    }

    static IntegrationOperation onActivitySaveInstanceState(
            final Activity activity, final Bundle bundle) {
        return new IntegrationOperation() {
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                integration.onActivitySaveInstanceState(activity, bundle);
            }

            @Override
            public String toString() {
                return "Activity Save Instance";
            }
        };
    }

    static IntegrationOperation onActivityDestroyed(final Activity activity) {
        return new IntegrationOperation() {
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                integration.onActivityDestroyed(activity);
            }

            @Override
            public String toString() {
                return "Activity Destroyed";
            }
        };
    }

    static IntegrationOperation identify(final IdentifyPayload identifyPayload) {
        return new IntegrationOperation() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                if (isIntegrationEnabled(identifyPayload.integrations(), key)) {
                    integration.identify(identifyPayload);
                }
            }

            @Override
            public String toString() {
                return identifyPayload.toString();
            }
        };
    }

    static IntegrationOperation group(final GroupPayload groupPayload) {
        return new IntegrationOperation() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                if (isIntegrationEnabled(groupPayload.integrations(), key)) {
                    integration.group(groupPayload);
                }
            }

            @Override
            public String toString() {
                return groupPayload.toString();
            }
        };
    }

    static IntegrationOperation track(final TrackPayload trackPayload) {
        return new IntegrationOperation() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                ValueMap integrationOptions = trackPayload.integrations();
//                Log.d(TAG, "integration: " + integration.getUnderlyingInstance().toString());
//                Log.d(TAG, "integrationOptions: " + integrationOptions.entrySet().toString());
                ValueMap trackingPlan = projectSettings.trackingPlan();
                if (isNullOrEmpty(trackingPlan)) {
                    // No tracking plan, use options provided.
                    if (isIntegrationEnabled(integrationOptions, key)) {
                        integration.track(trackPayload);
                    }
                    return;
                }

                String event = trackPayload.event();

                ValueMap eventPlan = trackingPlan.getValueMap(event);
                if (isNullOrEmpty(eventPlan)) {
                    if (!isNullOrEmpty(integrationOptions)) {
                        // No event plan, use options provided.
                        if (isIntegrationEnabled(integrationOptions, key)) {
                            integration.track(trackPayload);
                        }
                        return;
                    }

                    // Use schema defaults if no options are provided.
                    ValueMap defaultPlan = trackingPlan.getValueMap("__default");

                    // No defaults, send the event.
                    if (isNullOrEmpty(defaultPlan)) {
                        integration.track(trackPayload);
                        return;
                    }

                    // Send the event if new events are enabled or if this is the Segment integration.
                    boolean defaultEventsEnabled = defaultPlan.getBoolean("enabled", true);
                    if (defaultEventsEnabled || AnalyticsIntegration.SEGMENT_KEY.equals(key)) {
                        integration.track(trackPayload);
                    }

                    return;
                }

                // We have a tracking plan for the event.
                boolean isEnabled = eventPlan.getBoolean("enabled", true);
                if (!isEnabled) {
                    // If event is disabled in the tracking plan, send it only Segment.
                    if (AnalyticsIntegration.SEGMENT_KEY.equals(key)) {
                        integration.track(trackPayload);
                    }
                    return;
                }

                ValueMap integrations = new ValueMap();
                ValueMap eventIntegrations = eventPlan.getValueMap("integrations");
                if (!isNullOrEmpty(eventIntegrations)) {
                    integrations.putAll(eventIntegrations);
                }
                integrations.putAll(integrationOptions);
                if (isIntegrationEnabled(integrations, key)) {
                    integration.track(trackPayload);
                }
            }

            @Override
            public String toString() {
                return trackPayload.toString();
            }
        };
    }

    static IntegrationOperation trackJSON(final AdsPayload adsPayload) {
        return new IntegrationOperation() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                ValueMap integrationOptions = adsPayload.integrations();
//                Log.d(TAG, "integration: " + integration.getUnderlyingInstance().toString());
//                Log.d(TAG, "integrationOptions: " + integrationOptions.entrySet().toString());
                ValueMap trackingPlan = projectSettings.trackingPlan();
                if (isNullOrEmpty(trackingPlan)) {
                    // No tracking plan, use options provided.
                    if (isIntegrationEnabled(integrationOptions, key)) {
                        integration.trackJSON(adsPayload);
                    }
                    return;
                }

                String event = adsPayload.event();

                ValueMap eventPlan = trackingPlan.getValueMap(event);
                if (isNullOrEmpty(eventPlan)) {
                    if (!isNullOrEmpty(integrationOptions)) {
                        // No event plan, use options provided.
                        if (isIntegrationEnabled(integrationOptions, key)) {
                            integration.trackJSON(adsPayload);
                        }
                        return;
                    }

                    // Use schema defaults if no options are provided.
                    ValueMap defaultPlan = trackingPlan.getValueMap("__default");

                    // No defaults, send the event.
                    if (isNullOrEmpty(defaultPlan)) {
                        integration.trackJSON(adsPayload);
                        return;
                    }

                    // Send the event if new events are enabled or if this is the Segment integration.
                    boolean defaultEventsEnabled = defaultPlan.getBoolean("enabled", true);
                    if (defaultEventsEnabled || AnalyticsIntegration.SEGMENT_KEY.equals(key)) {
                        integration.trackJSON(adsPayload);
                    }

                    return;
                }

                // We have a tracking plan for the event.
                boolean isEnabled = eventPlan.getBoolean("enabled", true);
                if (!isEnabled) {
                    // If event is disabled in the tracking plan, send it only Segment.
                    if (AnalyticsIntegration.SEGMENT_KEY.equals(key)) {
                        integration.trackJSON(adsPayload);
                    }
                    return;
                }

                ValueMap integrations = new ValueMap();
                ValueMap eventIntegrations = eventPlan.getValueMap("integrations");
                if (!isNullOrEmpty(eventIntegrations)) {
                    integrations.putAll(eventIntegrations);
                }
                integrations.putAll(integrationOptions);
                if (isIntegrationEnabled(integrations, key)) {
                    integration.trackJSON(adsPayload);
                }
            }

            @Override
            public String toString() {
                return adsPayload.toString();
            }
        };
    }

    static IntegrationOperation screen(final ScreenPayload screenPayload) {
        return new IntegrationOperation() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                if (isIntegrationEnabled(screenPayload.integrations(), key)) {
                    integration.screen(screenPayload);
                }
            }

            @Override
            public String toString() {
                return screenPayload.toString();
            }
        };
    }

    static IntegrationOperation alias(final AliasPayload aliasPayload) {
        return new IntegrationOperation() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                if (isIntegrationEnabled(aliasPayload.integrations(), key)) {
                    integration.alias(aliasPayload);
                }
            }

            @Override
            public String toString() {
                return aliasPayload.toString();
            }
        };
    }

    static final IntegrationOperation FLUSH =
            new IntegrationOperation() {
                @Override
                void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                    integration.flush();
                }

                @Override
                public String toString() {
                    return "Flush";
                }
            };

    static final IntegrationOperation RESET =
            new IntegrationOperation() {
                @Override
                void run(String key, Integration<?> integration, ProjectSettings projectSettings) {
                    integration.reset();
                }

                @Override
                public String toString() {
                    return "Reset";
                }
            };

    private IntegrationOperation() {}

    /** Run this operation on the given integration. */
    abstract void run(String key, Integration<?> integration, ProjectSettings projectSettings);
}
