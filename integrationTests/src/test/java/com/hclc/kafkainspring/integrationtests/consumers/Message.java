package com.hclc.kafkainspring.integrationtests.consumers;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Message {

    private Long id;
    private String content;

    public Message() {
    }

    public Message(String content) {
        this.content = content;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long numberOfIdsSkippedComparedTo(Message another) {
        return max(abs(id - another.id) - 1, 0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message = (Message) o;

        if (!id.equals(message.id)) return false;
        return content.equals(message.content);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + content.hashCode();
        return result;
    }
}
