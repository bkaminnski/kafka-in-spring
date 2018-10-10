# Kafka in Spring

This project presents how to use Kafka in Spring.

## Subscribed Consumer

Uses `subscribe()` to subscribe to a topic. Requires group id for Kafka consumer.

## Assigned Consumer

Uses `assign()` to subscribe to individual partition. Does not require group id for Kafka consumer. Such consumer also does not need to care about new partition being added to a topic.

## Prosumer

`[Coming soon...]`

Consumes from one topic, produces to another.

## Prosumer synced with DB transaction

`[Coming soon...]`

Consumes from one topic, produces to another synchronizing with database transaction.
