/*
 * Copyright 2015 GeekSaga.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.geeksaga.light.agent.core;

import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.JvmAttributeGaugeSet;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jvm.*;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * https://dropwizard.github.io/metrics/3.1.0/
 *
 * @author geeksaga
 */
public class MetricRegistryTest {
    private static final MetricRegistry METRIC_REGISTRY = new MetricRegistry();

    @Before
    public void setUp() {
        ConsoleReporter reporter = ConsoleReporter.forRegistry(METRIC_REGISTRY)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();
        reporter.start(1, TimeUnit.SECONDS);
    }

    @Test
    public void testBasic() throws InterruptedException {
        Meter requests = METRIC_REGISTRY.meter("requests");
        requests.mark();

        assertThat(requests.getCount(), is(1L));

        requests.mark();

        assertThat(requests.getCount(), is(2L));

        assertThat(requests.getOneMinuteRate(), is(0D));
        assertThat(requests.getFiveMinuteRate(), is(0D));
        assertThat(requests.getFifteenMinuteRate(), is(0D));

        Thread.sleep(5 * 1000);
    }

    @Test
    public void testJVM() throws InterruptedException {
        METRIC_REGISTRY.register("jvm.memory", new MemoryUsageGaugeSet());
        METRIC_REGISTRY.register("jvm.vm", new JvmAttributeGaugeSet());
        METRIC_REGISTRY.register("jvm.gc", new GarbageCollectorMetricSet());
        METRIC_REGISTRY.register("jvm.thread", new ThreadStatesGaugeSet());
        METRIC_REGISTRY.register("jvm.class", new ClassLoadingGaugeSet());
        METRIC_REGISTRY.register("jvm.fd", new FileDescriptorRatioGauge());

        Thread.sleep(5 * 1000);
    }
}
