/*
 *  Copyright (c) 2017, salesforce.com, inc.
 *  All rights reserved.
 *  Licensed under the BSD 3-Clause license.
 *  For full license text, see LICENSE.txt file in the repo root  or https://opensource.org/licenses/BSD-3-Clause
 */

package com.salesforce.rxgrpc.stub;

import io.reactivex.FlowableOperator;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOperator;
import io.reactivex.disposables.Disposable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SubscribeOnlyOnceFlowableOperator throws an exception if a user attempts to subscribe more than once to a
 * {@link io.reactivex.Flowable}.
 */
public final class SubscribeOnlyOnceOperator {
    /**
     * SubscribeOnlyOnceFlowableOperator throws an exception if a user attempts to subscribe more than once to a
     * {@link io.reactivex.Flowable}.
     *
     * @param <T>
     */
    public static final class ForFlowable<T> implements FlowableOperator<T, T> {
        private AtomicBoolean subscribedOnce = new AtomicBoolean(false);

        @Override
        public Subscriber<? super T> apply(final Subscriber<? super T> observer) {
            return new Subscriber<T>() {
                @Override
                public void onSubscribe(Subscription subscription) {
                    if (subscribedOnce.getAndSet(true)) {
                        throw new NullPointerException("You cannot directly subscribe to a gRPC service multiple times " +
                                "concurrently. Use ForFlowable.share() instead.");
                    } else {
                        observer.onSubscribe(subscription);
                    }
                }

                @Override
                public void onNext(T t) {
                    observer.onNext(t);
                }

                @Override
                public void onError(Throwable throwable) {
                    observer.onError(throwable);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            };
        }
    }

    /**
     * SubscribeOnlyOnceSingleOperator throws an exception if a user attempts to subscribe more than once to a
     * {@link io.reactivex.Single}.
     *
     * @param <T>
     */
    public static final class ForSingle<T> implements SingleOperator<T, T> {
        private AtomicBoolean subscribedOnce = new AtomicBoolean(false);

        @Override
        public SingleObserver<? super T> apply(final SingleObserver<? super T> observer) {
            return new SingleObserver<T>() {
                @Override
                public void onSubscribe(Disposable d) {
                    if (subscribedOnce.getAndSet(true)) {
                        throw new NullPointerException("You cannot directly subscribe to a gRPC service multiple times " +
                                "concurrently. Use ForFlowable.share() instead.");
                    } else {
                        observer.onSubscribe(d);
                    }
                }

                @Override
                public void onSuccess(T t) {
                    observer.onSuccess(t);
                }

                @Override
                public void onError(Throwable e) {
                    observer.onError(e);
                }
            };
        }
    }
}
