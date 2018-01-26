/*
 * Copyright (c) 2018 Couchbase, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.couchbase.client.core.util;

import com.couchbase.client.core.msg.Request;
import io.reactivex.Single;
import io.reactivex.subjects.SingleSubject;

import java.util.NoSuchElementException;

/**
 * This class contains static helper functions when using the library with RxJava.
 *
 * @author Michael Nitschinger
 * @since 2.0.0
 */
public enum Rx {
  ;

  /**
   * Cache the no such element exception for single null creation.
   */
  private static final NoSuchElementException NO_SUCH_ELEMENT_EXCEPTION =
      new NoSuchElementException("A Single must never be completed with a null value!");

  static {
    NO_SUCH_ELEMENT_EXCEPTION.setStackTrace(new StackTraceElement[] {});
  }

  /**
   * Creates a RxJava {@link Single} from a {@link Request}.
   *
   * @param request the request to create the single from.
   * @param <T> the type of the response.
   * @return the wrapped {@link Single}.
   */
  public static <T> Single<T> single(final Request<T> request) {
    final SingleSubject<T> subject = SingleSubject.create();
    request.response().whenComplete((val, err) -> {
      if (err != null) {
        subject.onError(err);
      } else if (val != null) {
        subject.onSuccess(val);
      } else {
        subject.onError(NO_SUCH_ELEMENT_EXCEPTION);
      }
    });
    return subject;
  }

}
