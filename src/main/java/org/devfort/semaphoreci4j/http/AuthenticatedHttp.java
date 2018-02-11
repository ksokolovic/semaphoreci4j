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

package org.devfort.semaphoreci4j.http;

import java.net.URI;

/**
 * @author sokolovic
 */
public interface AuthenticatedHttp {

    /**
     * Appends the given URL with authentication query, if it doesn't already exist.
     *
     * @param url       Original URL.
     * @param authToken Authentication token query parameter value.
     * @return Original URL with authentication query.
     */
    default String authenticate(final String url, final String authToken) {
        URI uri = URI.create(url);
        StringBuilder query = new StringBuilder(uri.getQuery() == null ? "" : uri.getQuery());

        if (query.toString().contains("auth_token")) {
            return url;
        }
        if (query.length() > 0) {
            query.append("&auth_token=");
        } else {
            query.append("?auth_token=");
        }
        query.append(authToken);

        return url + query.toString();
    }

}
