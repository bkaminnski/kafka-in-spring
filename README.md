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

### Simple @KafkaListener

Presents a simple consumer registered with `@KafkaListener` consuming messages from `subscribedConsumerSimpleTopic`, covered by [this test](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/SubscribedConsumerTestScenario.java#L30).

### Rebalancing

- A topic (`subscribedConsumerRebalancedTopic`) with 2 partitions and 2 consumers (consumer 0 for partition 0, consumer 1 for partition 1).
- Send a message (A) to partition 0, simulate long processing time.
- Expect rebalancing: partition 0 reassigned from consumer 0 to consumer 1.
- Problematic message (A) processed by consumer 1 (this time with no artificial delay).
- Send another message (B) to partition 0 (with no artificial delay).
- Expect to process message (B) by consumer 1.
- Expect to eventually process message (A) by consumer 0 - observe side effect and `CommitFailedException` handled by error handler.
- Wait until consumer 0 is unblocked.
- Expect rebalancing back to original state.
- The scenario is covered by [this test](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/SubscribedConsumerTestScenario.java#L64).  
 
**Watch out!** for potential side effects caused by prolonged processing of **message (A) in consumer 0**! The real reason for extended processing time might be of external nature, like long time connecting to an overloaded database, long GC pause, network issues. Eventually, when the real reason is gone, that forgotten message might have actually executed some code leaving unwanted side effects.

## Forwarding consumer/Prosumer

Consumes from one topic (`forwardingConsumerFirstLineTopic`), produces to another (`forwardingConsumerSecondLineTopic`) in the same transaction handled by  `transactionManager` bean ([here](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/forwarding/src/main/java/com/hclc/kafkainspring/consumers/forwarding/ForwardingConsumerProducerConfig.java#L38)).

### Simple forwarding (no exception)

All operations are successful, covered by [this test](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/ForwardingConsumerTestScenario.java#L22).

### Exception after forward

These two variants of *Exception after forward* demonstrate that `KafkaTransactionManager` indeed prevents from publishing a message in case of an exception (rollback). To make the point clear, `ForwardingConsumerFirstLine` first sends a message to the `forwardingConsumerSecondLineTopic` ([here](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/forwarding/src/main/java/com/hclc/kafkainspring/consumers/forwarding/ForwardingConsumerFirstLine.java#L40)) and only then throws an exception (if requested by a producer, [here](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/forwarding/src/main/java/com/hclc/kafkainspring/consumers/forwarding/ForwardingConsumerFirstLine.java#L41)).

Following [official Spring Kafka documentation](https://docs.spring.io/spring-kafka/docs/2.1.10.RELEASE/reference/html/_reference.html#_kafkatransactionmanager):

> You can use the `KafkaTransactionManager` with normal Spring transaction support (`@Transactional`, `TransactionTemplate` etc). If a transaction is active, any `KafkaTemplate` operations performed within the scope of the transaction will use the transaction’s `Producer`. The manager will commit or rollback the transaction depending on success or failure. The `KafkaTemplate` must be configured to use the same `ProducerFactory` as the transaction manager.

- First variant is covered by [this test](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/ForwardingConsumerTestScenario.java#L32):
  - exception is thrown only on first try,
  - not reaching 3 tries limit (configured [here](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/forwarding/src/main/java/com/hclc/kafkainspring/consumers/forwarding/ForwardingConsumerConsumerConfig.java#L50)), 
  - producer error handler ([ProducerListener](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/forwarding/src/main/java/com/hclc/kafkainspring/consumers/forwarding/ForwardingConsumerProducerListener.java#L20)) is invoked once,
  - consumer error handler is not invoked, 
  - a message is successfully forwarded to the `forwardingConsumerSecondLineTopic` the second time it is consumed by `ForwardingConsumerFirstLine`.
- Second variant is covered by [this test](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/ForwardingConsumerTestScenario.java#L44):
  - exception is thrown on each of three tries, eventually reaching three tries limit,
  - producer error handler ([ProducerListener](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/forwarding/src/main/java/com/hclc/kafkainspring/consumers/forwarding/ForwardingConsumerProducerListener.java#L20)) is invoked on each try,
  - consumer error handler is invoked, 
  - a message is not forwarded to the `forwardingConsumerSecondLineTopic`.
  
## Forwarding consumer/Prosumer with DB transaction

Consumes from one topic (`forwardingWithDbConsumerFirstLineTopic`), produces to another (`forwardingWithDbConsumerSecondLineTopic`) also writing to Postgres database in the same transaction.

Uses `ChainedTransactionManager` (in particular `ChainedKafkaTransactionManager`). Following [ChainedTransactionManager javadoc](https://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/transaction/ChainedTransactionManager.html):

> The configured instances will start transactions in the order given and commit/rollback in reverse order, which means **the `PlatformTransactionManager` most likely to break the transaction should be the *last* in the list** configured. A `PlatformTransactionManager` throwing an exception during commit will automatically cause the remaining transaction managers to roll back instead of committing.

The value that `ChainedKafkaTransactionManager` brings is that it forces exactly one `KafkaTransactionManager` in the chain and takes care of sending offsets. Following [official Spring Kafka documentation](https://docs.spring.io/spring-kafka/docs/2.1.10.RELEASE/reference/html/_reference.html#chained-transaction-manager):

> The `ChainedKafkaTransactionManager` was introduced in version 2.1.3. This is a subclass of `ChainedTransactionManager` that can have exactly one `KafkaTransactionManager`. Since it is a `KafkaAwareTransactionManager`, the container can send the offsets to the transaction in the same way as when the container is configured with a simple `KafkaTransactionManager`. This provides another mechanism for synchronizing transactions without having to send the offsets to the transaction in the listener code. **Chain your transaction managers in the desired order and provide the `ChainedTransactionManager` in the `ContainerProperties`**.

This is one of the possible implementations of **Best Efforts 1PC pattern**, described in [Distributed transactions in Spring, with and without XA](https://www.javaworld.com/article/2077963/open-source-tools/distributed-transactions-in-spring--with-and-without-xa.html?page=2) by David Syer.

**Watch out!** With Best Effort 1PC pattern, there is a risk of having **duplicates**. If database transaction commits and exception is thrown when committing to Kafka, a message will be redelivered and reprocessed. In case no additional check is done at the beginning of message processing, the same message might be saved to database for the second time.

### Simple forwarding (no exception)

All operations are successful, covered by [this test](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/ForwardingWithDbConsumerTestScenario.java#L24).

### Exception after forward

These two variants of *Exception after forward* demonstrate that `KafkaTransactionManager` indeed prevents from publishing a message in case of an exception (rollback). To make the point clear, `ForwardingWithDbConsumerFirstLine` first writes to database ([here](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/forwardingWithDb/src/main/java/com/hclc/kafkainspring/consumers/forwardingwithdb/ForwardingWithDbConsumerFirstLine.java#L48)) and sends a message to the `forwardingWithDbConsumerSecondLineTopic` ([here](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/forwardingWithDb/src/main/java/com/hclc/kafkainspring/consumers/forwardingwithdb/ForwardingWithDbConsumerFirstLine.java#L49)) and only then throws an exception (if requested by a producer, [here](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/forwardingWithDb/src/main/java/com/hclc/kafkainspring/consumers/forwardingwithdb/ForwardingWithDbConsumerFirstLine.java#L50)).

- First variant is covered by [this test](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/ForwardingWithDbConsumerTestScenario.java#L40):
  - exception is thrown only on first try,
  - not reaching 3 tries limit (configured [here](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/forwardingWithDb/src/main/java/com/hclc/kafkainspring/consumers/forwardingwithdb/ForwardingWithDbConsumerConsumerConfig.java#L50)), 
  - producer error handler ([ProducerListener](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/forwardingWithDb/src/main/java/com/hclc/kafkainspring/consumers/forwardingwithdb/ForwardingWithDbConsumerProducerListener.java#L20)) is invoked once,
  - database transaction is rolled back,
  - consumer error handler is not invoked, 
  - a message is successfully forwarded to the `forwardingWithDbConsumerSecondLineTopic` the second time it is consumed by `ForwardingWithDbConsumerFirstLine`,
  - a message is saved to the database on second try (this can be proved by observing one database identifier generated from a sequence being skipped, [here](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/ForwardingWithDbConsumerTestScenario.java#L53)).
- Second variant is covered by [this test](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/ForwardingWithDbConsumerTestScenario.java#L65):
  - exception is thrown on each of three tries, eventually reaching three tries limit,
  - producer error handler ([ProducerListener](https://github.com/bkaminnski/kafkainspring/blob/master/consumers/forwardingWithDb/src/main/java/com/hclc/kafkainspring/consumers/forwardingwithdb/ForwardingWithDbConsumerProducerListener.java#L20)) is invoked on each try,
  - consumer error handler is invoked, 
  - a message is not forwarded to the `forwardingWithDbConsumerSecondLineTopic`,
  - no message is saved to the database (verified [here](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/ForwardingWithDbConsumerTestScenario.java#L80))
  - database transaction is rolled back 3 times (verified [here](https://github.com/bkaminnski/kafkainspring/blob/master/integrationTests/src/test/java/com/hclc/kafkainspring/integrationtests/ForwardingWithDbConsumerTestScenario.java#L83)).
  
# Guarantees
  
There are couple of configuration parameters in Kafka that play a particular role when it comes to achieving highest possible guarantees (ordering, exactly-once delivery, transactional processing), or (when switched otherwise) highest possible throughput. This section presents most important parameters. Since there are still many, many other configuration parameters, always make sure to cross check with the latest Kafka documentation for [producers](https://kafka.apache.org/documentation/#producerconfigs) and [consumers](https://kafka.apache.org/documentation/#consumerconfigs) configuration. Also this series of Confluent blog posts explains a lot regarding transaction support and exactly-once delivery guarantees in Kafka: 
 - [part 1 - Exactly-once Semantics are Possible: Here’s How Kafka Does it](https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it/), 
 - [part 2 - Transactions in Apache Kafka](https://www.confluent.io/blog/transactions-apache-kafka/), 
 - [part 3 - Enabling Exactly-Once in Kafka Streams](https://www.confluent.io/blog/enabling-exactly-kafka-streams/).
 
## max.in.flight.requests.per.connection
 
**Producer** property.
 
### max.in.flight.requests.per.connection for ordering 
 
When set to 1, offers highest guarantee of ordering. Anything > 1 risks reordering in case of failed delivery (this makes sense only when retries are turned on on a producer with `retries` configuration property > 0).
 
Caution notice in `retries` documentation from `ProducerConfig` class, kafka client version 1.0.1:
 
>  Allowing retries without setting `max.in.flight.requests.per.connection` to 1 will potentially change the ordering of records because if two batches are sent to a single partition, and the first fails and is retried but the second succeeds, then the records in the second batch may appear first. 
 
Caution notice in [producer documentation](https://kafka.apache.org/documentation/#producerconfigs) for `max.in.flight.requests.per.connection`:
 
 > Note that if this setting is set to be greater than 1 and there are failed sends, there is a risk of message re-ordering due to retries (i.e., if retries are enabled).
 
### max.in.flight.requests.per.connection for duplicates
 
Before Kafka introduced exactly-once semantics in version 0.11, `max.in.flight.requests.per.connection` could have been also used to help avoiding duplicates (that could have happened in case of error when delivering ack from the broker back to producer provided that retries were turned on). Since `enable.idempotence` producer property is available, duplicates appearing in the process of producing messages can be avoided by setting `enable.idempotence` to `true`. And since version 1.0.0, when it comes to only *duplicates* protection, `max.in.flight.requests.per.connection` can be set to up to `5` provided that idempotence is enabled. Source of this piece of information is [here](https://kafka.apache.org/downloads):
 
> Since release 0.11.0, the idempotent producer (which is the producer used in the presence of a transaction, which of course is the producer we use for exactly-once processing) required max.in.flight.requests.per.connection to be equal to one. As anyone who has written or tested a wire protocol can attest, this put an upper bound on throughput. Thanks to KAFKA-5949, this can now be as large as five, relaxing the throughput constraint quite a bit.
 
## enable.idempotence

**Producer** property. Set to `true` to enable idempotence.
 
Following [part 1](https://www.confluent.io/blog/exactly-once-semantics-are-possible-heres-how-apache-kafka-does-it/) from the blog series: 
 
> The producer send operation is now idempotent. In the event of an error that causes a producer retry, the same message—which is still sent by the producer multiple times—will only be written to the Kafka log on the broker once.

## isolation.level

**Consumer** property. All messages produced to different topics can be now sent atomically. In order for a consumer to use this fact, `isolation.level` has to be set to `read_committed`.

## transactional.id

**Producer** property. This property is required for transactions to work and to protect from zombie messages (zombie fencing). 

From [part 2](https://www.confluent.io/blog/transactions-apache-kafka/):

> (...) in distributed environments, applications will crash or—worse!—temporarily lose connectivity to the rest of the system. Typically, new instances are automatically started to replace the ones which were deemed lost. Through this process, **we may have multiple instances processing the same input topics and writing to the same output topics**, causing **duplicate outputs** and violating the exactly once processing semantics. We call this the problem of “zombie instances.”

## processing.guarantee

**Stream** processing property (documented [here](https://kafka.apache.org/documentation/#streamsconfigs)). Setting `processing.guarantee` to `exactly_once` is a single configuration change that turns on transactions support and exactly-once guarantees in Kafka Streams. For more information check [part 3](https://www.confluent.io/blog/enabling-exactly-kafka-streams/) of the Confluent blog posts series. 