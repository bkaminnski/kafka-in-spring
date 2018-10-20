# Kafka in Spring

This project presents how to use Kafka in Spring. Each case is covered by a separate integration test (see [integrationTests](https://github.com/bkaminnski/kafkainspring/tree/master/integrationTests) project).

## Assigned Consumer

Uses `assign()` to subscribe to individual partition. Does not require group id for Kafka consumer. Such consumer also does not need to care about rebalancing (e.g. new partition added to a topic). Declares error handler that handles exceptions thrown during consumption and simple retry policy (up to 3 retries) with exponential backoff policy.

### Stateless retry vs stateful retry

Each case is covered by two variants: stateless retry and stateful retry; you can read more about stateless and stateful retries [here](https://docs.spring.io/spring-kafka/docs/2.1.10.RELEASE/reference/html/_reference.html#stateful-retry):

- successful processing of consumed message (no exception)
- exception thrown on first try having max 3 tries policy with exponential back off policy - successful processing
- exception thrown on each of 3 retries - eventually handled by error handler (after 3rd retry)
- exception thrown on each of 3 retries programmed for 4 consecutive errors (not making to the 4th one) - eventually handled by error handler (after 3rd retry) 

**Watch out!** Interval between retries for stateless retry is different from interval in stateful retry. 
- Stateless retry is handled completely on Spring side (record is consumed only once; retries do not cause additional polling of the partition). This is why exceeding `max.poll.interval.ms` is at risk. However in this case, **interval between retries relies solely on retry policy**. In our case, with exponential back-off policy starting at 100 and limited to 400 milliseconds, these values are: 100, 200, 400, 400, ...
-  Stateful retry on the other hand relies on restoring offset and polling a message another time. This is why exceeding `max.poll.interval.ms` is *not* at risk anymore. There is however another component that adds to the time between retries: `fetch.max.wait.ms`. Following [official Kafka documentation](http://kafka.apache.org/090/documentation.html), this is 
    > The maximum amount of time the server will block before answering the fetch request if there isn't sufficient data to immediately satisfy the requirement given by fetch.min.bytes.

    In our case this is set to 300 milliseconds (in [AssignedConsumerStatefulRetryConfig class](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/assigned/src/main/java/com/hclc/kafkainspring/consumers/assign/AssignedConsumerStatefulRetryConfig.java)), and is represented by `additionalIntervalMillisForPolling` component in [AssignedConsumerTestScenario class](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/AssignedConsumerTestScenario.java). Therefore for stateful retry, interval between retries follows a shifted pattern: 400, 500, 700, 700, ...

### Pause consumption on error

Let's say we have a case that each and every message has to be handled successfully, and when it is not, messages consumption needs to be paused and manual intervention is required (in such case, a pager alert might be generated - this is however out of the scope of this exercise). This scenario is covered by [pauseConsumptionAfterHandlingFailureAndConsumeAfterResumed integration test](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/AssignedConsumerTestScenario.java#L106).

According to [this part of official Spring Kafka documentation](https://docs.spring.io/spring-kafka/docs/2.1.10.RELEASE/reference/html/_reference.html#pause-resume):

> Version 2.1.3 added pause() and resume() methods to listener containers. Previously, you could pause a consumer within a ConsumerAwareMessageListener and resume it by listening for ListenerContainerIdleEvent s, which provide access to the Consumer object. While you could pause a consumer in an idle container via an event listener, in some cases this was not thread-safe since there is no guarantee that the event listener is invoked on the consumer thread. **To safely pause/resume consumers, you should use the methods on the listener containers.**

## Subscribed Consumer

Uses `subscribe()` to subscribe to a topic. Requires group id for Kafka consumer.

Cases:

- successful processing of consumed message (no exception)

## Prosumer

`[Coming soon...]`

Consumes from one topic, produces to another.

## Prosumer synced with DB transaction

`[Coming soon...]`

Consumes from one topic, produces to another synchronizing with database transaction.
