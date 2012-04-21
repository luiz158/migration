/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.open18.extension;

import javax.enterprise.inject.spi.BeanManager;

/**
 * Simple holder for the {@link javax.enterprise.inject.spi.BeanManager} so it can be used without needing injection.
 */
public class BeanManagerProvider {
    private static BeanManager bm;

    public static BeanManager getBm() {
        if (BeanManagerProvider.bm == null) {
            throw new IllegalStateException("No BeanManager is set");
        }

        return bm;
    }

    protected static void setBm(BeanManager bm) {
        BeanManagerProvider.bm = bm;
    }
}
