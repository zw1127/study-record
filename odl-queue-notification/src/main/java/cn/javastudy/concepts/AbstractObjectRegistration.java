/*
 * Copyright (c) 2013 Cisco Systems, Inc. and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package cn.javastudy.concepts;

import static java.util.Objects.requireNonNull;

import com.google.common.base.MoreObjects.ToStringHelper;
import org.eclipse.jdt.annotation.NonNull;

/**
 * Utility registration handle. It is a convenience for register-style method which can return an AutoCloseable realized
 * by a subclass of this class. Invoking the close() method triggers unregistration of the state the method installed.
 */
public abstract class AbstractObjectRegistration<T> extends AbstractRegistration implements ObjectRegistration<T> {
    private final T instance;

    protected AbstractObjectRegistration(final @NonNull T instance) {
        this.instance = requireNonNull(instance);
    }

    @Override
    public final T getInstance() {
        return instance;
    }

    @Override
    protected ToStringHelper addToStringAttributes(final ToStringHelper toStringHelper) {
        return super.addToStringAttributes(toStringHelper).add("instance", instance);
    }
}

