/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.client.java.impl;

import apache.rocketmq.v2.AckMessageRequest;
import apache.rocketmq.v2.AckMessageResponse;
import apache.rocketmq.v2.ChangeInvisibleDurationRequest;
import apache.rocketmq.v2.ChangeInvisibleDurationResponse;
import apache.rocketmq.v2.EndTransactionRequest;
import apache.rocketmq.v2.EndTransactionResponse;
import apache.rocketmq.v2.ForwardMessageToDeadLetterQueueRequest;
import apache.rocketmq.v2.ForwardMessageToDeadLetterQueueResponse;
import apache.rocketmq.v2.HeartbeatRequest;
import apache.rocketmq.v2.HeartbeatResponse;
import apache.rocketmq.v2.NotifyClientTerminationRequest;
import apache.rocketmq.v2.NotifyClientTerminationResponse;
import apache.rocketmq.v2.QueryAssignmentRequest;
import apache.rocketmq.v2.QueryAssignmentResponse;
import apache.rocketmq.v2.QueryRouteRequest;
import apache.rocketmq.v2.QueryRouteResponse;
import apache.rocketmq.v2.ReceiveMessageRequest;
import apache.rocketmq.v2.ReceiveMessageResponse;
import apache.rocketmq.v2.SendMessageRequest;
import apache.rocketmq.v2.SendMessageResponse;
import apache.rocketmq.v2.TelemetryCommand;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.Metadata;
import io.grpc.stub.StreamObserver;
import java.time.Duration;
import java.util.Iterator;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.java.route.Endpoints;

/**
 * Client manager supplies a series of unified APIs to execute remote procedure calls for each {@link Client}.
 *
 * <p>To manage lifecycle for client manager, {@link Client} must be registered before using client manager,
 * once {@link Client} is shut down, it must be unregistered by the client manager. The client manager holds the
 * connections and underlying threads, which are shared by all registered clients.
 */
public interface ClientManager {
    /**
     * Register client.
     *
     * @param client client.
     */
    void registerClient(Client client);

    /**
     * Unregister client.
     *
     * @param client client.
     */
    void unregisterClient(Client client);

    /**
     * Returns {@code true} if manager contains no {@link Client}.
     *
     * @return {@code true} if this map contains no {@link Client}.
     */
    boolean isEmpty();

    /**
     * Provide for the client to share the scheduler.
     *
     * @return shared scheduler.
     */
    ScheduledExecutorService getScheduler();

    /**
     * Query topic route asynchronously, the method ensures no throwable.
     *
     * @param endpoints requested endpoints.
     * @param metadata  gRPC request header metadata.
     * @param request   query route request.
     * @param duration  request max duration.
     * @return response future of the topic route.
     */
    ListenableFuture<QueryRouteResponse> queryRoute(Endpoints endpoints, Metadata metadata, QueryRouteRequest request,
        Duration duration);

    /**
     * Heart beat asynchronously, the method ensures no throwable.
     *
     * @param endpoints requested endpoints.
     * @param metadata  gRPC request header metadata.
     * @param request   heartbeat request.
     * @param duration  request max duration.
     * @return response future of heartbeat.
     */
    ListenableFuture<HeartbeatResponse> heartbeat(Endpoints endpoints, Metadata metadata, HeartbeatRequest request,
        Duration duration);

    /**
     * Send message asynchronously, the method ensures no throwable.
     *
     * @param endpoints requested endpoints.
     * @param metadata  gRPC request header metadata.
     * @param request   send message request.
     * @param duration  request max duration.
     * @return response future of the sending message.
     */
    ListenableFuture<SendMessageResponse> sendMessage(Endpoints endpoints, Metadata metadata,
        SendMessageRequest request, Duration duration);

    /**
     * Query assignment asynchronously, the method ensures no throwable.
     *
     * @param endpoints requested endpoints.
     * @param metadata  gRPC request header metadata.
     * @param request   query assignment request.
     * @param duration  request max duration.
     * @return response future of query assignment.
     */
    ListenableFuture<QueryAssignmentResponse> queryAssignment(Endpoints endpoints, Metadata metadata,
        QueryAssignmentRequest request, Duration duration);

    /**
     * Receiving messages asynchronously from the server, the method ensures no throwable.
     *
     * @param endpoints requested endpoints.
     * @param metadata  gRPC request header metadata.
     */
    ListenableFuture<Iterator<ReceiveMessageResponse>> receiveMessage(Endpoints endpoints, Metadata metadata,
        ReceiveMessageRequest request, Duration duration);

    /**
     * Ack message asynchronously after the success of consumption, the method ensures no throwable.
     *
     * @param endpoints requested endpoints.
     * @param metadata  gRPC request header metadata.
     * @param request   ack message request.
     * @param duration  request max duration.
     * @return response future of ack message.
     */
    ListenableFuture<AckMessageResponse> ackMessage(Endpoints endpoints, Metadata metadata, AckMessageRequest request,
        Duration duration);

    /**
     * Nack message asynchronously after the failure of consumption, the method ensures no throwable.
     *
     * @param endpoints requested endpoints.
     * @param metadata  gRPC request header metadata.
     * @param request   nack message request.
     * @param duration  request max duration.
     * @return response future of nack message.
     */
    ListenableFuture<ChangeInvisibleDurationResponse> changeInvisibleDuration(Endpoints endpoints, Metadata metadata,
        ChangeInvisibleDurationRequest request, Duration duration);

    /**
     * Send a message to the dead letter queue asynchronously, the method ensures no throwable.
     *
     * @param endpoints requested endpoints.
     * @param metadata  gRPC request header metadata.
     * @param request   request of sending a message to DLQ.
     * @param duration  request max duration.
     * @return response future of sending a message to DLQ.
     */
    ListenableFuture<ForwardMessageToDeadLetterQueueResponse> forwardMessageToDeadLetterQueue(
        Endpoints endpoints, Metadata metadata, ForwardMessageToDeadLetterQueueRequest request, Duration duration);

    /**
     * Submit transaction resolution asynchronously, the method ensures no throwable.
     *
     * @param endpoints requested endpoints.
     * @param metadata  gRPC request header metadata.
     * @param request   end transaction request.
     * @param duration  request max duration.
     * @return response future of submitting transaction resolution.
     */
    ListenableFuture<EndTransactionResponse> endTransaction(Endpoints endpoints, Metadata metadata,
        EndTransactionRequest request, Duration duration);

    /**
     * Asynchronously notify the server that client is terminated, the method ensures no throwable.
     *
     * @param endpoints request endpoints.
     * @param metadata  gRPC request header metadata.
     * @param request   notify client termination request.
     * @param duration  request max duration.
     * @return response future of notification of client termination.
     */
    @SuppressWarnings("UnusedReturnValue")
    ListenableFuture<NotifyClientTerminationResponse> notifyClientTermination(Endpoints endpoints, Metadata metadata,
        NotifyClientTerminationRequest request, Duration duration);

    StreamObserver<TelemetryCommand> telemetry(Endpoints endpoints, Metadata metadata,
        Duration duration, StreamObserver<TelemetryCommand> responseObserver) throws ClientException;
}
