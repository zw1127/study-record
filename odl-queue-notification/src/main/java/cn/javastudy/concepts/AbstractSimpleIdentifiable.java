/*
 * Copyright (c) 2019 PANTHEON.tech, s.r.o. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.javastudy.concepts;

import com.google.common.annotations.Beta;
import org.eclipse.jdt.annotation.NonNullByDefault;

@Beta
@NonNullByDefault
public abstract class AbstractSimpleIdentifiable<T> extends AbstractIdentifiable<T, T> {
    protected AbstractSimpleIdentifiable(final T identifier) {
        super(identifier);
    }
}
