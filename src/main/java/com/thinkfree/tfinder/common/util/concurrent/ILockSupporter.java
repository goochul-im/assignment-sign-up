package com.thinkfree.tfinder.common.util.concurrent;

import java.util.function.Supplier;

public interface ILockSupporter {

    <T> T lockSupport(Supplier<T> task, String lock);

}
