/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server.controller;
import server.model.Conversation;
/**
 *
 * @author Relax2954
 */
/**
 * The server side controller. All calls to the server side model pass through here.
 */
public class Controller {
    private final Conversation conversation = new Conversation();

    /**
     * Appends the specified entry to the conversation.
     *
     * @param entry The entry to append.
     */
    public void appendEntry(String entry) {
        conversation.appendEntry(entry);
    }

    /**
     * @return All entries in the conversation, in the order they were entered.
     */
    public String[] getConversation() {
        return conversation.getConversation();
    }
}
