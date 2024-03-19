/*
 * Scake - A high available, scalable distributed file system.
 * Copyright (C) 2024 RollW
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package scake.common.rpc;

import io.grpc.Status;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author RollW
 */
public class StreamObserverWrapper<V> implements CancelableStreamObserver<V> {
    private volatile boolean close = false;
    private final StreamObserver<V> streamObserver;
    private final ServerCallStreamObserver<V> callStreamObserver;

    public StreamObserverWrapper(StreamObserver<V> streamObserver) {
        this.streamObserver = streamObserver;
        if (streamObserver instanceof ServerCallStreamObserver) {
            this.callStreamObserver = (ServerCallStreamObserver<V>) streamObserver;
        } else {
            this.callStreamObserver = null;
        }
    }

    private void close() {
        close = true;
    }

    @Override
    public void onNext(V value) {
        if (close) {
            return;
        }
        streamObserver.onNext(value);
    }

    @Override
    public void onError(Throwable t) {
        if (close) {
            return;
        }
        close();
        streamObserver.onError(t);
    }

    @Override
    public void onCompleted() {
        if (close) {
            return;
        }
        close();
        streamObserver.onCompleted();
    }

    public void setClose() {
        close = true;
    }

    @Override
    public void cancel() {
        onError(Status.CANCELLED.asRuntimeException());
    }

    @Override
    public boolean isReady() {
        if (callStreamObserver == null) {
            return true;
        }
        return callStreamObserver.isReady();
    }

    @Override
    public void setOnReadyHandler(Runnable runnable) {
        if (callStreamObserver == null) {
            return;
        }
        callStreamObserver.setOnReadyHandler(runnable);
    }

    public static final int WAIT_FOR_READY_TIMEOUT = 10000;

    public void waitForReady() throws InterruptedException {
        if (isClosed()) {
            return;
        }
        if (callStreamObserver == null) {
            return;
        }
        if (callStreamObserver.isReady()) {
            return;
        }
        try {
            CountDownLatch countDownLatch = new CountDownLatch(1);
            callStreamObserver.setOnReadyHandler(countDownLatch::countDown);
            boolean state = countDownLatch.await(500, TimeUnit.MILLISECONDS);
            if (state) {
                return;
            }
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception ignored) {
            // it may not allow setting the handler
        }

        // fallback to busy waiting
        int i = 0;
        while (!callStreamObserver.isReady()) {
            // wait for ready
            // TODO: is there a better way to do this?
            if (i++ > WAIT_FOR_READY_TIMEOUT) {
                // wait for max 10000 times
                return;
            }
        }
    }

    public void setOnCancelHandler(Runnable runnable) {
        if (callStreamObserver == null) {
            return;
        }
        callStreamObserver.setOnCancelHandler(runnable);
    }

    @Override
    public void disableAutoInboundFlowControl() {
        if (callStreamObserver == null) {
            return;
        }
        callStreamObserver.disableAutoInboundFlowControl();
    }

    @Override
    public void request(int numMessages) {
        if (callStreamObserver == null) {
            return;
        }
        callStreamObserver.request(numMessages);
    }

    public void setMessageCompression(boolean enable) {
        if (callStreamObserver == null) {
            return;
        }
        callStreamObserver.setMessageCompression(enable);
    }

    @Override
    public boolean isCancelled() {
        if (callStreamObserver == null) {
            return false;
        }
        return callStreamObserver.isCancelled();
    }


    @Override
    public boolean isClosed() {
        if (close) {
            return true;
        }
        return isCancelled();
    }

    @Override
    public boolean isOpen() {
        if (close) {
            return false;
        }
        return !isCancelled();
    }

    public StreamObserver<V> getRawStreamObserver() {
        return streamObserver;
    }

    public static <V> StreamObserverWrapper<V> wrap(
            StreamObserver<V> streamObserver) {
        if (streamObserver instanceof StreamObserverWrapper) {
            return (StreamObserverWrapper<V>) streamObserver;
        }
        return new StreamObserverWrapper<>(streamObserver);
    }

}
