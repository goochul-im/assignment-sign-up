package com.thinkfree.tfinder.common.concurrent;

import java.util.function.Supplier;

public interface ILockSupporter {

    public <T> T lockSupport(Supplier<T> task, String lock);

}
