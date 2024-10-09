package com.jointech.sdk.jt707.base;

import java.io.Serializable;

/**
 * base enumeration
 * @author HyoJung
 */
public interface BaseEnum  <T> extends Serializable {
    T getValue();
}