package com.pocketmarket.mined.di;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

/**
 * Created by markanthonypanizales on 01/07/2017.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface SharedReference {
}
