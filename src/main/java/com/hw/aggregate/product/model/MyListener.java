package com.hw.aggregate.product.model;

import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
@Slf4j
public class MyListener {
    @PrePersist
    void onPrePersist(Object o) {
      log.debug("onPrePersist {}",o);
    }

    @PostPersist
    void onPostPersist(Object o) {
        log.debug("onPostPersist {}",o);
    }

    @PostLoad
    void onPostLoad(Object o) {
        log.debug("onPostLoad {}",o);
    }

    @PreUpdate
    void onPreUpdate(Object o) {
        log.debug("onPreUpdate {}",o);
    }

    @PostUpdate
    void onPostUpdate(Object o) {
        log.debug("onPostUpdate {}",o);
    }

    @PreRemove
    void onPreRemove(Object o) {
        log.debug("onPreRemove {}",o);
    }

    @PostRemove
    void onPostRemove(Object o) {
        log.debug("onPostRemove {}",o);
    }
}
