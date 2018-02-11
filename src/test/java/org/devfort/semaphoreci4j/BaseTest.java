/*
 * Copyright (c) 2017 devfort
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.devfort.semaphoreci4j;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.devfort.semaphoreci4j.model.Model;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * @author sokolovic
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Model.class)
@PowerMockIgnore("javax.net.ssl.*")
public abstract class BaseTest {

    protected SemaphoreCI semaphore;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
        wireMockConfig().port(8089)
    );

    @Before
    public void setUp() {
        PowerMockito.mockStatic(Model.class);
        Mockito.when(Model.getRootApiUrl()).thenReturn("http://localhost:8089");

        try {
            semaphore = new SemaphoreCI("valid-token");
            assertNotNull(semaphore);
        } catch (IOException exception) {
            fail("Exception thrown on initialization.");
        }
    }

}
