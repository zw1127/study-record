/*
 * Copyright (c) 2022 Fiberhome Technologies.
 *
 * No.6, Gaoxin 4th Road, Hongshan District.,Wuhan,P.R.China,
 * Fiberhome Telecommunication Technologies Co.,LTD
 *
 * All rights reserved.
 */
package cn.javastudy.springboot.mqtt.api;

import java.util.Comparator;

abstract class NullSafeComparator<T> implements Comparator<T> {
    NullSafeComparator() {
    }

    public int compare(T o1, T o2) {
        if (o1 == o2) {
            return 0;
        } else if (o1 == null) {
            return Integer.MAX_VALUE;
        } else {
            return o2 == null ? 1 : this.compareNonNull(o1, o2);
        }
    }

    protected abstract int compareNonNull(T var1, T var2);
}
