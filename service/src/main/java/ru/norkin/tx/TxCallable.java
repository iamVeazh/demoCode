package ru.norkin.tx;

import javax.persistence.EntityManager;

public interface TxCallable<T> {
    T call(EntityManager em) throws Exception;
}

