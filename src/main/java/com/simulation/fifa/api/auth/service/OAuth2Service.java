package com.simulation.fifa.api.auth.service;

public interface OAuth2Service<T, T1> {
    T login(T1 params);

    void test();
}
